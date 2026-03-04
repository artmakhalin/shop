package com.ait.shop.security.config;

import com.ait.shop.security.filter.TokenFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${security.csrf-enabled:true}")
    private boolean csrfEnabled;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenFilter filter) throws Exception {
        return http
                .csrf(x -> {
                            if (csrfEnabled) {
                                x.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                        .ignoringRequestMatchers(
                                                "/v3/api-docs/**",
                                                "/swagger-ui/**",
                                                "/swagger-ui.html",
                                                "/auth/login",
                                                "/auth/access",
                                                "/auth/logout"
                                        );
                            } else {
                                x.disable();
                            }
                        }
                )
                .sessionManagement(
                        x -> x.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        x -> x.requestMatchers(HttpMethod.POST, "/products").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/products").permitAll()
                                .requestMatchers(HttpMethod.GET, "/products/{id:\\d+}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/products/{id:\\d+}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/products/{id:\\d+}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/products/{id:\\d+}/restore").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/products/count").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/products/total-cost").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/products/avg-price").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/products/{id:\\d+}/image").hasRole("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/customers").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/customers").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/customers/{id:\\d+}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/customers/count").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/customers/{id:\\d+}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/customers/{id:\\d+}/restore").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.DELETE, "/customers/{id:\\d+}").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.POST, "/customers/{customerId:\\d+}/cart/items/{productId:\\d+}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.PUT, "/customers/{customerId:\\d+}/cart/items/{productId:\\d+}/remove").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/customers/{customerId:\\d+}/cart/total-cost").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.GET, "/customers/{customerId:\\d+}/cart/avg-price").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.DELETE, "/customers/{customerId:\\d+}/cart/items").hasAnyRole("ADMIN", "USER")
                                .requestMatchers(HttpMethod.POST, "/customers/{customerId:\\d+}/image").hasAnyRole("ADMIN", "USER")

                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/access").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/logout").permitAll()
                                .requestMatchers(HttpMethod.GET, "/auth/csrf").permitAll()

                                .requestMatchers(HttpMethod.POST, "users/register").permitAll()
                                .requestMatchers(HttpMethod.GET, "users/confirm/{code}").permitAll()

                                .requestMatchers(
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html"
                                ).permitAll()

                                .anyRequest().denyAll()
                )
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
