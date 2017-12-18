package com.xiaad.netty

import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

import scala.collection.mutable

/**
  * <p></p>
  *
  * @author Andy 2017/12/18
  */
class Master(val hostNme: String, val port: Int) extends Actor {
  private val id2WorkerInfo = new mutable.HashMap[String, WorkerInfo]()
  private val workerInfoes = new mutable.HashSet[WorkerInfo]()


  override def preStart(): Unit = {
    import scala.concurrent.duration._
    //隐式转换的工具
    import context.dispatcher
    context.system.scheduler.schedule(0 millis,15000 millis,self,CheckTimeOut)
  }

  /**
    * 这个方法启动之后一直做监听，你可以认为它是一个死循环
    *
    * @return
    */
  override def receive: Receive = {
    case RegisterWorker(workerId, cpu, memory) => {
      //master接收到Worker过来的注册信息
     if(!id2WorkerInfo.contains(workerId)) {
       /**
         * 1)可以保存到map中 workerId当键，WorkerInfo(cpu,memory)当值 保存在内存中 hashMap
         * <k,v> workerId WorkerInfo
         * 2）把信息保存到zookeeper中
         */
       val workerInfo = new WorkerInfo(workerId, cpu, memory)
       //保存worker信息到内存中
       id2WorkerInfo(workerId) = workerInfo
       workerInfoes += workerInfo

       //如果保存成功，那么它是注册成功了
       //master给worker发送了注册成功的信息
       sender() ! RegisteredWorker(s"masterURL:hostName:${hostNme} port:${port}")
     }
    }

    case HeartBeat(workerId)=>{
      val currentTimeMillis = System.currentTimeMillis()

      val workerInfo = id2WorkerInfo(workerId)
      workerInfo.lastHeartBeatTime = currentTimeMillis
    }

    case CheckTimeOut =>{
      //首先过滤超时的心跳
      val currentTimeMillis = System.currentTimeMillis()
      val deadWorker = workerInfoes.filter(w => currentTimeMillis - w.lastHeartBeatTime > 15000)
      //遍历超时的Worker，超时的Worker从内存中移除
      deadWorker.foreach(w =>{
        id2WorkerInfo -= w.workerId
        workerInfoes -= w
      })

      println(s"成功注册的Worker个数${workerInfoes.size}")
    }
  }
}

object Master {
  val MASTER_ACTOR_NAME = "MasterActorSystem"
  val MASTER_NAME = "master"

  def main(args: Array[String]): Unit = {
    val hostName = args(0)
    val port = args(1).toInt
    val str =
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname="$hostName"
         |akka.remote.netty.tcp.port="$port"
      """.stripMargin
    val config = ConfigFactory.parseString(str)
    val masterActorSystem = ActorSystem(MASTER_ACTOR_NAME, config)
    masterActorSystem.actorOf(Props(new Master(hostName,port)), MASTER_NAME)
  }
}
