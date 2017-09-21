package com.github.holgerbrandl.spark.components;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessible;
import net.imglib2.algorithm.binary.Thresholder;
import net.imglib2.algorithm.gauss3.Gauss3;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;

import java.util.Random;

/**
 * @author Holger Brandl
 */
@SuppressWarnings("WeakerAccess")
public class ImageUtils {

    static Img<BitType> makeTestImage(int[] dims, int cutoff) {
        // old IJ API
        // todo set seed here!! ij.process.ByteProcessor.noise() is using fresh Random()
//        int options = NewImage.FILL_RANDOM + NewImage.CHECK_AVAILABLE_MEMORY;
//        ImagePlus img = NewImage.createByteImage("Noise", dims[0], dims[1], dims[2], options);
//        ImagePlusImg image = ImagePlusAdapter.wrap(img);
//        Img<UnsignedByteType> image = ImageJFunctions.wrap(img);

        // more modern imglib2 API
        long[] longDims = new long[]{dims[0], dims[1], dims[2]}; // how to do n-dim here?
        Img<UnsignedByteType> image = noiseImage(new UnsignedByteType(), longDims, new Random(1));
        ImageJFunctions.show(image, "noise");


        // perform gaussian convolution with float precision (see http://imagej.net/ImgLib2_Examples)
//        double[] sigma = IntStream.range(0, image.numDimensions()).mapToDouble(d -> 8).toArray();

        // first extend the image to infinity, zeropad
        RandomAccessible<UnsignedByteType> infiniteImg = Views.extendValue(image, new UnsignedByteType());


        try {
            Gauss3.gauss(3, infiniteImg, image);
        } catch (IncompatibleTypeException e) {
            throw new RuntimeException(e);
        }

        ImageJFunctions.show(image, "nach_gauss");


        // threshold see https://github.com/StephanPreibisch/imglib2-introduction/blob/master/src/main/java/net/imglib2/introduction/ImgLib2_Threshold6.java

        // from http://wiki.cmci.info/documents/120206pyip_cooking/python_imagej_cookbook#threshold_to_create_a_mask_binary
        // note reasonable cutoffs are between 127 and 131 which changes from 1 to multiple components
        @SuppressWarnings("UnnecessaryLocalVariable")
        Img<BitType> threshold = Thresholder.threshold(image, new UnsignedByteType(new Integer(cutoff).byteValue()), false, 1);
//        ImagePlus thresholdImg = ImageJFunctions.wrapFloat(image, "bar");
//        thresholdImg.getProcessor().threshold(255);


//        return threshold;
//

        // this worked (also see gitter
        // from https://www.programcreek.com/java-api-examples/index.php?api=net.imglib2.converter.Converter
//        RandomAccessibleInterval<ByteType> convert = Converters.convert(threshold, (input, output) -> {
//            output.set((byte) (input.get() ? 1 : 0));
//        }, new ByteType());
//        Img<ByteType> wrap = ImgView.wrap(convert, new ArrayImgFactory<>());
//        ImageJFunctions.show(wrap, "131");


//
//        Scale affine = new Scale(100);
//        threshold = .affine(threshold, affine);


        // how to convert http://forum.imagej.net/t/how-to-convert-from-net-imagej-dataset-to-net-imglib2-img-img-floattype/1371/2
//        Img<FloatType> img2 = ImageJFunctions.convertFloat(ImagePlusAdapter.wrap(convert));

        return threshold;
//        return ImagePlusAdapter.wrap(thresholdImg) ;
    }


    // from https://github.com/imglib/imglib2/blob/3016066a5c77969d1d5008a627475d6e98c251b4/src/test/java/tests/JUnitTestBase.java#L213
    public static <T extends RealType<T> & NativeType<T>> Img<T> noiseImage(final T type, final long[] dims, Random r) {
        final ImgFactory<T> factory = new ArrayImgFactory<T>();
        final Img<T> result = factory.create(dims, type);
        final Cursor<T> cursor = result.cursor();
        final long[] pos = new long[cursor.numDimensions()];
        while (cursor.hasNext()) {
            cursor.fwd();
            cursor.localize(pos);
            final float value = new Integer(r.nextInt()).byteValue();
            cursor.get().setReal(value);
        }
        return result;
    }
}
