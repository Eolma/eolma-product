package com.eolma.product.domain.service;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import com.eolma.product.domain.model.*;
import com.eolma.product.domain.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    private Product createTestProduct() {
        return Product.builder()
                .sellerId("seller-1")
                .title("Test Product")
                .description("Test Description")
                .category(Category.ELECTRONICS)
                .conditionGrade(ConditionGrade.GOOD)
                .startingPrice(100000L)
                .minBidUnit(1000L)
                .endType(EndType.TIME)
                .durationHours(24)
                .build();
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("존재하는 상품 조회 시 상품을 반환한다")
        void returnProduct_whenExists() {
            Product product = createTestProduct();
            given(productRepository.findById(1L)).willReturn(Optional.of(product));

            Product found = productService.findById(1L);

            assertThat(found).isEqualTo(product);
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회 시 PRODUCT_NOT_FOUND 예외를 던진다")
        void throwException_whenNotExists() {
            given(productRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.findById(999L))
                    .isInstanceOf(EolmaException.class)
                    .satisfies(ex -> {
                        EolmaException e = (EolmaException) ex;
                        assertThat(e.getErrorType()).isEqualTo(ErrorType.PRODUCT_NOT_FOUND);
                    });
        }
    }

    @Nested
    @DisplayName("findProducts")
    class FindProducts {

        private final Pageable pageable = PageRequest.of(0, 20);

        @Test
        @DisplayName("category와 status 모두 주어지면 둘 다로 필터링한다")
        void filterByCategoryAndStatus() {
            Page<Product> page = new PageImpl<>(List.of(createTestProduct()));
            given(productRepository.findByCategoryAndStatus(Category.ELECTRONICS, ProductStatus.ACTIVE, pageable))
                    .willReturn(page);

            Page<Product> result = productService.findProducts(Category.ELECTRONICS, ProductStatus.ACTIVE, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(productRepository).findByCategoryAndStatus(Category.ELECTRONICS, ProductStatus.ACTIVE, pageable);
        }

        @Test
        @DisplayName("category만 주어지면 category로 필터링한다")
        void filterByCategory() {
            Page<Product> page = new PageImpl<>(List.of(createTestProduct()));
            given(productRepository.findByCategory(Category.ELECTRONICS, pageable)).willReturn(page);

            Page<Product> result = productService.findProducts(Category.ELECTRONICS, null, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(productRepository).findByCategory(Category.ELECTRONICS, pageable);
        }

        @Test
        @DisplayName("status만 주어지면 status로 필터링한다")
        void filterByStatus() {
            Page<Product> page = new PageImpl<>(List.of(createTestProduct()));
            given(productRepository.findByStatus(ProductStatus.ACTIVE, pageable)).willReturn(page);

            Page<Product> result = productService.findProducts(null, ProductStatus.ACTIVE, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(productRepository).findByStatus(ProductStatus.ACTIVE, pageable);
        }

        @Test
        @DisplayName("필터 없이 호출하면 전체 조회한다")
        void findAll_whenNoFilter() {
            Page<Product> page = new PageImpl<>(List.of(createTestProduct()));
            given(productRepository.findAll(pageable)).willReturn(page);

            Page<Product> result = productService.findProducts(null, null, pageable);

            assertThat(result.getContent()).hasSize(1);
            verify(productRepository).findAll(pageable);
        }
    }
}
