package whilter.ai.ads_manager.service;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.entity.FacebookProfile;
import whilter.ai.ads_manager.entity.InstagramProfile;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.repository.CustomerRepository;
import whilter.ai.ads_manager.repository.FacebookProfileRepository;
import whilter.ai.ads_manager.repository.InstagramProfileRepository;
import whilter.ai.ads_manager.utility.PasswordValidator;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
public class CustomerService{

    private final CustomerRepository customerRepository;
    private final FacebookProfileRepository facebookProfileRepository;
    private final InstagramProfileRepository instagramProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(CustomerRepository customerRepository, FacebookProfileRepository facebookProfileRepository, InstagramProfileRepository instagramProfileRepository, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.facebookProfileRepository = facebookProfileRepository;
        this.instagramProfileRepository = instagramProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerNewCustomer(CustomerRegistrationDto registrationDto) {
        // Validate password
        if (!PasswordValidator.isValid(registrationDto.getPassword())) {
            throw new IllegalArgumentException(PasswordValidator.getPasswordValidationMessage());
        }

        // Check if username or email already exists
        if (customerRepository.existsByUserName(registrationDto.getUserName())) {
            throw new RuntimeException("Username already exists");
        }
        if (customerRepository.existsByEmailId(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setUserName(registrationDto.getUserName());
        customer.setEmailId(registrationDto.getEmail());
        customer.setFirstName(registrationDto.getFirstName());
        customer.setLastName(registrationDto.getLastName());
        customer.setPhoneNumber(registrationDto.getPhoneNumber());
        customer.setCreatedAt(Instant.now());
        customer.setCompanyName(registrationDto.getCompanyName());
        // Encode and save password
        customer.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        Customer addedCustomer = customerRepository.save(customer);
        log.info("Customer added successfully: {}", addedCustomer);

        FacebookProfile facebookProfile = new FacebookProfile();
        facebookProfile.setCustomer(addedCustomer);
        facebookProfile.setLinkedStatus(false);
        facebookProfileRepository.save(facebookProfile);
        log.info("Facebook profile added successfully: {}", facebookProfile);

        InstagramProfile instagramProfile = new InstagramProfile();
        instagramProfile.setCustomer(addedCustomer);
        instagramProfile.setLinkedStatus(false);
        instagramProfileRepository.save(instagramProfile);
        log.info("Instagram profile added successfully: {}", instagramProfile);
    }
//
//    public Optional<Customer> loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        return customerRepository.findByUserName(username);
//    }

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

    public boolean validateCustomer(String enteredPassword, String password) {

        return passwordEncoder.matches(enteredPassword, password);
    }

    public void updateCustomerDetails(@Valid CustomerRegistrationDto customerDto) {
        customerRepository.findByUserName(customerDto.getUserName())
                .ifPresentOrElse(customer -> {
                    customer.setEmailId(customerDto.getEmail());
                    customer.setFirstName(customerDto.getFirstName());
                    customer.setLastName(customerDto.getLastName());
                    customer.setPhoneNumber(customerDto.getPhoneNumber());
                    customer.setCompanyName(customerDto.getCompanyName());
                    customerRepository.save(customer);
                }, () -> {
                    throw new UsernameNotFoundException("Customer not found");
                });
    }
}
