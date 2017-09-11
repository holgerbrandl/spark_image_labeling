package com.github.holgerbrandl.spark.components;

import net.imglib2.Cursor;
import net.imglib2.RealPoint;
import net.imglib2.RealRandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;


class DrawMandelbrot {
    public static void main(final String[] args) {
        final int[] dimensions = new int[]{600, 400};
        final Img<UnsignedByteType> img = new ArrayImgFactory<UnsignedByteType>()
                .create(dimensions, new UnsignedByteType());

        final RealRandomAccess<UnsignedByteType> mb = new MandelbrotRealRandomAccess();

        final double scale = 0.005;
        final double[] offset = new double[]{-2, -1};

        final Cursor<UnsignedByteType> cursor = img.localizingCursor();
        while (cursor.hasNext()) {
            cursor.fwd();
            for (int d = 0; d < 2; ++d)
                mb.setPosition(scale * cursor.getIntPosition(d) + offset[d], d);
            cursor.get().set(mb.get());
        }

        ImageJFunctions.show(img);
    }


    public static class MandelbrotRealRandomAccess extends RealPoint
            implements RealRandomAccess<UnsignedByteType> {
        final UnsignedByteType t;


        public MandelbrotRealRandomAccess() {
            super(2); // number of dimensions is 2
            t = new UnsignedByteType();
        }


        public final int mandelbrot(final double re0, final double im0,
                                    final int maxIterations) {
            double re = re0;
            double im = im0;
            int i = 0;
            for (; i < maxIterations; ++i) {
                final double squre = re * re;
                final double squim = im * im;
                if (squre + squim > 4)
                    break;
                im = 2 * re * im + im0;
                re = squre - squim + re0;
            }
            return i;
        }


        @Override
        public UnsignedByteType get() {
            t.set(mandelbrot(position[0], position[1], 255));
            return t;
        }


        @Override
        public MandelbrotRealRandomAccess copyRealRandomAccess() {
            return copy();
        }


        @Override
        public MandelbrotRealRandomAccess copy() {
            final MandelbrotRealRandomAccess a = new MandelbrotRealRandomAccess();
            a.setPosition(this);
            return a;
        }
    }
}