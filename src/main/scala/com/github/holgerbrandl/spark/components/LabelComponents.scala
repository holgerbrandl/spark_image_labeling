package com.github.holgerbrandl.spark.components

import com.github.holgerbrandl.spark.components.Utils.getLongHash
import net.imglib2.RandomAccess
import net.imglib2.`type`.numeric.integer.UnsignedByteType
import net.imglib2.img.Img
import net.imglib2.img.array.ArrayImgFactory
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions



/**
  * @author Holger Brandl
  */
//noinspection TypeAnnotation
class LabelComponents(val img: Img[_ <: Any], val spark: SparkSession = Utils.localSpark()) {



  // https://spark.apache.org/docs/latest/graphx-programming-guide.html#connected-components


  val sc = spark.sparkContext

  // Load the graph as in the PageRank example
  // see https://spark.apache.org/docs/latest/graphx-programming-guide.html
  //  val graph = GraphLoader.edgeListFile(sc, "data/graphx/followers.txt")


  // upscale to ease later visualization
  //  https://www.programcreek.com/java-api-examples/index.php?api=net.imglib2.ops.operation.real.binary.RealMultiply

  val graphData = new EdgesByCursor(img)

  // fixme hashcode with proper uid here
  // see https://spark.apache.org/docs/latest/graphx-programming-guide.html
  val vertRDD = sc.parallelize(JavaConversions.asScalaBuffer(graphData.nodes).to)
    .map(x => (getLongHash(x), x))

  val edgesRDD: RDD[Edge[String]] = sc.parallelize(JavaConversions.asScalaBuffer(graphData.edges))
    .map(x => new Edge[String](getLongHash(x.getKey), getLongHash(x.getValue)))


  val graph = Graph(vertRDD, edgesRDD)

  // Find the connected components
  val concomp = graph.connectedComponents()

  // join the graph with the nodes
  val labelGraph = vertRDD.join(concomp.vertices)


  //
  // create label image
  //

  private val dimension = Array.ofDim[Long](img.numDimensions())
  img.dimensions(dimension)
  val labelImage: Img[UnsignedByteType] = new ArrayImgFactory[UnsignedByteType]().create(dimension, new UnsignedByteType)
  val randAcc: RandomAccess[UnsignedByteType] = labelImage.randomAccess()

  labelGraph.collect()
    .foreach { case (vertexId, (coord, color)) =>
      randAcc.setPosition(coord)
      randAcc.get.setInteger(color)
    }
}

object Utils {

  def getLongHash(x: Array[Int]): Long = {
    // https://stackoverflow.com/questions/744735/java-array-hashcode-implementation
    //    x.hashCode().toLong
    java.util.Arrays.hashCode(x).toLong
  }

  def localSpark(numThreads: Integer = 1): SparkSession = {
    SparkSession.builder()
      .appName("Spark SQL basic example").master(s"local[$numThreads]")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()
  }
}