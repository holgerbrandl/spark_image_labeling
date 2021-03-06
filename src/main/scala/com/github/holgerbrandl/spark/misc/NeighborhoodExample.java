package com.github.holgerbrandl.spark.misc;


import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.algorithm.neighborhood.Neighborhood;
import net.imglib2.algorithm.neighborhood.RectangleShape;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.util.Util;
import net.imglib2.view.Views;


// https://github.com/imglib/imglib2-tutorials/blob/master/src/main/java/net/imglib2/algorithm/region/localneighborhood/NeighborhoodExample.java
public class NeighborhoodExample {
    public static void main(final String[] args) {
        final ArrayImg<IntType, IntArray> img = ArrayImgs.ints(
                new int[]{
                        0, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 0, 1, 0,
                        0, 1, 0, 0, 0,
                        0, 1, 0, 0, 0
                },
                5, 5);
        findLocalMaxima(Views.interval(Views.extendBorder(img), img));
    }


    public static <T extends Type<T> & Comparable<T>> void findLocalMaxima(final RandomAccessibleInterval<T> img) {
        // Create a neighborhood Shape, in this case a rectangle.
        // The parameters are span and skipCenter: span = 1 says that this will
        // be a 3x3x...x3 rectangle shape (where 3 == 2 * span + 1). skipCenter
        // = true says that the center pixel of the 3x3x...x3 shape is skipped
        // when iterating the shape.
        final RectangleShape shape = new RectangleShape(1, true);

        // Create a RandomAccess of img (This will be used to access the center
        // pixel of a neighborhood.)
        final RandomAccess<T> center = img.randomAccess();

        // Use the shape to create an Iterable<Neighborhood<T>>.
        // This is a IterableInterval whose elements of Neighborhood<T> are all
        // the 3x3x...x3 neighborhoods of img.
        // The Neighborhood<T> themselves are IterableIntervals over the pixels
        // of type T in the 3x3x...x3 neighborhood. The Neighborhood<T> are also
        // Localizable, and localize() provides the coordinates which the
        // neighborhood is currently centered on.
        //
        // Note: By "all the 3x3x...x3 neighborhoods of img" we mean the set of
        // 3x3x...x3 neighborhoods centered on every pixel of img.
        // This means that out-of-bounds values will be accessed. The 3x3
        // neighborhood centered on pixel (0,0) contains pixels
        // {(-1,-1)...(1,1)}
        final Iterable<Neighborhood<T>> neighborhoods = shape.neighborhoods(img);

        // Iterate over all neighborhoods.
        for (final Neighborhood<T> neighborhood : neighborhoods) {
            // Position the center RandomAccess to the origin of the current
            // neighborhood and get() the centerValue.
            center.setPosition(neighborhood);
            final T centerValue = center.get();

            // Loop over pixels of the neighborhood and check whether the
            // centerValue is strictly greater than all of them. Note that
            // because we specified skipCenter = true for the RectangleShape the
            // center pixel itself is not included in the neighborhood values.
            boolean isMaximum = true;
            for (final T value : neighborhood) {
                if (value.compareTo(centerValue) >= 0) {
                    isMaximum = false;
                    break;
                }
            }

            // If this is a maximum print it's coordinates.
            if (isMaximum)
                System.out.println("maximum found at " + Util.printCoordinates(center));
        }
    }
}