package com.zy.travel.expense;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ExpenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpenseApplication.class, args);
    }
}
