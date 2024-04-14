package br.com.due.meudin.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    @Autowired
    SecurityFilter securityFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // Permite solicitações de qualquer origem
        corsConfiguration.addAllowedMethod("*"); // Permite todos os métodos HTTP
        corsConfiguration.addAllowedHeader("*"); // Permite todos os cabeçalhos
        corsConfiguration.setAllowCredentials(true); // Permite credenciais
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                                              .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors()
                .and()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.GET, "/api-status").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/user/cpf/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/user/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/spends/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/spends/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/spends/chart").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/spends/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/spends/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/wallet/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/card/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/card/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/card/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/card/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
