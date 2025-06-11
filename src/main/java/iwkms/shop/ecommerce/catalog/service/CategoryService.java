package iwkms.shop.ecommerce.catalog.service;

import iwkms.shop.ecommerce.catalog.dto.CategoryDto;
import iwkms.shop.ecommerce.catalog.entity.Category;
import iwkms.shop.ecommerce.catalog.mapper.CategoryMapper;
import iwkms.shop.ecommerce.catalog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }

    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.findByName(categoryDto.name()).isPresent()) {
            throw new DataIntegrityViolationException("Category with this name '" + categoryDto.name() + "' already exists");
        }
        Category category = new Category();
        category.setName(categoryDto.name());
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }
}