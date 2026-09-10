package com.tradefinance.demo.config;

import com.tradefinance.demo.controller.SessionController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
public class SecurityConfiguration {

    private static final String[] PUBLIC_PATHS = {
        "/", "/index.html", "/assets/**", "/error", SessionController.CSRF_PATH,
        "/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**"
    };

    private static final String CSP_DIRECTIVES =
        "default-src 'self'; script-src 'self'; style-src 'self'; object-src 'none'; frame-ancestors 'none'; base-uri 'self'";

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(this::configureAuthorization);
        http.formLogin(this::configureFormLogin);
        http.logout(this::configureLogout);
        http.exceptionHandling(this::configureExceptionHandling);
        http.headers(this::configureHeaders);

        return http.build();
    }

    private void configureAuthorization(
            AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        auth.requestMatchers(PUBLIC_PATHS).permitAll();
        auth.anyRequest().authenticated();
    }

    private void configureFormLogin(FormLoginConfigurer<HttpSecurity> form) {
        form.loginPage("/");
        form.loginProcessingUrl("/api/login");
        form.successHandler((req, res, authResult) -> res.setStatus(HttpStatus.OK.value()));
        form.failureHandler((req, res, ex) -> res.sendError(HttpStatus.UNAUTHORIZED.value()));
        form.permitAll();
    }

    private void configureLogout(LogoutConfigurer<HttpSecurity> logout) {
        logout.logoutUrl("/api/logout");
        logout.logoutSuccessHandler((req, res, authResult) -> res.setStatus(HttpStatus.OK.value()));
    }

    private void configureExceptionHandling(ExceptionHandlingConfigurer<HttpSecurity> errors) {
        errors.defaultAuthenticationEntryPointFor(
            new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
            PathPatternRequestMatcher.withDefaults().matcher("/api/**"));
    }

    private void configureHeaders(HeadersConfigurer<HttpSecurity> headers) {
        headers.contentSecurityPolicy(csp -> csp.policyDirectives(CSP_DIRECTIVES));
    }
}
