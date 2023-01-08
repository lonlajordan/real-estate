package com.real.estate;

import com.real.estate.models.User;
import com.real.estate.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;


@SpringBootApplication
public class RealEstateApplication extends SpringBootServletInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public RealEstateApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public static void main(String[] args) {
        SpringApplication.run(RealEstateApplication.class, args);
        
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(RealEstateApplication.class);
    }

    @Override
    public void run(String... args) {
        List<User> users = userRepository.findAll();
        if(users.isEmpty()){
            User user = new User();
            user.setFirstName("super");
            user.setLastName("admin");
            user.setEmail(" admin@gmail.com");
            user.setUsername("admin");
            user.setPassword(new BCryptPasswordEncoder().encode("admin"));
            user.setRoles("ROLE_ADMINISTRATOR");
            user.normalize();
            userRepository.save(user);
        }
    }
}
