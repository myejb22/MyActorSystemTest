package com.xiaad.netty

/**
  * <p></p>
  *
  * @author Andy 2017/12/18
  */

/**
  * 这个用来保存worker的信息
  * @param workId 每个Worker独一无二的ID号
  * @param cpu
  * @param memory
  */
case class RegisterWorker(val workId:String,val cpu:Int,val memory:Int) extends Serializable {
}

case class RegisteredWorker(val masterURL:String) extends Serializable

case class HeartBeat(val workerId:String) extends Serializable

case object SendHeartBeat

case object CheckTimeOut
