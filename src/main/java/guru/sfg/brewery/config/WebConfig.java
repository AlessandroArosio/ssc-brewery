package guru.sfg.brewery.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // this works globally (overridden if configured in spring security too
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT"); //.allowedOrigins("*");
    }
}
