package com.github.eyce9000.spark.bes

case class BigfixConfig(
  val relevanceQuery:String="",
  val url:String,
  val username:String,
  val password:String,
  val connTimeout:Int=1000,
  val readTimeout:Int=5000){
  
  
  def this(m:Map[String,String]) = 
    this(relevanceQuery = m("relevanceQuery"),
      url=m("url"),
      username=m("username"),
      password=m("password"),
      connTimeout=m.getOrElse("connTimeout", "1000").toInt,
      readTimeout=m.getOrElse("readTimeout","5000").toInt)
}