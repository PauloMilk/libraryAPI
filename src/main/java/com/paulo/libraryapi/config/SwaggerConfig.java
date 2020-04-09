package com.paulo.libraryapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfig {

    @Bean
    public Docket docket() {
        return new Docket(DocumentationType.SWAGGER_2)
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.paulo.libraryapi.api.resource"))
                    .paths(PathSelectors.any())
                    .build()
                    .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                    .title("Library API")
                    .description("API para controle de empréstimo de livros.")
                    .version("1.0.0")
                    .contact(this.contact())
                    .build();
    }

    private Contact contact() {
        return new Contact("Paulo Leite Costa", "http://github.com/pauloMilk", "pauloleitecosta14@gmail.com");
    }

}
