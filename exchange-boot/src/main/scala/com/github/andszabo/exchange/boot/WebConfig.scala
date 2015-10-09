package com.github.andszabo.exchange.boot

import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.servlet.config.annotation.{ViewControllerRegistry, WebMvcConfigurerAdapter}
import org.thymeleaf.spring4.SpringTemplateEngine

@Configuration
class WebConfig extends WebMvcConfigurerAdapter {


  override def addViewControllers(registry: ViewControllerRegistry) = {
    registry.addViewController("/home").setViewName("index")
    registry.addViewController("/").setViewName("index")
    registry.addViewController("/login").setViewName("login")
  }
}
