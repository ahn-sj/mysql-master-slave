package aws.masterslave.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @BeforeEach
    public void start() {
        // Product => 100L, "상품", 1000
        productService.create();
    }

    @AfterEach
    public void end() {
        // Product => 100L, "상품", 1000
        productService.delete();
    }

    @Test
    @DisplayName("Redisson Test >> 재고: 100")
    public void 레디슨() throws Exception {
        // given
        Long findProductId = productService.read("상품").getId();

        final int PRODUCT_STOCK = 1000;
        final int THREAD_COUNT = 900;
        final int EXPECTED = PRODUCT_STOCK - THREAD_COUNT; // 1000 - 900 = 100

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    productService.productOrderRedisson(findProductId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        assertThat(productService.read("상품").getStock()).isEqualTo(EXPECTED);
    }

    @Test
    @DisplayName("PessimisticLock Test >> 재고: 100")
    public void 비관적_락() throws Exception {
        // given
        Long findProductId = productService.read("상품").getId();

        final int PRODUCT_STOCK = 1000;
        final int THREAD_COUNT = 900;
        final int EXPECTED = PRODUCT_STOCK - THREAD_COUNT; // 1000 - 900 = 100

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    productService.productOrderWithPessimisticLock(findProductId);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        // then
        Integer stock = productService.read("상품").getStock();
        System.out.println("stock = " + stock);

        assertThat(stock).isEqualTo(EXPECTED);
    }
}