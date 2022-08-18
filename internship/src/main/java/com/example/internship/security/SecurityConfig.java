package com.example.internship.security;

import com.example.internship.repository.TeamMemberRepo;
import com.example.internship.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl myUserDetailsService;

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final TeamMemberRepo teamMemberRepo;

    @Bean
    public JwtRequestFilter authenticationJwtTokenFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl(teamMemberRepo);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests().antMatchers("/api/auth/login","/api/auth/refreshToken", "/api/reports/excel").permitAll()
                .antMatchers(HttpMethod.POST,"/api/categories").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/categories/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/categories/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/teamMembers").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/teamMembers/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/teamMembers/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/timeSheets").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.PUT,"/api/timeSheets/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.DELETE,"/api/timeSheets/**").hasAnyAuthority("ADMIN")
                .antMatchers(HttpMethod.POST,"/api/projects").hasAnyAuthority("ADMIN")
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll();

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("*")
                        .allowedMethods("*");
            }
        };
    }
}
