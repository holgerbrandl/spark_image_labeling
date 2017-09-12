package com.github.holgerbrandl.spark.components

import ij.io.FileSaver
import net.imglib2.RandomAccess
import net.imglib2.`type`.numeric.integer.UnsignedByteType
import net.imglib2.img.Img
import net.imglib2.img.array.{ArrayImgFactory, ArrayImgs}
import net.imglib2.img.display.imagej.ImageJFunctions
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions


/**
  * @author Holger Brandl
  */
object LabelComponents extends App {

  val spark = SparkSession.builder()
    .appName("Spark SQL basic example").master("local[3]")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()
  // https://spark.apache.org/docs/latest/graphx-programming-guide.html#connected-components

  val sc = spark.sparkContext

  // Load the graph as in the PageRank example
  // see https://spark.apache.org/docs/latest/graphx-programming-guide.html
  //  val graph = GraphLoader.edgeListFile(sc, "data/graphx/followers.txt")


  val img = ArrayImgs.ints(Array[Int](
    0, 0, 0, 0, 0,
    0, 1, 0, 0, 1,
    0, 1, 0, 2, 3,
    0, 0, 0, 4, 0,
    0, 0, 0, 0, 0
  ), 5, 5)

  val graphData = new EdgesByCursor(img)


  private def getLongHash(x: Array[Int]): Long = {
    // https://stackoverflow.com/questions/744735/java-array-hashcode-implementation
    //    x.hashCode().toLong
    java.util.Arrays.hashCode(x).toLong
  }

  // fixme hashcode with proper uid here
  // see https://spark.apache.org/docs/latest/graphx-programming-guide.html
  val vertRDD = sc.parallelize(JavaConversions.asScalaBuffer(graphData.nodes))
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

  labelGraph
    .map { case (vertexId, (coord, color)) =>
      randAcc.setPosition(coord)
      randAcc.get.setInteger(color)
    }.collect()


  new FileSaver(ImageJFunctions.wrapUnsignedByte(img, "bar")).saveAsPng("test_image.png")
  new FileSaver(ImageJFunctions.wrapUnsignedByte(labelImage, "foo")).saveAsPng("label_image.png")

  // wrong internal package path RealUnsignedByteConverter
}
