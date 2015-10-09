package com.github.andszabo.exchange.boot

import akka.actor.{ActorRef, ActorSystem}
import com.github.andszabo.exchange.actor.ExchangeActor
import com.typesafe.config.{Config, ConfigFactory}
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ExchangeApp {

  @Bean
  def config: Config = ConfigFactory.load()

  @Bean
  def actorSystem : ActorSystem = ActorSystem("ExchangeSystem", config)

  @Bean
  def exchange: ActorRef = actorSystem.actorOf(ExchangeActor.props(), name = "exchange")
}


object Application {

  def main(args: Array[String]) {
    SpringApplication.run(classOf[ExchangeApp])
  }
}
