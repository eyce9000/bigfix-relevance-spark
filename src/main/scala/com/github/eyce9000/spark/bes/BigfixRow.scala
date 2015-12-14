package com.github.eyce9000.spark.bes

import org.apache.spark.sql.Row

class BigfixRow(val data:IndexedSeq[Any]) extends Row{
  def copy(): org.apache.spark.sql.Row = new BigfixRow(data)
  def get(i: Int): Any = data(i)
  def length: Int = data.length
}