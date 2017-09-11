package com.github.holgerbrandl.spark.components;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.iterator.LocalizingZeroMinIntervalIterator;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.UnsignedByteType;

/**
 * @author Holger Brandl
 */
public class PrepImage {
    public static void main(String[] args) {
        final Img<UnsignedByteType> img = new ArrayImgFactory<UnsignedByteType>()
                .create(new long[]{400, 320}, new UnsignedByteType());


        // fixme net/imglib2/display/RealUnsignedByteConverter
//        ImageJFunctions.show( img );

        // https://imagej.net/ImgLib2_-_Getting_Started

        // http://javadoc.imagej.net/ImgLib2/net/imglib2/iterator/LocalizingZeroMinIntervalIterator.html
        LocalizingZeroMinIntervalIterator i = new LocalizingZeroMinIntervalIterator(img);
        RandomAccess<UnsignedByteType> s = img.randomAccess();

        while (i.hasNext()) {
            i.fwd();
            s.setPosition(i);
//            s.Typ.performOperation();
//            System.out.println(s.);

        }


        final ArrayImg<IntType, IntArray> imgGen = ArrayImgs.ints(
                new int[]{
                        0, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 0, 1, 0,
                        0, 1, 0, 0, 0,
                        0, 1, 0, 0, 0
                },
                5, 5);


    }
}
