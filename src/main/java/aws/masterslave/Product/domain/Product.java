package aws.masterslave.Product.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Data
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seller;

    private String productName;

    private Integer stock;

    public Product(Long seller, String productName, Integer stock) {
        this.seller = seller;
        this.productName = productName;
        this.stock = stock;
    }

    public Integer decrease() {
        if (this.stock - 1 < 0) {
            System.out.println("재고 없음");
            return 0;
        }
        this.stock = this.stock - 1;
        return this.stock;
    }
}
