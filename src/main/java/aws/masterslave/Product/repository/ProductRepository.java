package aws.masterslave.Product.repository;

import aws.masterslave.Product.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Redisson
     */
    @Query("select p from Product p where p.id = :id")
    Product findByIdWithRedisson(@Param("id") Long id);

    /**
     * 비관적 락
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Product findByIdWithPessimisticLock(@Param("id") Long id);

    @Query("select p from Product p where p.productName = :productName")
    Optional<Product> findByProductName(@Param("productName") String productName);
}
