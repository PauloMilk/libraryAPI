package com.paulo.libraryapi;

import com.paulo.libraryapi.service.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import javax.validation.constraints.Email;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

	@Autowired
	EmailService emailService;

	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {

			List<String> mail = Arrays.asList("af77633a6f-72d298@inbox.mailtrap.io");
			emailService.sendMails(mail, "Testando Serviço");
		};
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(LibraryApiApplication.class, args);
	}

}
