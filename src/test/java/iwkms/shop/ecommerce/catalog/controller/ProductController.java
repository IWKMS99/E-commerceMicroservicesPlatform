package iwkms.shop.ecommerce.catalog.controller;

import iwkms.shop.ecommerce.catalog.dto.ProductDto;
import iwkms.shop.ecommerce.catalog.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private ProductDto productDto;
    private UUID productId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        productId = UUID.randomUUID();
        productDto = new ProductDto(productId, "Test Product", "Desc", new BigDecimal("10.00"), 1L, "Category");
    }

    @Test
    void getAllProducts_shouldReturnProductList() throws Exception {
        when(productService.findAllProducts()).thenReturn(Collections.singletonList(productDto));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(productId.toString())))
                .andExpect(jsonPath("$[0].name", is("Test Product")));
    }

    @Test
    void getProductById_whenProductExists_shouldReturnProduct() throws Exception {
        when(productService.findProductById(productId)).thenReturn(productDto);

        mockMvc.perform(get("/api/v1/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(productId.toString())))
                .andExpect(jsonPath("$.name", is("Test Product")));
    }
}