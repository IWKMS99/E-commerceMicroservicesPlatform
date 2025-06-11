package iwkms.shop.ecommerce.catalog.mapper;

import iwkms.shop.ecommerce.catalog.dto.ProductDto;
import iwkms.shop.ecommerce.catalog.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    ProductDto toDto(Product product);
}