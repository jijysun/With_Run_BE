package UMC_8th.With_Run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class WithRunApplication {

	public static void main(String[] args) {
		SpringApplication.run(WithRunApplication.class, args);
	}

}
