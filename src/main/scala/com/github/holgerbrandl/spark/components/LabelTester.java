package com.github.holgerbrandl.spark.components;

import ij.ImageJ;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.IntType;

import java.util.Map;

/**
 * @author Holger Brandl
 */
public class LabelTester {

    public static void main(String[] args) throws IncompatibleTypeException {
        new ImageJ();

        // build a 3d image
        Img<BitType> image = ImageUtils.makeTestImage(new int[]{500, 500, 50}, 130);

//        ImagePlus testImage = ImageJFunctions.wrapUnsignedByte(image, "original");
//        testImage.show();
        ImageJFunctions.show(image, "test");
    }


    public static void main2(String[] args) throws IncompatibleTypeException {
//
        Img image = ImageUtils.makeTestImage(new int[]{2000, 2000}, 130);

//        ImageJFunctions.show(makeTestImage(new int[]{2000, 2000, 1}, 123), "123"); // almost black
//        ImageJFunctions.show(makeTestImage(new int[]{2000, 2000, 1}, 125), "125");
//        ImageJFunctions.show(makeTestImage(new int[]{2000, 2000, 1}, 127), "127");
//        ImageJFunctions.show(makeTestImage(new int[]{2000, 2000, 1}, 129), "129");
//        ImageJFunctions.show(makeTestImage(new int[]{2000, 2000, 1}, 131), "131");
//        ImageJFunctions.show(makeTestImage(new int[]{2000, 2000, 1}, 133), "133"); // white
//

//        ImagePlus testImage = ImageJFunctions.wrapUnsignedByte(image, "original");
//        ImagePlus testImage = ImageJFunctions.wrapBit(image, "original");
//        testImage.show();
//        new FileSaver(testImage).saveAsPng("test_image.png");


        LabelComponents labelComponents = new LabelComponents(image, Utils.localSpark(8));


        // todo rather do histogram over image
        Map<Long, Long> compSizes = labelComponents.labelGraph().toJavaRDD().map(x -> (Long) x._2()._2).countByValue();
        System.out.println("numver of components was " + compSizes.size());

        // TODO compare against
//        ConnectedComponents.labelAllConnectedComponents(image)
    }


    public static void main3(String[] args) throws IncompatibleTypeException {
        ArrayImg<IntType, IntArray> img = ArrayImgs.ints(new int[]{
                0, 0, 0, 0, 0,
                0, 1, 0, 0, 1,
                0, 1, 0, 2, 3,
                0, 0, 0, 4, 0,
                0, 0, 0, 0, 0
        }, 5, 5);


        new LabelComponents(img, Utils.localSpark(1));

        // see http://imagej.net/ImgLib2_Examples
        ImageJFunctions.show(img);
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
}
