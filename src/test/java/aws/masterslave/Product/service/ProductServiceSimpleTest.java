package aws.masterslave.Product.service;

import org.junit.jupiter.api.AfterEach;
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
    public void start() {
        // Product => 100L, "상품", 1000
        productService.create();
    }

    @AfterEach
    public void end() {
        productService.delete();
    }

    @Test
    @DisplayName("Redisson Simple Test >> 재고: 996")
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
    @DisplayName("PessimisticLock Simple Test >> 재고: 996")
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

    @Test
    @DisplayName("ONLY SLAVE")
    public void SLAVE_읽기() throws Exception {
        for (int i = 1; i <= 10; i++) {
            productService.read("상품").getId();
            System.out.println("SLAVE = " + i);
        }
    }
}