package whilter.ai.ads_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import whilter.ai.ads_manager.entity.Customer;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUserName(String username);
    Optional<Customer> findByEmailId(String email);
    Optional<Customer> findByCompanyName(String providerCustomerId);
    boolean existsByUserName(String username);
    boolean existsByEmailId(String email);

    @Query(value = "SELECT * FROM customers WHERE user_name = :userName AND password = :password", nativeQuery = true)
    Customer validateCustomer(@Param("userName") String userName, @Param("password") String password);
}
