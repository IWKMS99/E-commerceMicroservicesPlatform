package iwkms.shop.ecommerce.catalog.service;

import iwkms.shop.ecommerce.catalog.dto.CreateProductDto;
import iwkms.shop.ecommerce.catalog.dto.ProductDto;
import iwkms.shop.ecommerce.catalog.entity.Category;
import iwkms.shop.ecommerce.catalog.entity.Product;
import iwkms.shop.ecommerce.catalog.mapper.ProductMapper;
import iwkms.shop.ecommerce.catalog.repository.CategoryRepository;
import iwkms.shop.ecommerce.catalog.repository.ProductRepository;
import iwkms.shop.ecommerce.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private CreateProductDto createDto;
    private Category category;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() {
        Long categoryId = 1L;
        UUID productId = UUID.randomUUID();

        createDto = new CreateProductDto("Test Product", "Description", new BigDecimal("99.99"), categoryId);

        category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");

        product = new Product();
        product.setId(productId);
        product.setName(createDto.name());
        product.setCategory(category);
        product.setPrice(createDto.price());

        productDto = new ProductDto(productId, product.getName(), product.getDescription(), product.getPrice(), category.getId(), category.getName());
    }

    @Test
    void createProduct_whenCategoryExists_shouldCreateProduct() {
        when(categoryRepository.findById(createDto.categoryId())).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(productDto);

        ProductDto result = productService.createProduct(createDto);

        assertNotNull(result);
        assertEquals(productDto.id(), result.id());
        assertEquals("Test Product", result.name());
        verify(categoryRepository).findById(createDto.categoryId());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_whenCategoryNotFound_shouldThrowResourceNotFoundException() {
        when(categoryRepository.findById(createDto.categoryId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.createProduct(createDto);
        });
    }
}