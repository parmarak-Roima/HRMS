package com.HRMS.HRMS.Config;
import com.HRMS.HRMS.filters.FilterChainExceptionHandler;
import com.HRMS.HRMS.repository.EmployeeRepository;
import com.HRMS.HRMS.filters.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.HRMS.HRMS.Config.OAuth2LoginSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // enable preauthorize in controllers
public class SecurityConfig {

    private final JwtAuthFilter authFilter;
    private final FilterChainExceptionHandler filterChainExceptionHandler;
    private final EmployeeRepository employeeRepository;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Autowired
    public SecurityConfig(
            JwtAuthFilter authFilter,
            EmployeeRepository employeeRepository,
            FilterChainExceptionHandler filterChainExceptionHandler,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler
    ){
        this.authFilter = authFilter;
        this.employeeRepository = employeeRepository;
        this.filterChainExceptionHandler = filterChainExceptionHandler;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors() // Enable CORS
                .and()
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/log").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/api/auth/**", "/oauth2/**", "/login/**").permitAll()
                        .requestMatchers("/notifications/**").permitAll()// Login & Register are open
                        .anyRequest().authenticated() // All other requests need token
                )
                .oauth2Login(oauth2 -> oauth2
                        //calling success handler
                        .successHandler(oAuth2LoginSuccessHandler)
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(authFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filterChainExceptionHandler, JwtAuthFilter.class)
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> employeeRepository.findByEmail(username)
                .map(employee -> org.springframework.security.core.userdetails.User.builder()
                        .username(employee.getEmail())
                        .password(employee.getPasswordHash())
                        .roles(String.valueOf(employee.getRole()))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
