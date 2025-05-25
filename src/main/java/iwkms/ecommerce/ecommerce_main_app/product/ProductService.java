package iwkms.ecommerce.ecommerce_main_app.product;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
    }
    
    @Transactional
    public Product createProduct(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setImageUrl(productDto.getImageUrl());
        return productRepository.save(product);
    }

    @Transactional
    public Optional<Product> updateProduct(Long id, ProductDto productDto) {
        return productRepository.findById(id)
            .map(existingProduct -> {
                existingProduct.setName(productDto.getName());
                existingProduct.setDescription(productDto.getDescription());
                existingProduct.setPrice(productDto.getPrice());
                existingProduct.setImageUrl(productDto.getImageUrl());
                return productRepository.save(existingProduct);
            });
    }

    @Transactional
    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
    
}
