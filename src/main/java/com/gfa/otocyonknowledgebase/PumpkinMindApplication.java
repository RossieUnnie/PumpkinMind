package com.gfa.otocyonknowledgebase;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Pumpkin MIND",
    version = "1.0", description = "Knowledge base"))
public class PumpkinMindApplication {

  public static void main(String[] args) {
    SpringApplication.run(PumpkinMindApplication.class, args);
  }

}
