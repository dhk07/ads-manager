package whilter.ai.ads_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import whilter.ai.ads_manager.entity.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserName(String username);
    Optional<Customer> findByEmailId(String email);
    Optional<Customer> findByProviderName(String providerCustomerId);
    boolean existsByUserName(String username);
    boolean existsByEmailId(String email);
}
