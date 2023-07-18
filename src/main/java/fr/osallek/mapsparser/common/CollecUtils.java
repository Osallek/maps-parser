package fr.osallek.mapsparser.common;

import java.util.Collections;
import java.util.List;

public final class CollecUtils {

    private CollecUtils() {
    }

    public static  <E extends Comparable<E>> int rank(List<E> list, E e) {
        int index = Collections.binarySearch(list, e);

        if (index > 0) {
            while (index > 0 && list.get(index - 1).equals(list.get(index))) {
                index--;
            }
        }

        return index;
    }
}
