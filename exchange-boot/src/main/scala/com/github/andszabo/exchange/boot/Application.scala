package com.github.andszabo.exchange.boot

import akka.actor.{ActorRef, ActorSystem}
import com.github.andszabo.exchange.actor.ExchangeActor
import com.github.andszabo.exchange.actor.cluster.NodeConfig
import com.typesafe.config.Config
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ExchangeApp {

  @Bean
  def config(): Config = {
    val seedNodes = if (System.getenv("SEED_NODES") != null)
      System.getenv("SEED_NODES").split(",").map(_.trim).toSeq
    else
      Seq.empty
    NodeConfig("true".equalsIgnoreCase(System.getenv("SEED")), seedNodes).config
  }

  @Bean
  def actorSystem: ActorSystem = ActorSystem("ExchangeSystem", config())

  @Bean
  def exchange: ActorRef = actorSystem.actorOf(ExchangeActor.props(), name = "exchange")
}


object Application {

  def main(args: Array[String]) {
    System.out.println(System.getenv())
    SpringApplication.run(Array(classOf[ExchangeApp].asInstanceOf[AnyRef]), args)
  }
}
