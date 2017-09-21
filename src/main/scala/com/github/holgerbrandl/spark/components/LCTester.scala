package com.github.holgerbrandl.spark.components

import net.imglib2.`type`.numeric.integer.IntType
import net.imglib2.img.array.{ArrayImg, ArrayImgs}
import net.imglib2.img.basictypeaccess.array.IntArray
import net.imglib2.img.display.imagej.ImageJFunctions

/**
  * @author Holger Brandl
  */
//object LCTester2 extends App {
object LCTester2 { // required for sbt plugin

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
  ImageJFunctions.show(img)
  //  ImageJFunctions.wrapUnsignedByte(img, "original").show()
  //  ImageJFunctions.wrapUnsignedByte(components.labelImage, "label_image").show()


  //  private val value: Any = ImagePlusAdapter.wrap(img)
  //  ImagePlusAdapter.wrap(imp)
  //  new FileSaver().saveAsPng("test_image.png")
  //  new FileSaver(ImageJFunctions.wrapUnsignedByte(img, "bar")).saveAsPng("test_image.png")
  //  new FileSaver(ImageJFunctions.wrapUnsignedByte(labelImage, "foo")).saveAsPng("label_image.png")
  //  RealUnsignedByteConverter
  // wrong internal package path RealUnsignedByteConverter
}
