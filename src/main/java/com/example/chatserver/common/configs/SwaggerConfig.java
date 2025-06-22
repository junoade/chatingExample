package com.example.chatserver.common.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("My Chat API")
                        .version("1.0.0")
                        .description("채팅 서비스 API 문서")
                        .contact(new Contact().name("Junho Choi").email("test@example.com")));
    }
}
