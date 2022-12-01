package aws.masterslave.service;

import aws.masterslave.domain.Product;
import aws.masterslave.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final RedissonClient redissonClient;
    private final ProductRepository productRepository;

    public Product productOrderRedisson(Long productId) {
        RLock lock = redissonClient.getLock(productId.toString());

        Product product;

        try {
            // 몇 초동안 점유할 것인지에 대한 설정
            boolean available = lock.tryLock(10, 5, TimeUnit.SECONDS);

            if(!available) {
                System.out.println("lock 획득 실패");
                throw new RuntimeException("락 획득 실패");
            }

            // lock 획득 성공
            product = createOrderWithRedisson(productId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            // 락 해제
            lock.unlock();
        }
        return product;
    }

    @Transactional
    protected Product createOrderWithRedisson(Long productId) {
        Product findProduct = productRepository.findByIdWithRedisson(productId);

        findProduct.decrease();
        productRepository.save(findProduct);
        System.out.println("findProduct.getStock() = " + findProduct.getStock());

        return findProduct;
    }

    @Transactional
    public Product productOrderWithPessimisticLock(Long productId) {
        Product findProduct = productRepository.findByIdWithPessimisticLock(productId);
        findProduct.decrease();

        return findProduct;
    }

    @Transactional
    public Product create() {
        return productRepository.save(new Product(100L, "상품", 1000));
    }

    @Transactional(readOnly = true)
    public Product read(String productName) {
        return productRepository.findByProductName(productName).orElseThrow();
    }

    @Transactional
    public void delete() {
        productRepository.deleteAll();
    }
}
