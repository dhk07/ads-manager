package whilter.ai.ads_manager.utility;

import jakarta.annotation.PostConstruct;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Value;
import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.entity.FacebookProfile;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.model.Dashboard;

import java.util.List;

@UtilityClass
public class Utils {

    public  CustomerRegistrationDto customerToCustomerDto(Customer customer, String facebookCallBackUrl) {
        CustomerRegistrationDto customerDto = new CustomerRegistrationDto();
        customerDto.setUserName(customer.getUserName());
        customerDto.setEmail(customer.getEmailId());
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        customerDto.setCompanyName(customer.getCompanyName());
        customerDto.setDashboardList(getDashboardDataList(customer, facebookCallBackUrl));
        return customerDto;
    }

    private List<Dashboard> getDashboardDataList(Customer customer, String facebookCallBackUrl) {
        FacebookProfile facebookProfile = customer.getFacebookProfile();
        if (facebookProfile != null) {
            Dashboard dashboard = new Dashboard(facebookCallBackUrl, facebookProfile.isLinkedStatus(), "Facebook", customer.getId());
            return List.of(dashboard);
        }
        return List.of();
    }
}
