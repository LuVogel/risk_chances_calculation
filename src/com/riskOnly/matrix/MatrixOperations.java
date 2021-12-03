package com.riskOnly.matrix;

import Jama.Matrix;


/**
 * matrix operations like print, fillWithZero
 *
 */
public class MatrixOperations {

    /**
     * prints a matrix (needed for testing)
     * @param matrix
     * @param name
     */
    public static void printDoubleMatrix(double[][] matrix, String name) {
        System.out.println("----------\n" + name + " : ");
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.printf("%12f", matrix[row][col]);
            }
            System.out.println();
        }
        System.out.println("----------");
    }
    public static void printMatrix(Matrix matrix1, String name) {
        double[][] matrix = matrix1.getArray();
        System.out.println("----------\n" + name + " : ");
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.printf("%12f", matrix[row][col]);
            }
            System.out.println();
        }
        System.out.println("----------");
    }
    public static void printStringMatrix(String[][] matrix, String name) {
        System.out.println("----------\n" + name + " : ");
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.print(matrix[row][col] + " | ");
            }
            System.out.println();
        }
        System.out.println("----------");
    }


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
