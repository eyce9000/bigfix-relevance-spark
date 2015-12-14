package com.github.eyce9000.spark.bes

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.Partition
import org.apache.spark.TaskContext
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.catalyst.expressions.SpecificMutableRow
import org.apache.spark.sql.catalyst.expressions.MutableRow
import scala.collection.JavaConverters._
import org.apache.spark.sql.types.IntegerType
import org.apache.spark.sql.types.StringType
import java.util.Date
import org.apache.spark.sql.types.TimestampType
import java.net.URI
import com.github.eyce9000.spark.bes.client.BigfixAPI
import com.github.eyce9000.spark.bes.client.xml.QueryResult
import org.apache.spark.sql.catalyst.InternalRow

class BigfixRDD(@transient sc: SparkContext, config:BigfixConfig, schema: StructType)
    extends RDD[BigfixRow](sc, Nil) {
  
  val relevanceQuery:String = config.relevanceQuery
  
  object SinglePartition extends Partition{
    val index:Int=0
  }
  override def getPartitions: Array[Partition] = Array(SinglePartition)
  
  private def getApi(config:BigfixConfig):BigfixAPI = {
    new BigfixAPI(config)
  }

  override def compute(thePart: Partition, context: TaskContext) = {
    getApi(config).query(config.relevanceQuery).map { row => new BigfixRow(row) }
  }
}