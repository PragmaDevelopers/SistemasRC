package com.api.sistema_rc.config;

import com.api.sistema_rc.service.SecurityFilterImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private SecurityFilterImpl securityFilterImpl;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
            .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/private/**").hasAnyRole("PROFESSIONAL","ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest().authenticated()
            )
            .logout(logout -> logout.disable()
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
//                .logoutRequestMatcher(new AntPathRequestMatcher("/p/logout"))
//                .logoutSuccessUrl("/p/login?logout=true")
//                .permitAll()
            )
            .sessionManagement(sessionManagement -> sessionManagement.disable()
//                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .csrf(csrf->csrf.disable());

        return httpSecurity.build();
    }

    @Bean
    public FilterRegistrationBean<SecurityFilterImpl> securityFilterRegistration(SecurityFilterImpl securityFilter) {
        FilterRegistrationBean<SecurityFilterImpl> registrationBean = new FilterRegistrationBean<>(securityFilter);
        registrationBean.setEnabled(false); // Desativar o filtro por padrão

        // Condicionalmente ative o filtro para determinadas rotas
        // Por exemplo, ative o filtro para a rota /api/secure
        registrationBean.addUrlPatterns("/api/private/**","/api/admin/**");

        return registrationBean;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}