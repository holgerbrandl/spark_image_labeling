package com.github.holgerbrandl.spark.components

import org.apache.spark.graphx.GraphLoader
import org.apache.spark.sql.SparkSession

/**
  * @author Holger Brandl
  */
object LabelComponents extends App {

  val spark = SparkSession.builder()
    .appName("Spark SQL basic example").master("local")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()
  // https://spark.apache.org/docs/latest/graphx-programming-guide.html#connected-components

  val sc = spark.sparkContext

  // Load the graph as in the PageRank example
  val graph = GraphLoader.edgeListFile(sc, "data/graphx/followers.txt")
  // Find the connected components
  val cc = graph.connectedComponents().vertices
  // Join the connected components with the usernames
  val users = sc.textFile("data/graphx/users.txt").map { line =>
    val fields = line.split(",")
    (fields(0).toLong, fields(1))
  }
  val ccByUsername = users.join(cc).map {
    case (id, (username, cc)) => (username, cc)
  }
  // Print the result
  println(ccByUsername.collect().mkString("\n"))

}
