package it.formazione.kafka.order;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication @org.springframework.scheduling.annotation.EnableScheduling public class OrderApplication { public static void main(String[] args) { SpringApplication.run(OrderApplication.class,args); } }
