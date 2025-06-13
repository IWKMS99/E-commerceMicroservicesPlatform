package iwkms.shop.ecommerce.catalog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import iwkms.shop.ecommerce.catalog.dto.CategoryDto;
import iwkms.shop.ecommerce.catalog.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCategory_whenUserIsAdmin_shouldReturnCreated() throws Exception {
        CategoryDto inputDto = new CategoryDto(null, "New Category");
        CategoryDto outputDto = new CategoryDto(1L, "New Category");
        when(categoryService.createCategory(any(CategoryDto.class))).thenReturn(outputDto);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createCategory_whenUserIsUser_shouldReturnForbidden() throws Exception {
        CategoryDto inputDto = new CategoryDto(null, "Forbidden Category");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    void createCategory_whenUserIsAnonymous_shouldReturnUnauthorized() throws Exception {
        CategoryDto inputDto = new CategoryDto(null, "Anonymous Category");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isUnauthorized());
    }
}