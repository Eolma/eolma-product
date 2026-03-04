package com.eolma.product.application.usecase;

import com.eolma.common.exception.EolmaException;
import com.eolma.common.exception.ErrorType;
import com.eolma.product.application.port.out.EventPublisher;
import com.eolma.product.domain.model.*;
import com.eolma.product.domain.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ActivateProductUseCaseTest {

    @Mock
    private ProductService productService;

    @Mock
    private EventPublisher eventPublisher;

    @InjectMocks
    private ActivateProductUseCase activateProductUseCase;

    private Product createDraftProduct(Long sellerId) {
        return Product.builder()
                .sellerId(sellerId)
                .title("Test Product")
                .category(Category.ELECTRONICS)
                .conditionGrade(ConditionGrade.GOOD)
                .startingPrice(100000L)
                .minBidUnit(1000L)
                .endType(EndType.TIME)
                .endValue("24h")
                .build();
    }

    @Test
    @DisplayName("본인 DRAFT 상품을 활성화하면 이벤트가 발행된다")
    void activateProduct_publishesEvent() {
        Product product = createDraftProduct(1L);
        given(productService.findById(1L)).willReturn(product);
        given(productService.save(any())).willReturn(product);

        activateProductUseCase.execute(1L, 1L);

        verify(productService).save(product);
        verify(eventPublisher).publish(any());
    }

    @Test
    @DisplayName("다른 사용자의 상품은 활성화할 수 없다")
    void cannotActivateOthersProduct() {
        Product product = createDraftProduct(1L);
        given(productService.findById(1L)).willReturn(product);

        assertThatThrownBy(() -> activateProductUseCase.execute(999L, 1L))
                .isInstanceOf(EolmaException.class)
                .satisfies(ex -> {
                    EolmaException e = (EolmaException) ex;
                    assert e.getErrorType() == ErrorType.FORBIDDEN;
                });

        verify(eventPublisher, never()).publish(any());
    }
}
