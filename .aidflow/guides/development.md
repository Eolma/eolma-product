# eolma-product Development Guide

## 서비스 개요

상품 등록/관리를 담당하는 서비스. 경매 트리거(상품 활성화)와 경매 결과에 따른 상품 상태 관리.

- 포트: 8082
- 프레임워크: Spring MVC (Servlet)
- DB: PostgreSQL (`eolma_product`), JPA
- 캐시: Redis + Redisson

## 핵심 도메인

### Product 엔티티

| 필드 | 타입 | 설명 |
|------|------|------|
| id | Long | PK |
| sellerId | Long | 판매자 회원 ID |
| title | String | 상품 제목 |
| description | String | 상품 설명 (TEXT) |
| category | Category | 상품 카테고리 (Enum) |
| conditionGrade | ConditionGrade | 상품 상태 등급 |
| startingPrice | Long | 시작가 (원) |
| instantPrice | Long | 즉시 구매가 (nullable) |
| reservePrice | Long | 최저 낙찰가 (nullable) |
| minBidUnit | Long | 최소 입찰 단위 |
| endType | EndType | 경매 종료 방식 |
| endValue | String | 종료 조건 값 (시간/횟수) |
| status | ProductStatus | 상품 상태 |
| imageUrls | List<String> | 이미지 URL 목록 (JSON) |

### 상태 머신

```
DRAFT → ACTIVE → IN_AUCTION → SOLD
  ↓                    ↓
CANCELLED         ACTIVE (유찰 시 복원)
```

- **DRAFT**: 초기 등록 상태, 수정/삭제 가능
- **ACTIVE**: 경매 대기, PRODUCT_ACTIVATED 이벤트 발행됨
- **IN_AUCTION**: 경매 진행 중 (AUCTION_STARTED 수신)
- **SOLD**: 낙찰 완료 (AUCTION_COMPLETED 수신)
- **CANCELLED**: 판매 취소

### Enum 값

**Category:** ELECTRONICS, FASHION, BOOKS, SPORTS, HOME, BEAUTY, TOYS, AUTO, COLLECTIBLES, OTHER

**ConditionGrade:** NEW, LIKE_NEW, EXCELLENT, GOOD, FAIR, POOR

**EndType:** TIME(시간제), BID_COUNT(입찰횟수제), COMBINED(복합)

## API 엔드포인트

| 메서드 | 경로 | 인증 | 설명 |
|--------|------|------|------|
| POST | `/api/v1/products` | O | 상품 등록 (DRAFT) |
| GET | `/api/v1/products/{id}` | X | 상품 상세 조회 |
| GET | `/api/v1/products` | X | 상품 목록 (카테고리/상태 필터) |
| GET | `/api/v1/products/me` | O | 내 상품 목록 |
| PUT | `/api/v1/products/{id}` | O | 상품 수정 (DRAFT만) |
| DELETE | `/api/v1/products/{id}` | O | 상품 삭제 (DRAFT만) |
| POST | `/api/v1/products/{id}/activate` | O | 상품 활성화 (경매 시작) |
| POST | `/api/v1/products/{id}/cancel` | O | 상품 취소 |

## UseCase 목록

| UseCase | 설명 | 이벤트 |
|---------|------|--------|
| RegisterProductUseCase | 상품 등록 (DRAFT) | - |
| ActivateProductUseCase | DRAFT->ACTIVE, 경매 트리거 | PRODUCT_ACTIVATED 발행 |
| CancelProductUseCase | 상품 취소 | PRODUCT_CANCELLED 발행 |
| HandleAuctionEventUseCase | 경매 이벤트에 따른 상태 전환 | - |

## Kafka 이벤트

**발행 (`eolma.product.events`):**
- `PRODUCT_ACTIVATED`: 상품 활성화 시 -> auction 서비스가 경매 생성
- `PRODUCT_CANCELLED`: 상품 취소 시

**수신 (`eolma.auction.events`):**
- `AUCTION_STARTED` -> 상품 상태 IN_AUCTION
- `AUCTION_COMPLETED` -> 상품 상태 SOLD
- `AUCTION_FAILED` -> 상품 상태 ACTIVE (복원)

AuctionEventConsumer에서 IdempotencyChecker를 사용하여 중복 이벤트 방지.

## 주의사항

- 상품 수정/삭제는 DRAFT 상태에서만 가능
- 상품 활성화는 판매자 본인만 가능 (sellerId 검증)
- 이미지 URL은 JSON 배열로 PostgreSQL에 저장 (`@JdbcTypeCode(SqlTypes.JSON)`)
- 가격은 모두 Long 타입 (원 단위, 소수점 없음)
