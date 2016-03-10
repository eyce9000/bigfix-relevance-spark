package com.github.eyce9000.spark.bes

import java.util.regex.{Matcher, Pattern}

import org.apache.spark.{ SparkContext, SparkConf }
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.types._
import org.apache.spark.sql.sources._
import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.StringOps
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.SaveMode
import org.apache.spark.SparkEnv
import akka.event.Logging
import java.net.URI
import org.apache.spark.sql.catalyst.expressions.MutableRow
import org.apache.spark.sql.catalyst.InternalRow

case class BigfixReadRelation(config: BigfixConfig,schema: StructType)(@transient val sqlContext: SQLContext)
    extends BaseRelation with TableScan {
 
  def buildScan(): RDD[Row] = {
    new BigfixRDD(sqlContext.sparkContext,config,schema).asInstanceOf[RDD[Row]]
  }

}

class DefaultSource extends SchemaRelationProvider with RelationProvider{
    val ColumnMatcher = """.*//@Column\(?(\w+)?\)?\s+(\w+)\s?""".r

    def columnExtractor(rawText:String):StructType= {

      val fields:Seq[Option[StructField]] = rawText.split("\n").map((line:String) => line match {
        case ColumnMatcher(null, columnName) => {
          Option.apply(StructField(columnName,StringType,true))
        }
        case ColumnMatcher(typeName, columnName) => {
          val columnType:DataType = typeName match {
            case "string" => StringType
            case "date" => TimestampType
            case "time" => TimestampType
            case "number" => DoubleType
            case "int" => IntegerType
            case "bool" => BooleanType
            case _ => throw new Exception(s"unknown type ${typeName}")
          }

          Option.apply(StructField(columnName, columnType,true))
        }
        case _ => Option.empty
      }
        )
      StructType(fields.flatten)
    }
    def createRelation(sqlContext:SQLContext, parameters:Map[String,String]) ={
      create(sqlContext, parameters, columnExtractor(parameters("relevanceQuery")))
    }
    def createRelation(sqlContext:SQLContext, parameters:Map[String,String], schema: StructType) ={
      create(sqlContext, parameters, schema)
    }
    private def create(sqlContext: SQLContext, parameters: Map[String, String], inSchema: StructType) = {
      val config: BigfixConfig = new BigfixConfig(parameters)
      BigfixReadRelation(config, inSchema)(sqlContext)
    }
}