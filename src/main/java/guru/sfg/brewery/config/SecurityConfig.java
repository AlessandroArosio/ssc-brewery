package guru.sfg.brewery.config;

import guru.sfg.brewery.security.google.Google2faFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository persistentTokenRepository;
    private final Google2faFilter google2faFilter;

    // needed for use with Spring Data JPA SPeL (spring expression language)
    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(google2faFilter, SessionManagementFilter.class);

        http.cors().and()
                .authorizeRequests(authorize -> authorize
                        .antMatchers("/h2-console/**").permitAll() // not use in PROD
                        .antMatchers("/", "/webjars/**", "/login", "/resources/**").permitAll())
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .formLogin(loginConfigurer ->
                        loginConfigurer.loginProcessingUrl("/login")
                            .loginPage("/index").permitAll()
                            .successForwardUrl("/")
                            .defaultSuccessUrl("/")
                            .failureUrl("/?error"))
                .logout(logoutConfigurer ->
                        logoutConfigurer.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                            .logoutSuccessUrl("/?logout")
                            .permitAll())
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**", "/api/**")
                .and().rememberMe()
                    .tokenRepository(persistentTokenRepository)
                    .userDetailsService(userDetailsService);

//                .rememberMe()
//                    .key("sfg-key")
//                    .userDetailsService(userDetailsService);

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
