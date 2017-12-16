import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory

/**
  * <p></p>
  *
  * @author Andy 2017/12/16
  */
class Worker extends Actor{


  override def preStart(): Unit = {
    val actorRef = context.actorSelection("akka.tcp://MasterActorSystem@localhost:7899/user/master")
    actorRef ! "hello"
  }

  override def receive: Receive = {
    case "hi"=>{
      println("我是Worker，接收到Master的信息【hi】")
    }
  }
}

object Worker {
  def main(args: Array[String]): Unit = {
    val hostName = "localhost"
    val str =
      s"""
        |akka.actor.provider="akka.remote.RemoteActorRefProvider"
        |akka.remote.netty.tcp.hostname="$hostName"
      """.stripMargin
    val config = ConfigFactory.parseString(str)
    val workerActorSystem = ActorSystem("WorkerActorSystem",config)
    workerActorSystem.actorOf(Props(new Worker),"worker")
  }
}
