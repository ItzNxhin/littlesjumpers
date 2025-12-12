package co.edu.udistrital.controller;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "co.edu.udistrital")
@EnableJpaRepositories(basePackages = "co.edu.udistrital.repository")
@EntityScan(basePackages = "co.edu.udistrital.model")
@EnableAsync
public class LittlesjumpersApplication {

	public static void main(String[] args) {
		// Cargar variables del archivo .env
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing()
				.load();

		// Establecer las variables como propiedades del sistema
		dotenv.entries().forEach(entry ->
			System.setProperty(entry.getKey(), entry.getValue())
		);

		SpringApplication.run(LittlesjumpersApplication.class, args);
	}

}