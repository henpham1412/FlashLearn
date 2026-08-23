package com.hen.flastcard.configuration;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hen.flastcard.constant.PredefinedRole;
import com.hen.flastcard.entity.Role;
import com.hen.flastcard.entity.User;
import com.hen.flastcard.repository.RoleRepository;
import com.hen.flastcard.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    static String ADMIN_USER_NAME = "admin";
    static String ADMIN_PASSWORD = "admin123";
    static String ADMIN_EMAIL = "admin@gmail.com";
    // @ConditionalOnProperty use turn on/of a bean
    // in this case this annotation not really necessary  (just use to know more)
    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (userRepository.findByUsername(ADMIN_USER_NAME).isEmpty()) {
                roleRepository.save(Role.builder()
                        .name(PredefinedRole.USER_ROLE)
                        .description("User role")
                        .build());
                Role adminRole = roleRepository.save(Role.builder()
                        .name(PredefinedRole.ADMIN_ROLE)
                        .description("Admin role")
                        .build());
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                User user = User.builder()
                        .username(ADMIN_USER_NAME)
                        .email(ADMIN_EMAIL)
                        .password(passwordEncoder.encode(ADMIN_PASSWORD))
                        .roles(roles)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with password: admin123, please change it!");
            }
            log.info("Application initialization completed .....");
        };
    }
}
