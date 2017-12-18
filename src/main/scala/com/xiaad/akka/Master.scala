package com.xiaad.akka

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * <p></p>
  *
  * @author Andy 2017/12/16
  */
class Master extends Actor{
  override def receive: Receive = {
    case "hello" =>{
      println("我是Master,接收了Worker发送的消息，内容【hello】")
      sender() ! "hi"
    }
  }
}

object Master {
  def main(args: Array[String]): Unit = {
    val hostName = "localhost"
    val port = 7899
    val str =
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname="$hostName"
         |akka.remote.netty.tcp.port="$port"
      """.stripMargin
    val config = ConfigFactory.parseString(str)
    val masterActorSystem = ActorSystem("MasterActorSystem", config)
    masterActorSystem.actorOf(Props(new Master), "master")
  }
}
