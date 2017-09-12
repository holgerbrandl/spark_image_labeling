package com.github.holgerbrandl.spark.components;

import javafx.util.Pair;
import net.imglib2.RandomAccess;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.array.ArrayLocalizingCursor;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.type.numeric.integer.IntType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Holger Brandl
 */
public class EdgedByCursor {


    List<Pair<int[], int[]>> edges = new ArrayList<>();
    List<int[]> nodes = new ArrayList<>();


    public EdgedByCursor(ArrayImg<IntType, IntArray> img) {

//        https://imagej.net/ImgLib2_-_Accessors#A_RealRandomAccess_to_Render_Mandelbrot_Fractals
        ArrayLocalizingCursor<IntType> locCursor = img.localizingCursor();
        final RandomAccess<IntType> r = img.randomAccess();

        int[] pos = new int[locCursor.numDimensions()];
        int[] centerPos = new int[locCursor.numDimensions()];

        while (locCursor.hasNext()) {
            locCursor.fwd();

            locCursor.localize(pos);
            locCursor.localize(centerPos);

            if (locCursor.get().get() == 0) { // or use whatever cutoff here
                continue;
            }

            int[] centerClone = Arrays.copyOf(centerPos, centerPos.length);
            nodes.add(centerClone);

            // scan all leftside neighbors
            for (int i = 0; i < pos.length; i++) {

                pos[i] -= 1;
                r.setPosition(pos);


                if (r.get().get() > 0) {
                    // found a new edge in the connectivity graph
                    edges.add(new Pair<>(Arrays.copyOf(pos, pos.length), centerClone));
                }

                pos[i] += 1;

            }
        }
    }


    public static void main(final String[] args) {
        final ArrayImg<IntType, IntArray> img = ArrayImgs.ints(
                new int[]{
                        0, 0, 0, 0, 0,
                        0, 1, 0, 0, 0,
                        0, 0, 0, 1, 0,
                        0, 1, 0, 0, 0,
                        0, 3, 0, 0, 0
                },
                5, 5);


        EdgedByCursor graph = new EdgedByCursor(img);

        System.out.println("num edges " + graph.edges.size());

//        final Cursor<IntType> cursor = img.cursor();
//        int max = 0;
//        while (cursor.hasNext()) {
//            cursor.fwd();
//            final IntType t = cursor.get();
//            max = Math.max(t.get(), max);
//
//            cursor.copyCursor().
//
//        }
//        System.out.println("max is " + max);
    }

}
