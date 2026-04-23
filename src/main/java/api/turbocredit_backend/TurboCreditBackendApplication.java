package api.turbocredit_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TurboCreditBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurboCreditBackendApplication.class, args);
		System.out.println("\n----------------------------------------------------------");
		System.out.println("TurboCredit API running at: http://localhost:8082");
		System.out.println("Swagger UI: http://localhost:8082/swagger-ui/index.html");
		System.out.println("----------------------------------------------------------\n");
	}
}