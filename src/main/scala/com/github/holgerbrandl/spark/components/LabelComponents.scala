package com.github.holgerbrandl.spark.components

import com.github.holgerbrandl.spark.components.HashUtils.getLongHash
import net.imglib2.RandomAccess
import net.imglib2.`type`.numeric.integer.{IntType, UnsignedByteType}
import net.imglib2.img.Img
import net.imglib2.img.array.{ArrayImg, ArrayImgFactory, ArrayImgs}
import net.imglib2.img.basictypeaccess.array.IntArray
import net.imglib2.img.display.imagej.ImageJFunctions
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.JavaConversions

object Tester extends App {

  val img: ArrayImg[IntType, IntArray] = ArrayImgs.ints(Array[Int](
    0, 0, 0, 0, 0,
    0, 1, 0, 0, 1,
    0, 1, 0, 2, 3,
    0, 0, 0, 4, 0,
    0, 0, 0, 0, 0
  ), 5, 5)


  //  private val components = new LabelComponents(img)
  new LabelComponents(img)

  // see http://imagej.net/ImgLib2_Examples
  //ImageJFunctions.show(labelImage)
  ImageJFunctions.wrapUnsignedByte(img, "original").show()
  //  ImageJFunctions.wrapUnsignedByte(components.labelImage, "label_image").show()

}

/**
  * @author Holger Brandl
  */
class LabelComponents(val img: Img[IntType]) {

  //  Logger.getLogger("org").setLevel(Level.OFF)

  val spark = SparkSession.builder()
    .appName("Spark SQL basic example").master("local[6]")
    .config("spark.some.config.option", "some-value")
    .getOrCreate()
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


  //  private val value: Any = ImagePlusAdapter.wrap(img)
  //  ImagePlusAdapter.wrap(imp)
  //  new FileSaver().saveAsPng("test_image.png")
  //  new FileSaver(ImageJFunctions.wrapUnsignedByte(img, "bar")).saveAsPng("test_image.png")
  //  new FileSaver(ImageJFunctions.wrapUnsignedByte(labelImage, "foo")).saveAsPng("label_image.png")
  //  RealUnsignedByteConverter
  // wrong internal package path RealUnsignedByteConverter
}

object HashUtils {

  def getLongHash(x: Array[Int]): Long = {
    // https://stackoverflow.com/questions/744735/java-array-hashcode-implementation
    //    x.hashCode().toLong
    java.util.Arrays.hashCode(x).toLong
  }
}