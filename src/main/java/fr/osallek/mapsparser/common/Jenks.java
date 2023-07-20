package fr.osallek.mapsparser.common;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

/* From https://github.com/genepi/haplocheck/blob/master/src/main/java/genepi/haplocheck/util/Jenks.java */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The Jenks optimization method, also called the Jenks natural breaks
 * classification method, is a data classification method designed to determine
 * the best arrangement of values into different classes. This is done by
 * seeking to minimize each class’s average deviation from the class mean, while
 * maximizing each class’s deviation from the means of the other groups. In
 * other words, the method seeks to reduce the variance within classes and
 * maximize the variance between classes.
 */
public class Jenks {

    private List<Double> list;

    public Jenks(Collection<Double> list) {
        this.list = new ArrayList<>(list);
        Collections.sort(this.list);
    }

    public Breaks computeBreaks() {
        int uniqueValues = (int) this.list.stream().distinct().count();

        if (uniqueValues <= 3) {
            return computeBreaks(uniqueValues);
        }

        Breaks lastBreaks = computeBreaks(2);
        double lastGvf = lastBreaks.gvf();
        double lastImprovement = lastGvf - computeBreaks(1).gvf();

        for (int i = 3; i <= Math.min(6, uniqueValues); ++i) {
            Breaks breaks = computeBreaks(2);
            double gvf = breaks.gvf();
            double marginalImprovement = gvf - lastGvf;

            if (marginalImprovement < lastImprovement) {
                return lastBreaks;
            }

            lastBreaks = breaks;
            lastGvf = gvf;
            lastImprovement = marginalImprovement;
        }

        return lastBreaks;
    }

    /**
     * @param nbClasses int number of classes
     *
     * @return int[] breaks (upper indices of class)
     */
    public Breaks computeBreaks(int nbClasses) {
        if (this.list.size() == 0) {
            return new Breaks(List.of(0d), List.of(0));
        }

        // * lowerClassLimits (LC): optimal lower class limits
        // * varianceCombinations (OP): optimal variance combinations for all classes
        double[][] lowerClassLimits = new double[this.list.size() + 1][nbClasses + 1];
        double[][] varianceCombinations = new double[this.list.size() + 1][nbClasses + 1];

        for (int i = 1; i <= nbClasses; i++) {
            lowerClassLimits[1][i] = 1;
            varianceCombinations[1][i] = 0;

            for (int j = 2; j <= this.list.size(); j++) {
                varianceCombinations[j][i] = Double.MAX_VALUE;
            }
        }

        double variance = 0;
        for (int l = 2; l <= this.list.size(); l++) {
            double sum = 0;
            double sumSquares = 0;
            double w = 0;

            for (int m = 1; m <= l; m++) {
                int lowerClassLimit = l - m + 1;
                double val = this.list.get(lowerClassLimit - 1);

                sumSquares += val * val;
                sum += val;

                w++;
                variance = sumSquares - (sum * sum) / w;
                int i4 = lowerClassLimit - 1;

                if (i4 != 0) {
                    for (int j = 2; j <= nbClasses; j++) {
                        if (varianceCombinations[l][j] >= (variance + varianceCombinations[i4][j - 1])) {
                            lowerClassLimits[l][j] = lowerClassLimit;
                            varianceCombinations[l][j] = variance + varianceCombinations[i4][j - 1];
                        }
                    }
                }
            }

            lowerClassLimits[l][1] = 1;
            varianceCombinations[l][1] = variance;
        }

        int k = this.list.size();
        Integer[] kclass = new Integer[nbClasses];
        kclass[nbClasses - 1] = this.list.size() - 1;

        for (int j = nbClasses; j >= 2; j--) {
            int id = (int) (lowerClassLimits[k][j]) - 2;

            kclass[j - 2] = Math.max(0, id);

            k = (int) lowerClassLimits[k][j] - 1;
        }

        return new Breaks(this.list, Arrays.asList(kclass));
    }

    public static class Breaks {

        private final List<Double> sortedValues;

        private final List<Integer> breaks;

        /**
         * @param sortedValues the complete array of sorted data values
         * @param breaks       the indexes of the values within the sorted array that begin new classes
         */
        private Breaks(List<Double> sortedValues, List<Integer> breaks) {
            this.sortedValues = sortedValues;
            this.breaks = breaks;
        }

        /**
         * The Goodness of Variance Fit (GVF) is found by taking the difference
         * between the squared deviations from the array mean (SDAM) and the
         * squared deviations from the class means (SDCM), and dividing by the
         * SDAM
         *
         * @return
         */
        public double gvf() {
            double sdam = sumOfSquareDeviations(this.sortedValues);
            double sdcm = 0.0;

            for (int i = 0; i != numClasses(); ++i) {
                sdcm += sumOfSquareDeviations(classList(i));
            }

            return (sdam - sdcm) / sdam;
        }

        private double sumOfSquareDeviations(List<Double> values) {
            double mean = mean(values);
            double sum = 0.0;

            for (int i = 0; i != values.size(); ++i) {
                double sqDev = Math.pow(values.get(i) - mean, 2);
                sum += sqDev;
            }

            return sum;
        }

        private List<Double> classList(int i) {
            int classStart = (i == 0) ? 0 : this.breaks.get(i - 1) + 1;
            int classEnd = this.breaks.get(i);
            List<Double> list = new ArrayList<>(classEnd - classStart + 1);

            for (int j = classStart; j <= classEnd; ++j) {
                list.set(j - classStart, this.sortedValues.get(j));
            }

            return list;
        }

        /**
         * @param classIndex
         *
         * @return the minimum value (inclusive) of the given class
         */
        public double getClassMin(int classIndex) {
            if (classIndex == 0) {
                return this.sortedValues.get(0);
            } else {
                return this.sortedValues.get(this.breaks.get(classIndex - 1) + 1);
            }
        }

        /**
         * @param classIndex
         *
         * @return the maximum value (inclusive) of the given class
         */
        public double getClassMax(int classIndex) {
            return this.sortedValues.get(this.breaks.get(classIndex));
        }

        public int getClassCount(int classIndex) {
            if (classIndex == 0) {
                return this.breaks.get(0) + 1;
            } else {
                return this.breaks.get(classIndex) - this.breaks.get(classIndex - 1);
            }
        }

        private double mean(List<Double> values) {
            return values.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        }

        public int numClasses() {
            return this.breaks.size();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i != numClasses(); ++i) {
                if (getClassMin(i) == getClassMax(i)) {
                    sb.append(getClassMin(i));
                } else {
                    sb.append(getClassMin(i)).append(" - ").append(getClassMax(i));
                }

                sb.append(" (").append(getClassCount(i)).append(")");
                sb.append(" = ").append(Arrays.toString(classList(i).toArray(Double[]::new)));
                sb.append("\n");
            }

            return sb.toString();
        }

        public String printClusters() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i != numClasses(); ++i) {
                if (getClassMin(i) == getClassMax(i)) {
                    sb.append(getClassMin(i));
                } else {
                    sb.append(getClassMin(i)).append(" - ").append(getClassMax(i));
                }
                sb.append(" (" + getClassCount(i) + ");");
                // sb.append("\n");
            }
            return sb.toString();
        }

        public int classOf(double value) {
            for (int i = 0; i != numClasses(); ++i) {
                if (value <= getClassMax(i)) {
                    return i;
                }
            }
            return numClasses() - 1;
        }
    }
}
