package aws.masterslave.repository;

import aws.masterslave.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 비관적 락
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Product findByIdWithRedisson(@Param("id") Long id);
}
