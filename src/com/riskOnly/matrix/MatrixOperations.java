package com.riskOnly.matrix;




/**
 * matrix operations like print, fillWithZero
 *
 */
public class MatrixOperations {

    /**
     * fills matrix with zero
     * @param length
     * @param height
     * @return double[length][height] zero matrix
     */
    public static double[][] fillWithZero(int length, int height) {
        double[][] temp = new double[length][height];
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < temp[i].length; j++) {
                temp[i][j] = 0;
            }
        }
        return temp;
    }
    }
