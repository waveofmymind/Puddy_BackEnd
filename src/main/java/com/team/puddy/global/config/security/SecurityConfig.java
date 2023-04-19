package com.team.puddy.global.config.security;


import com.team.puddy.global.config.security.jwt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;


    // 정적 자원에 대해서는 Security 설정을 적용하지 않음
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**/*").permitAll()
                .antMatchers(HttpMethod.POST,"/users/experts","/experts/send-docs").hasRole("EXPERT")
                .antMatchers(HttpMethod.PUT,"/users/experts","/experts/**").hasRole("EXPERT")
                .antMatchers(HttpMethod.POST,"/users/pets","/users/experts","/questions/**","/articles/**","/reviews/**","/users/reissue").hasAnyRole("USER","EXPERT")
                .antMatchers(HttpMethod.PUT,"/questions/**","/users/**").hasAnyRole("USER","EXPERT")
                .antMatchers(HttpMethod.DELETE,"/questions/**","/articles/**","/users/**").hasAnyRole("USER","EXPERT")
                .antMatchers(HttpMethod.PATCH,"/users/**","/questions/**","/articles/**").hasAnyRole("USER","EXPERT")
                .antMatchers(HttpMethod.POST,"/users/**","/oauth/**").permitAll()
                .antMatchers(HttpMethod.GET,
                        "/users/**",
                        "/reviews/**").hasAnyRole("USER", "EXPERT")
                .antMatchers("/users/**", "/experts/**", "/questions/**", "/articles/**","/home").permitAll()
                .and()
                .formLogin().disable()
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)

        ;

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
