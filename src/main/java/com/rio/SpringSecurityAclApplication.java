package com.rio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@SpringBootApplication
public class SpringSecurityAclApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringSecurityAclApplication.class, args);
    }
}
