package com.pasciitools.pithy;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;

//@EnableCaching
@SpringBootApplication
public class PithyApplication {
    public static void main(String[] args) {
        SpringApplication.run(PithyApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner (JdbcUserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        var defaultAdmin = User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("USER", "ADMIN").build();
        return args -> {
            UserDetails fetchedAdmin;
            try {
                fetchedAdmin = userDetailsManager.loadUserByUsername("admin");
            } catch (UsernameNotFoundException e) {
                fetchedAdmin = null;
            }
            if (fetchedAdmin == null)
                userDetailsManager.createUser(defaultAdmin);
        };
    }
}
