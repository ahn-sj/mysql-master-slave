package aws.masterslave.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seller;

    private String productName;

    private Integer stock;

    public Product(Long seller, String productName, Integer stock) {
        this.seller = seller;
        this.productName = productName;
        this.stock = stock;
    }

    public void decrease() {
        if (this.stock - 1 < 0) {
            throw new RuntimeException("Not Exist Stock Amount");
        }
        this.stock = stock - 1;
    }
}
