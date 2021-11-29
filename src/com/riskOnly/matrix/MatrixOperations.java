package com.riskOnly.matrix;

/**
 * matrix operations like print, fillWithZero, potential, multiply,etc
 *
 */
public class MatrixOperations {

    /**
     * prints a matrix (needed for testing)
     * @param matrix
     * @param name
     */
    public static void printMatrix(double[][] matrix, String name) {
        System.out.println("----------\n" + name + " : ");
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.printf("%12f", matrix[row][col]);
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

    /**
     * multiply a matrix n times with itself (A^n)
     * @param matrix given matrix to multiply
     * @param exponent given exponent (exponent = n in example A^n)
     * @return new double[matrix.length][matrix[0].length] ==> matrix^exponent
     */
    public static double[][] multiplyMatrixNTimes(double[][] matrix, double exponent) {
        double[][] tmp = matrix;
        double[][] sumMatrix = new double[matrix.length][matrix[0].length];
        if (exponent == 0) {
            for (int i = 0; i < matrix.length; i++) {
                for (int j = 0; j < matrix[i].length; j++) {
                    if (i == j) {
                        sumMatrix[i][j] = 1.0;
                    } else{
                        sumMatrix[i][j] = 0.0;
                    }
                }
            }
            tmp = sumMatrix;
        } else {
            for (int i = 0; i <= exponent - 2; i++) {
                sumMatrix = multiplyMatrix(tmp, matrix);
                tmp = sumMatrix;
            }
        }
        return tmp;
    }

    /**
     * multiply two matrices
     * @param matrix1
     * @param matrix2
     * @return
     */
    public static double[][] multiplyMatrix(double[][] matrix1, double[][] matrix2) {
        double[][] result = fillWithZero(matrix1.length, matrix2[0].length);
        for (int i = 0; i < matrix1.length; i++) {
            for (int j = 0; j < matrix2[0].length; j++) {
                for (int k = 0; k < matrix2.length; k++) {
                    result[i][j] += matrix1[i][k] * matrix2[k][j];
                }

            }
        }
        return result;
    }

    public static double[][] transposeMatrix(double[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        double [][] transposedMatrix = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                transposedMatrix[j][i] = matrix[i][j];
            }
        }
        return transposedMatrix;
    }


}
