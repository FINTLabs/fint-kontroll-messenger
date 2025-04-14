package no.fintlabs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;


@SpringBootApplication
        (exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@Lazy(value = false)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public OpenAPI openAPIDescription() {
        return new OpenAPI()
                .info(new Info()
                              .title("FINT Kontroll messaging")
                              .version("1.0.0")
                              .description("API's for sending messages")
                              .termsOfService("http://swagger.io/terms/")
                              .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                                    .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                                            .type(SecurityScheme.Type.HTTP)
                                            .scheme("bearer")
                                            .bearerFormat("JWT")
                                            .in(SecurityScheme.In.HEADER)
                                            .description("Auth key")
                                            .name("Authorization"))
                )
                .addSecurityItem(new SecurityRequirement()
                                         .addList("bearer-jwt"));
    }
}
