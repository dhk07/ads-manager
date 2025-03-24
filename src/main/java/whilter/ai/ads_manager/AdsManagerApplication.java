package whilter.ai.ads_manager;

import io.swagger.v3.oas.models.annotations.OpenAPI30;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@OpenAPI30
//@ComponentScan(basePackages = "whilter.ai.ads_manager.*")
@EnableFeignClients
public class AdsManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdsManagerApplication.class, args);
	}
}
