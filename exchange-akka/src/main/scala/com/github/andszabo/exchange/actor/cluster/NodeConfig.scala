package com.github.andszabo.exchange.actor.cluster

import com.typesafe.config._
import org.slf4j.LoggerFactory

/**
 * This configuration is intended to run in a docker environment.
 */
case class NodeConfig(isSeed: Boolean = false, seedNodes: Seq[String] = Seq.empty) {

  import NodeConfig._

  lazy val config = asConfig

  private def asConfig(): Config = {

    val configPath = if (isSeed) SEED_NODE else CLUSTER_NODE
    val ip = HostIP.load getOrElse "127.0.0.1"
    val config = ConfigFactory.parseString(
      s"""clustering.ip=${ip}
         |akka.remote.netty.tcp.hostname=${ip}
       """.stripMargin)
      .withFallback(ConfigFactory parseResources configPath)
      .withFallback(ConfigFactory.parseString(s"akka.cluster.seed-nodes = [$seedNodesString]"))
      .withFallback(ConfigFactory.load())
      .resolve

    logClusterSettings(config)

    config
  }

  private def logClusterSettings(config: Config): Unit = {
    if (log.isInfoEnabled) {
      val settingsString = Array("clustering.ip",
        "clustering.port",
        "akka.remote.netty.tcp.hostname",
        "akka.remote.netty.tcp.port",
        "akka.cluster.seed-nodes"
      ).map(k => s"${k}=${config.getAnyRef(k)}").mkString(",\n")
      log.info("Cluster settings:\n" + settingsString)
    }
  }

  private def seedNodesString: String = {
    seedNodes.map { node => s""""akka.tcp://ExchangeSystem@$node"""" }.mkString(",\n")
  }
}

object NodeConfig {

  val log = LoggerFactory.getLogger(NodeConfig.getClass)

  /** static configuration for seed nodes */
  val SEED_NODE = "node.seed.conf"

  /** static configuration for normal cluster nodes */
  val CLUSTER_NODE = "node.cluster.conf"

}