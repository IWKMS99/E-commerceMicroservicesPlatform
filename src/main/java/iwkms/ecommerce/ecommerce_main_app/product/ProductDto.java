package iwkms.ecommerce.ecommerce_main_app.product;

import java.math.BigDecimal;

import lombok.Data;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class ProductDto {
    @NotBlank(message = "Name is mandatory")
    @Size(max = 255, message = "Name can be at most 255 characters")
    private String name;

    @NotBlank(message = "Description is mandatory")
    @Size(max = 1000, message = "Description can be at most 1000 characters")
    private String description;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotBlank(message = "Image URL is mandatory")
    @Size(max = 500, message = "Image URL can be at most 500 characters")
    private String imageUrl;
}
