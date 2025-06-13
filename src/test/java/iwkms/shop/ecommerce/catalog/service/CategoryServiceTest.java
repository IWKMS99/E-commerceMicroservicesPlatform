package iwkms.shop.ecommerce.catalog.service;

import iwkms.shop.ecommerce.catalog.dto.CategoryDto;
import iwkms.shop.ecommerce.catalog.entity.Category;
import iwkms.shop.ecommerce.catalog.mapper.CategoryMapper;
import iwkms.shop.ecommerce.catalog.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void findAll_shouldReturnListOfCategoryDtos() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        CategoryDto categoryDto = new CategoryDto(1L, "Electronics");

        when(categoryRepository.findAll()).thenReturn(Collections.singletonList(category));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(categoryDto);

        List<CategoryDto> result = categoryService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).name());
    }

    @Test
    void createCategory_whenNameIsUnique_shouldCreateAndReturnCategoryDto() {
        CategoryDto inputDto = new CategoryDto(null, "Books");

        Category categoryToSave = new Category();
        categoryToSave.setName("Books");

        Category savedCategory = new Category();
        savedCategory.setId(2L);
        savedCategory.setName("Books");

        CategoryDto outputDto = new CategoryDto(2L, "Books");

        when(categoryRepository.findByName("Books")).thenReturn(java.util.Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);
        when(categoryMapper.toDto(any(Category.class))).thenReturn(outputDto);

        CategoryDto result = categoryService.createCategory(inputDto);

        assertNotNull(result);
        assertEquals(2L, result.id());
        assertEquals("Books", result.name());
    }

    @Test
    void createCategory_whenNameAlreadyExists_shouldThrowException() {
        CategoryDto inputDto = new CategoryDto(null, "Books");
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Books");

        when(categoryRepository.findByName("Books")).thenReturn(java.util.Optional.of(existingCategory));

        assertThrows(DataIntegrityViolationException.class, () -> {
            categoryService.createCategory(inputDto);
        });
    }
}