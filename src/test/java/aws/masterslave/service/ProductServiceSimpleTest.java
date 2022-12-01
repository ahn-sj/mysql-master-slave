package aws.masterslave.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceSimpleTest {

    @Autowired
    private ProductService productService;

    @BeforeEach
    public void init() {
        // Product => 100L, "상품", 1000
        productService.create();
    }

    @Test
    @DisplayName("Redisson Test >> ")
    public void 레디슨() throws Exception {
        // given
        Long findProductId = productService.read("상품").getId();

        // when
        productService.productOrderRedisson(findProductId);
        productService.productOrderRedisson(findProductId);
        productService.productOrderRedisson(findProductId);
        productService.productOrderRedisson(findProductId);

        // then
        assertThat(productService.read("상품").getStock()).isEqualTo(1000 - 4);
    }

    @Test
    @DisplayName("PessimisticLock Test >> ")
    public void 비관적_락() throws Exception {
        // given
        Long findProductId = productService.read("상품").getId();

        // when
        productService.productOrderWithPessimisticLock(findProductId);
        productService.productOrderWithPessimisticLock(findProductId);
        productService.productOrderWithPessimisticLock(findProductId);
        productService.productOrderWithPessimisticLock(findProductId);

        // then
        assertThat(productService.read("상품").getStock()).isEqualTo(1000 - 4);
    }
}