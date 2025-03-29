package whilter.ai.ads_manager.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import whilter.ai.ads_manager.entity.Customer;
import whilter.ai.ads_manager.model.CustomerRegistrationDto;
import whilter.ai.ads_manager.service.CustomerService;
import whilter.ai.ads_manager.utility.Utils;

@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    //update customer details
    @PostMapping("/update")
    public String updateCustomerDetails(@Valid @ModelAttribute("customerDto") CustomerRegistrationDto customerDto,
                                        BindingResult bindingResult,
                                        HttpSession httpSession,
                                        Model model) {
        log.info("Inside updateCustomerDetails endpoint: ");
        try {
//            if (bindingResult.hasErrors()) {
//                model.addAttribute("customerDto", customerDto);
//                return "profile";
//            }
            customerService.updateCustomerDetails(customerDto);
            httpSession.setAttribute("existingCustomer", customerDto);
            model.addAttribute("existingCustomer", customerDto);
            log.info("Customer details updated successfully");
            return "dashboard";
        } catch (Exception e) {
            log.error("Error updating customer details: ", e);
            return "profile";
        }
    }

    @GetMapping("/profile")
    public String openEditProfilePage(Model model, HttpSession session) {
        log.info("Inside openEditProfilePage endpoint: ");
        CustomerRegistrationDto customerDto = (CustomerRegistrationDto) session.getAttribute("existingCustomer");
        model.addAttribute("customerDto", customerDto);
        return "profile";
    }


}
