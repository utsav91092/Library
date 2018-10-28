package com.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  public static final String SWAGGER = "/swagger**";
  public static final String SWAGGER2 = "/configuration/**";
  public static final String SWAGGER3 = "/error";
  public static final String SWAGGER4 = "/v2/api-docs";
  public static final String SWAGGER5 = "/webjars/**";
  public static final String SWAGGER6 = "/favicon.ico";
  public static final String SWAGGER7 = "/swagger-ui.html#/";
  public static final String SWAGGER8 = "/swagger-ui.html";

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Override
  protected void configure(HttpSecurity http) throws Exception {


    // Disable CSRF (cross site requestdto forgery)
    http.csrf().disable();

    // No session will be created or used by spring security
    http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

    // Entry points
    http.authorizeRequests()//
            .antMatchers("/users/signin").permitAll()
            .antMatchers("/users/signup").permitAll()
            .antMatchers(SWAGGER).permitAll()
            .antMatchers(SWAGGER2).permitAll()
            .antMatchers(SWAGGER3).permitAll()
            .antMatchers(SWAGGER4).permitAll()
            .antMatchers(SWAGGER5).permitAll()
            .antMatchers(SWAGGER6).permitAll()
            .antMatchers(SWAGGER7).permitAll()
            .antMatchers(SWAGGER8).permitAll()
            // Disallow everything else..
            .anyRequest().authenticated();

    // If a user try to access a resource without having enough permissions
    http.exceptionHandling().accessDeniedPage("/login");

    // Apply JWT
    http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));

    // Optional, if you want to test the API from a browser
    // http.httpBasic();
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    // Allow swagger to be accessed without authentication
    web.ignoring().antMatchers("/v2/api-docs")
        .antMatchers("/swagger-resources/**")
        .antMatchers("/swagger-ui.html")
        .antMatchers("/configuration/**")
        .antMatchers("/webjars/**")
        .antMatchers("/public");
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

}
