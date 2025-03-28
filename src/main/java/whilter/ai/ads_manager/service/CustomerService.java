package whilter.ai.ads_manager.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.repository.CustomerRepository;
import whilter.ai.ads_manager.utility.PasswordValidator;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CustomerService implements UserDetailsService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Customer registerNewCustomer(CustomerRegistrationDto registrationDto) {
        // Validate password
        if (!PasswordValidator.isValid(registrationDto.getPassword())) {
            throw new IllegalArgumentException(PasswordValidator.getPasswordValidationMessage());
        }

        // Check if username or email already exists
        if (customerRepository.existsByUserName(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (customerRepository.existsByEmailId(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setUserName(registrationDto.getUsername());
        customer.setEmailId(registrationDto.getEmail());
        customer.setFirstName(registrationDto.getFirstName());
        customer.setLastName(registrationDto.getLastName());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());

        // Encode and save password
        customer.setPassword(passwordEncoder.encode(registrationDto.getPassword()));

        return customerRepository.save(customer);
    }

    @Transactional
    public Customer processOAuthPostLogin(String username, String email, String providerCustomerId, String provider) {
        Optional<Customer> existCustomer = customerRepository.findByEmailId(email);

        if (existCustomer.isPresent()) {
            // Update existing customer with provider details
            Customer customer = existCustomer.get();
            customer.setProviderName(provider);
//            customer.setProviderCustomerId(providerCustomerId);
            return customerRepository.save(customer);
        } else {
            // Create new customer
            Customer newCustomer = new Customer();
            newCustomer.setUserName(username);
            newCustomer.setEmailId(email);
            newCustomer.setProviderName(provider);
//            newCustomer.setProviderCustomerId(providerCustomerId);

            // Generate a random password for OAuth customers
            newCustomer.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));

            return customerRepository.save(newCustomer);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        return new org.springframework.security.core.userdetails.User(
                customer.getUserName(),
                customer.getPassword(),
                new ArrayList<>()
        );
    }

    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUserName(username);
    }

    // Method to change password
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        Customer customer = customerRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found"));

        // Validate old password
        if (!passwordEncoder.matches(oldPassword, customer.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password
        if (!PasswordValidator.isValid(newPassword)) {
            throw new IllegalArgumentException(PasswordValidator.getPasswordValidationMessage());
        }

        // Encode and save new password
        customer.setPassword(passwordEncoder.encode(newPassword));
        customerRepository.save(customer);
    }

    public boolean validateCustomer(String userName, String password) {

        Customer customer = customerRepository.validateCustomer(userName, password);
        return customer != null;
    }
}
