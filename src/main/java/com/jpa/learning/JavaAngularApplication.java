package com.jpa.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JavaAngularApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaAngularApplication.class, args);
    }
    
    int i = 0;
    
//    @Bean
//    CommandLineRunner init(UserRepository userRepository) {
//        return args -> {
//            
//            Stream.of("John", "Julie", "Jennifer", "Helen", "Rachel").forEach(name -> {
//                User user = new User(++i,name, name.toLowerCase() + "@domain.com");
//                userRepository.save(user);
//            });
//            userRepository.findAll().forEach(System.out::println);
//        };
//    }
}
