package com.github.eyce9000.spark.bes.client
import java.net.URLEncoder
import scalaj.http._
import javax.xml.bind.JAXBContext
import com.github.eyce9000.spark.bes.client.xml.QueryResult
import com.github.eyce9000.spark.bes.client.xml.ResultTuple
import scala.collection.JavaConverters._
import com.github.eyce9000.spark.bes.relevance.Relevance
import com.github.eyce9000.spark.bes.BigfixConfig
import com.github.eyce9000.spark.bes.client.xml.RequestBuilder
import com.github.eyce9000.spark.bes.client.xml.Envelope
import java.io.StringWriter
import com.github.eyce9000.spark.bes.client.xml.WRResultParser

class BigfixAPI(val config:BigfixConfig){
  def context = JAXBContext.newInstance(classOf[QueryResult],classOf[Envelope])
  def unmarshaller = context.createUnmarshaller()
  def marshaller = context.createMarshaller()
  var token:String = null
  
  if(config.insecure)
    System.setProperty("jsse.enableSNIExtension", "false")
  
  def queryRESTAPI(relevanceQuery:String):Iterator[IndexedSeq[Any]]={
    var request = Http(config.url+"api/query")
      .auth(config.username,config.password)
      .timeout(connTimeoutMs=config.connTimeout,readTimeoutMs=config.readTimeout)
      .postData("relevance=" + URLEncoder.encode(relevanceQuery,"UTF-8"))
      
    if(config.insecure)
      request = request.option(HttpOptions.allowUnsafeSSL)
      
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
  
  def queryWebreports(relevanceQuery:String):Iterator[IndexedSeq[Any]]={
    var builder = new RequestBuilder()
    if(token==null){
      builder.login(config.username, config.password)
    }
    else{
      builder.authenticate(config.username, token)
    }
    val writer = new StringWriter()
    marshaller.marshal(builder.buildRelevanceRequest(relevanceQuery),writer)
    
    val data = writer.toString()
    
    var request = Http(config.url+"soap")
      .timeout(connTimeoutMs=config.connTimeout,readTimeoutMs=config.readTimeout)
      .postData(data)
    if(config.insecure)
      request = request.option(HttpOptions.allowUnsafeSSL)
    
    def httpResult = request.execute { input => new WRResultParser().parse(input) }
    if(httpResult.code!=200)
      throw new Exception("HTTP Returned "+httpResult.code+" on URL "+request.url)
    
    def result = httpResult.body
    
    if(result.getError()!=null)
      throw new Exception(result.getError)
    
    if(result.getToken!=null)
      token = result.getToken
    
    return result.getResults.iterator().asScala.map { row => row.asScala.toVector }
  }
  
  def query(rawQuery:String):Iterator[IndexedSeq[Any]] = config.clientType match {
    case ClientType.RESTAPI => {queryRESTAPI(Relevance.getCleanedQuery(rawQuery))}
    case ClientType.Webreports => {queryWebreports(Relevance.getCleanedQuery(rawQuery))}
  }
}