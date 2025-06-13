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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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

    @Test
    void findProductById_whenProductNotFound_shouldThrowResourceNotFoundException() {
        UUID nonExistentId = UUID.randomUUID();
        when(productRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findProductById(nonExistentId);
        });
    }

    @Test
    void findAllProducts_shouldReturnListOfProductDtos() {
        when(productRepository.findAll()).thenReturn(Collections.singletonList(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        List<ProductDto> result = productService.findAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDto, result.getFirst());
        verify(productRepository).findAll();
    }

    @Test
    void findProductsByCategoryId_whenCategoryExists_shouldReturnProductList() {
        Long categoryId = 1L;
        when(categoryRepository.existsById(categoryId)).thenReturn(true);
        when(productRepository.findByCategoryId(categoryId)).thenReturn(Collections.singletonList(product));
        when(productMapper.toDto(product)).thenReturn(productDto);

        List<ProductDto> result = productService.findProductsByCategoryId(categoryId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(productDto, result.getFirst());
        verify(categoryRepository).existsById(categoryId);
        verify(productRepository).findByCategoryId(categoryId);
    }

    @Test
    void findProductsByCategoryId_whenCategoryDoesNotExist_shouldThrowResourceNotFoundException() {
        Long nonExistentCategoryId = 99L;
        when(categoryRepository.existsById(nonExistentCategoryId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findProductsByCategoryId(nonExistentCategoryId);
        });

        verify(productRepository, never()).findByCategoryId(nonExistentCategoryId);
    }
}