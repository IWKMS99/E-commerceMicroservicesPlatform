package iwkms.shop.ecommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
        "iwkms.shop.ecommerce.user.repository",
        "iwkms.shop.ecommerce.catalog.repository",
        "iwkms.shop.ecommerce.order.repository",
        "iwkms.shop.ecommerce.inventory.repository",
        "iwkms.shop.ecommerce.payment.repository"
})
public class JpaConfig {
}