package com.github.eyce9000.spark.bes.client
import java.net.URLEncoder
import scalaj.http._
import javax.xml.bind.JAXBContext
import com.github.eyce9000.spark.bes.client.xml.QueryResult
import com.github.eyce9000.spark.bes.client.xml.ResultTuple
import scala.collection.JavaConverters._
import com.github.eyce9000.spark.bes.relevance.Relevance
import com.github.eyce9000.spark.bes.BigfixConfig

class BigfixAPI(val config:BigfixConfig){
  def context = JAXBContext.newInstance(classOf[QueryResult])
  def unmarshaller = context.createUnmarshaller()
  
  def query(rawQuery:String):Iterator[IndexedSeq[Any]]={
    System.setProperty("jsse.enableSNIExtension", "false")
    def relevanceQuery = Relevance.getCleanedQuery(rawQuery)
    def request = Http(config.url+"api/query")
      .auth(config.username,config.password)
      .option(HttpOptions.allowUnsafeSSL)
      .timeout(connTimeoutMs=config.connTimeout,readTimeoutMs=config.readTimeout)
      .postData("relevance=" + URLEncoder.encode(relevanceQuery,"UTF-8"))
      
    def result = request.execute { input => unmarshaller.unmarshal(input).asInstanceOf[QueryResult] }.body
    
    if(result.getSingleResults.size > 0){
      return result.getSingleResults.iterator().asScala.map { obj => Vector(obj) }
    }
    
    else if(result.getPluralResults.size > 0){
      return result.getPluralResults.iterator().asScala.map { tuple => tuple.getAnswers.asScala.toVector}
    }
    
    else{
      return IndexedSeq().iterator
    }
    
  }
  
}