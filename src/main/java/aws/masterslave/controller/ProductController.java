package aws.masterslave.controller;

import aws.masterslave.domain.Product;
import aws.masterslave.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;

    @PostMapping("/order/{productId}")
    public ResponseDto CreatedOrder(@PathVariable Long productId) {
        RLock lock = redissonClient.getLock(productId.toString());

        ResponseDto responseDto;
        
        try {
            boolean available = lock.tryLock(5, 2, TimeUnit.SECONDS);

            if (!available) {
                return null;
            }
            responseDto = new ResponseDto(productRepository.findByIdWithRedisson(productId));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        return responseDto;
    }

    @AllArgsConstructor
    static class ResponseDto {
        private Long seller;
        private String productName;
        private Integer stock;

        public ResponseDto(Product product) {
            seller = product.getSeller();
            productName = product.getProductName();
            stock = product.getStock();
        }
    }
}
