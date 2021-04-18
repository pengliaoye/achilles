package com.getxinfo.support.config;

import com.getxinfo.support.JwkSetEndpointFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwkSetEndpointFilter jwtSetFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(jwtSetFilter, SecurityWebFiltersOrder.LOGOUT)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/users/**").permitAll()
                        .anyExchange().authenticated()
                )
                .httpBasic(withDefaults())
                .formLogin(withDefaults());
        return http.build();
    }
    
}
