package com.github.eyce9000.spark.bes.client

import org.junit.Test
import com.github.eyce9000.spark.bes.BigfixConfig
import javax.xml.bind.JAXBContext
import java.io.File

class TestBigfixAPI {
	val context = JAXBContext.newInstance(classOf[ConnectionDoc])
	val unmarshaller = context.createUnmarshaller()
	val webreportsDoc = unmarshaller.unmarshal(new File("config/test-webreports.xml")).asInstanceOf[ConnectionDoc]
	val restapiDoc = unmarshaller.unmarshal(new File("config/test-restapi.xml")).asInstanceOf[ConnectionDoc]
	
  @Test def connect {
    def api = new BigfixAPI(new BigfixConfig(url=webreportsDoc.host.toString(),username=webreportsDoc.username,password=webreportsDoc.password,clientType=ClientType.Webreports,insecure=true))
    
    var results = api.query("""names of bes computers whose (name of it contains "george") """)
    results.map { row => row.mkString(",") }.foreach { value => println(value) }
    results = api.query("""(last report time of it, name of it) of bes computers """)
    results.map { row => row.mkString(",") }.foreach { value => println(value) }
  }
}