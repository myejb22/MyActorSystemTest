package com.xiaad.netty

/**
  * <p></p>
  *
  * @author Andy 2017/12/18
  */

/**
  * 这个是保存Worker的机器信息
  * @param workerId
  * @param cpu
  * @param memory
  */
class WorkerInfo(val workerId:String,val cpu:Int,val memory:Int) {
  var lastHeartBeatTime:Long =_
}
