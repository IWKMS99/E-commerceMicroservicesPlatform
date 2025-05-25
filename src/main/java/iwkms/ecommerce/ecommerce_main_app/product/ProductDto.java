package iwkms.ecommerce.ecommerce_main_app.product;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDto {
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
}
