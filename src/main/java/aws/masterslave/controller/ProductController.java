package aws.masterslave.controller;

import aws.masterslave.domain.Product;
import aws.masterslave.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostConstruct
    public void init() {
        productService.create();
    }

    @PostMapping("/v1/order/{productId}")
    public ResponseDto CreatedOrderWithRedisson(@PathVariable Long productId) {
        return new ResponseDto(productService.productOrderRedisson(productId));
    }

    @PostMapping("/v2/order/{productId}")
    public ResponseDto CreatedOrderWithPessimisticLock(@PathVariable Long productId) {
        return new ResponseDto(productService.productOrderWithPessimisticLock(productId));
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
