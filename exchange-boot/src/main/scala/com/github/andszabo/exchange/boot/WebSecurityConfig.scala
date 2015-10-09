package com.github.andszabo.exchange.boot

import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.{EnableWebSecurity, WebSecurityConfigurerAdapter}
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig extends WebSecurityConfigurerAdapter {


  override protected def configure(http: HttpSecurity) = {

    http
      .headers()
      .frameOptions().sameOrigin()
      .and()
      .csrf()
      .ignoringAntMatchers("/exchange/**")
      .and()
      .formLogin()
      .defaultSuccessUrl("/home")
      .loginPage("/login")
      .failureUrl("/login?error")
      .permitAll()
      .and()
      .logout()
      .logoutSuccessUrl("/login?logout")
      .logoutUrl("/logout")
      .permitAll()
      .and()
      .authorizeRequests()
      .antMatchers("/assets/**", "/webjars/**").permitAll()
      .anyRequest().authenticated()
  }

  override protected def configure(auth: AuthenticationManagerBuilder): Unit = {
    def inMemoryAuth = auth.inMemoryAuthentication()
    1 to 9 foreach { i =>
      inMemoryAuth.withUser(s"user${i}").password(s"user${i}").roles("USER")
    }
  }
}
