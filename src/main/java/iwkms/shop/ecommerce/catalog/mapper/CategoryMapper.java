package iwkms.shop.ecommerce.catalog.mapper;

import iwkms.shop.ecommerce.catalog.dto.CategoryDto;
import iwkms.shop.ecommerce.catalog.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toDto(Category category);
    Category toEntity(CategoryDto categoryDto);
}