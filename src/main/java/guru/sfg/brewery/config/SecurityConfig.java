package guru.sfg.brewery.config;

import guru.sfg.brewery.security.SfgPasswordEncoderFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    PasswordEncoder passwordEncoder() {
        return SfgPasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/h2-console/**").permitAll() // not use in PROD
                        .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll()
                        .antMatchers("/beers/find", "/beers*").permitAll()
                        .antMatchers(HttpMethod.GET, "/api/v1/beer/**").permitAll()
                        .mvcMatchers(HttpMethod.GET, "/api/v1/beerUpc/{upc}").permitAll())
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin().and()
                .httpBasic()
                .and().csrf().disable();

        // h2 console config
        http.headers().frameOptions().sameOrigin();
    }

    // using fluent API
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("spring")
//                .password("{bcrypt}$2a$10$6Oa/ijV2EBAp4k4S3Cdg7.tjBkcEoHr42Nmo9WeN3kFAsW1.O.1I2")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{sha256}4617111016211f141eb1f5c5eda7909ff38884f395efbacebd0bbe6c68783aa41a5db7f9eff6e6c8")
//                .roles("USER");
//
//        auth.inMemoryAuthentication()
//                .withUser("scott").password("{bcrypt10}$2a$15$aeC3xXE71/oODEbQCJu2ouI4GLvHk1gg94086GSax3/fZR6SDCnRu")
//                .roles("CUSTOMER");
//    }
}
