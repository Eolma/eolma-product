package com.eolma.product.domain.model;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    private Product createDraftProduct() {
        return Product.builder()
                .sellerId(1L)
                .title("Test Product")
                .category(Category.ELECTRONICS)
                .conditionGrade(ConditionGrade.GOOD)
                .startingPrice(100000L)
                .minBidUnit(1000L)
                .endType(EndType.TIME)
                .durationHours(24)
                .build();
    }

    @Nested
    @DisplayName("activate")
    class Activate {

        @Test
        @DisplayName("DRAFT 상품은 ACTIVE로 변경된다")
        void activateFromDraft() {
            Product product = createDraftProduct();

            product.activate();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
        }

        @Test
        @DisplayName("DRAFT가 아닌 상품은 활성화할 수 없다")
        void cannotActivateNonDraft() {
            Product product = createDraftProduct();
            product.activate(); // ACTIVE

            assertThatThrownBy(product::activate)
                    .isInstanceOf(EolmaException.class)
                    .satisfies(ex -> assertThat(((EolmaException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_REQUEST));
        }
    }

    @Nested
    @DisplayName("cancel")
    class Cancel {

        @Test
        @DisplayName("DRAFT 상품은 취소할 수 있다")
        void cancelFromDraft() {
            Product product = createDraftProduct();

            product.cancel();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.CANCELLED);
        }

        @Test
        @DisplayName("ACTIVE 상품은 취소할 수 있다")
        void cancelFromActive() {
            Product product = createDraftProduct();
            product.activate();

            product.cancel();

            assertThat(product.getStatus()).isEqualTo(ProductStatus.CANCELLED);
        }

        @Test
        @DisplayName("IN_AUCTION 상품은 취소할 수 없다")
        void cannotCancelInAuction() {
            Product product = createDraftProduct();
            product.activate();
            product.changeStatus(ProductStatus.IN_AUCTION);

            assertThatThrownBy(product::cancel)
                    .isInstanceOf(EolmaException.class)
                    .satisfies(ex -> assertThat(((EolmaException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_REQUEST));
        }
    }

    @Nested
    @DisplayName("validateOwnership")
    class ValidateOwnership {

        @Test
        @DisplayName("본인 상품이면 예외가 발생하지 않는다")
        void noException_whenOwner() {
            Product product = createDraftProduct();

            product.validateOwnership(1L);
        }

        @Test
        @DisplayName("본인이 아닌 상품이면 FORBIDDEN 예외가 발생한다")
        void throwForbidden_whenNotOwner() {
            Product product = createDraftProduct();

            assertThatThrownBy(() -> product.validateOwnership(999L))
                    .isInstanceOf(EolmaException.class)
                    .satisfies(ex -> assertThat(((EolmaException) ex).getErrorType())
                            .isEqualTo(ErrorType.FORBIDDEN));
        }
    }

    @Nested
    @DisplayName("validateModifiable")
    class ValidateModifiable {

        @Test
        @DisplayName("DRAFT 상품은 수정 가능하다")
        void draftIsModifiable() {
            Product product = createDraftProduct();

            product.validateModifiable();
        }

        @Test
        @DisplayName("ACTIVE 상품은 수정할 수 없다")
        void activeIsNotModifiable() {
            Product product = createDraftProduct();
            product.activate();

            assertThatThrownBy(product::validateModifiable)
                    .isInstanceOf(EolmaException.class)
                    .satisfies(ex -> assertThat(((EolmaException) ex).getErrorType())
                            .isEqualTo(ErrorType.INVALID_REQUEST));
        }
    }

    @Test
    @DisplayName("minBidUnit이 null이면 기본값 1000이 설정된다")
    void defaultMinBidUnit() {
        Product product = Product.builder()
                .sellerId(1L)
                .title("Test")
                .category(Category.ELECTRONICS)
                .conditionGrade(ConditionGrade.GOOD)
                .startingPrice(100000L)
                .minBidUnit(null)
                .endType(EndType.TIME)
                .durationHours(24)
                .build();

        assertThat(product.getMinBidUnit()).isEqualTo(1000L);
    }
}
