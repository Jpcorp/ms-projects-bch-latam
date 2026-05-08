package cl.bch.cloud.ms.fglosas.mantenedor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(OpenApiProperties.class)
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo(OpenApiProperties openApiProperties) {
        return new OpenAPI()
                .info(new Info()
                        .title(openApiProperties.name())
                        .version(openApiProperties.version())
                        .description(openApiProperties.description())
                        .contact(new Contact()
                                .name(openApiProperties.contact().name())
                                .email(openApiProperties.contact().mail())));
    }

    @Bean
    public ApiResponse apiResponse() {
        return new ApiResponse();
    }


}