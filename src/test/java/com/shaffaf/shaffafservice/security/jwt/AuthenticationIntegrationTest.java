package com.shaffaf.shaffafservice.security.jwt;

import com.shaffaf.shaffafservice.config.SecurityConfiguration;
import com.shaffaf.shaffafservice.config.SecurityJwtConfiguration;
import com.shaffaf.shaffafservice.config.WebConfigurer;
import com.shaffaf.shaffafservice.management.SecurityMetersService;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import tech.jhipster.config.JHipsterProperties;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = {
        JHipsterProperties.class,
        WebConfigurer.class,
        SecurityConfiguration.class,
        SecurityJwtConfiguration.class,
        SecurityMetersService.class,
        JwtAuthenticationTestUtils.class,
    }
)
public @interface AuthenticationIntegrationTest {
}
