package com.xiaad.netty

import java.util.UUID

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * <p></p>
  *
  * @author Andy 2017/12/18
  */
class Worker(val masterHost: String, val masterPort: Int, val cpu: Int, val memory: Int) extends Actor {
  var masterRef: ActorSelection = _
  val workerId = UUID.randomUUID().toString

  /**
    * 生命周期的方法
    */
  override def preStart(): Unit = {
    masterRef = context.actorSelection(s"akka.tcp://${Master.MASTER_ACTOR_NAME}@${masterHost}:${masterPort}/user/${Master.MASTER_NAME}")
    masterRef ! RegisterWorker(workerId, cpu, memory)
  }

  override def receive: Receive = {
    case RegisteredWorker(masterURL) => {
      println(s"我是worker，我想master注册成功以后，master给我发送的URL${masterURL}")

      /**
        * 1) 参数1为延迟多长时间之后，开始执行
        * 2）参数2为每隔多长执行一次
        * 3）给谁发送
        * 4）发送的内容
        * 因为在实际的架构模式里面，我们就不能直接去发送心跳
        * 一般在发送心跳之前做好多准备工作
        */
      import scala.concurrent.duration._
      //隐式转换的工具
      import context.dispatcher
      context.system.scheduler.schedule(0 millis, 10000 millis, self, SendHeartBeat)
    }

    case SendHeartBeat => {
      //做好多的准备工作

      //给master发心跳信息
      masterRef ! HeartBeat(workerId)
    }
  }
}

object Worker {
  val WORKER_ACTOR_NAME = "WorkerActorSystem"
  val WORKER_NAME = "worker"

  def main(args: Array[String]): Unit = {
    val hostName = args(0)
    val masterHost = args(1)
    val masterPort = args(2).toInt
    val cpu = args(3).toInt
    val memory = args(4).toInt
    val port = args(5).toInt
    val str =
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname="$hostName"
         |akka.remote.netty.tcp.port="$port"
      """.stripMargin
    val config = ConfigFactory.parseString(str)
    val workerActorName = ActorSystem(WORKER_ACTOR_NAME, config)
    //创建并启动了worker的Actor
    workerActorName.actorOf(Props(new Worker(masterHost, masterPort,cpu,memory)), WORKER_NAME)
  }
}
