package com.github.holgerbrandl.spark.components;

import ij.ImagePlus;
import ij.gui.NewImage;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.binary.Thresholder;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import java.util.Map;

/**
 * @author Holger Brandl
 */
public class LabelBenchmark {
    public static void main(String[] args) throws IncompatibleTypeException {
//
        Img image = makeTestImage(new int[]{2000, 2000, 1}, 1.);


//        ImagePlus testImage = ImageJFunctions.wrapUnsignedByte(image, "original");
        ImagePlus testImage = ImageJFunctions.wrapBit(image, "original");
        testImage.show();
//        new FileSaver(testImage).saveAsPng("test_image.png");


        LabelComponents labelComponents = new LabelComponents(image);


        // todo rather do histogram over image
        Map<Long, Long> compSizes = labelComponents.labelGraph().toJavaRDD().map(x -> (Long) x._2()._2).countByValue();
        System.out.println("numver of components was " + compSizes.size());

        // TODO compare against
//        ConnectedComponents.labelAllConnectedComponents(image)
    }


    private static Img makeTestImage(int[] dims, double cutoff) throws IncompatibleTypeException {

        int options = NewImage.FILL_RANDOM + NewImage.CHECK_AVAILABLE_MEMORY;
        ImagePlus img = NewImage.createByteImage("Noise", dims[0], dims[1], dims[2], options);

        ImagePlusImg image = ImagePlusAdapter.wrap(img);

        // perform gaussian convolution with float precision
        double[] sigma = new double[image.numDimensions()];

        for (int d = 0; d < image.numDimensions(); ++d) {
            sigma[d] = 8;
        }

        // first extend the image to infinity, zeropad
        RandomAccessible<ByteType> infiniteImg = Views.extendValue(image, new ByteType());


        // http://imagej.net/ImgLib2_Examples
        Gauss3.gauss(sigma, infiniteImg, image);

        // threshold see https://github.com/StephanPreibisch/imglib2-introduction/blob/master/src/main/java/net/imglib2/introduction/ImgLib2_Threshold6.java

        // from http://wiki.cmci.info/documents/120206pyip_cooking/python_imagej_cookbook#threshold_to_create_a_mask_binary
        // note reasonable cutoffs are between 127 and 131 which changes from 1 to multiple components
        Img<BitType> threshold = Thresholder.threshold(image, new UnsignedByteType(new Integer(130).byteValue()), false, 1);
//        ImagePlus thresholdImg = ImageJFunctions.wrapFloat(image, "bar");
//        thresholdImg.getProcessor().threshold(255);


//        return threshold;
//
        // from https://www.programcreek.com/java-api-examples/index.php?api=net.imglib2.converter.Converter
//        IterableInterval<ByteType> convert = Converters.convert(threshold, (input, output) -> {
//            output.set((byte) (input.get() ? 1 : 0));
//        }, new ByteType());


//        Scale affine = new Scale(100);
//        threshold = .affine(threshold, affine);


        // how to convert http://forum.imagej.net/t/how-to-convert-from-net-imagej-dataset-to-net-imglib2-img-img-floattype/1371/2
//        Img<FloatType> img2 = ImageJFunctions.convertFloat(ImagePlusAdapter.wrap(convert));

        return threshold;
//        return ImagePlusAdapter.wrap(thresholdImg) ;
    }


}
