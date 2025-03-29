package whilter.ai.ads_manager.utility;

import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;

public class Utils {


    public static CustomerRegistrationDto customerToCustomerDto(Customer customer) {
        CustomerRegistrationDto customerDto = new CustomerRegistrationDto();
        customerDto.setUserName(customer.getUserName());
        customerDto.setEmail(customer.getEmailId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setCompanyName(customer.getCompanyName());
        return customerDto;
    }
}
