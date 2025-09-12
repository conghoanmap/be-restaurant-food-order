package com.restaurant.foodorder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.restaurant.foodorder.auth.model.Role;
import com.restaurant.foodorder.auth.repo.RoleRepository;

@SpringBootApplication
public class FoodorderApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodorderApplication.class, args);
	}

	@Bean
	CommandLineRunner initRoles(RoleRepository roleRepository) {
		return args -> {
			if (!roleRepository.existsById("ROLE_MANAGER")) {
				roleRepository.save(new Role("ROLE_MANAGER"));
			}
			if (!roleRepository.existsById("ROLE_CUSTOMER")) {
				roleRepository.save(new Role("ROLE_CUSTOMER"));
			}
			if (!roleRepository.existsById("ROLE_STAFF")) {
				roleRepository.save(new Role("ROLE_STAFF"));
			}
		};
	}

}
