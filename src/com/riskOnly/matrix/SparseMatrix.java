package com.riskOnly.matrix;

public class SparseMatrix {

    int max;
    int row, col, len;
    double[][] matrix;

    public SparseMatrix(int r, int c, int max) {
        this.row = r;
        this.col = c;
        this.len = 0;
        matrix = new double[max][3];

    }

    public void insertSparse(int r, int c, double value) {
        if (r > row || c > col) {
            System.out.println("insertion not possible, wrong entry");
            System.exit(-1);
        } else {
            matrix[len][0] = r;
            matrix[len][1] = c;
            matrix[len][2] = value;
            len++;
        }
    }

    public SparseMatrix add(SparseMatrix sparseMatrix) {
        if (row != sparseMatrix.row || col != sparseMatrix.col) {
            System.out.println("addition not possible");
            System.exit(-1);
            return null;
        } else {
            int pos_1 = 0;
            int pos_2 = 0;
            SparseMatrix result = new SparseMatrix(row, col, (sparseMatrix.len + len));

            while (pos_1 < len && pos_2 < sparseMatrix.len) {
                if (matrix[pos_1][0] > sparseMatrix.matrix[pos_2][0] ||
                        (matrix[pos_1][0] == sparseMatrix.matrix[pos_2][0] &&
                                matrix[pos_1][1] > sparseMatrix.matrix[pos_2][1])) {
                    result.insertSparse((int)sparseMatrix.matrix[pos_2][0],
                            (int)sparseMatrix.matrix[pos_2][1],
                            sparseMatrix.matrix[pos_2][2]);
                    pos_2++;
                } else if (matrix[pos_1][0] < sparseMatrix.matrix[pos_2][0] ||
                        (matrix[pos_1][0] == sparseMatrix.matrix[pos_2][0] &&
                                matrix[pos_1][1] < sparseMatrix.matrix[pos_2][1])) {
                    result.insertSparse((int)matrix[pos_1][0],
                            (int)matrix[pos_1][1],
                            matrix[pos_2][2]);
                    pos_1++;
                } else {
                    double sum = matrix[pos_1][2] + sparseMatrix.matrix[pos_2][2];
                    if (sum != 0) {
                        result.insertSparse((int)matrix[pos_1][0],
                                (int)matrix[pos_1][1],
                                sum);
                    }
                    pos_1++;
                    pos_2++;
                }
            }
            while (pos_1 < len) {
                result.insertSparse((int)matrix[pos_1][0],
                        (int)matrix[pos_1][1],
                        matrix[pos_1++][2]);
            }
            while (pos_2 < sparseMatrix.len) {
                result.insertSparse((int)sparseMatrix.matrix[pos_2][0],
                        (int)sparseMatrix.matrix[pos_2][1],
                        sparseMatrix.matrix[pos_2++][2]);
            }
            return result;
        }

    }

    public SparseMatrix transposeSparse() {
        SparseMatrix result = new SparseMatrix(col, row, len);
        result.len = len;
        int count[] = new int[col+1];
        for (int i = 1; i <= col; i++) {
            count[i] = 0;
        }
        for (int i = 0; i < len; i++) {
            count[(int)matrix[i][1]]++;
        }
        int[] index = new int[col+1];
        index[1] = 0;
        for (int i = 2; i <= col; i++) {
            index[i] = index[i-1] + count[i-1];
        }
        for (int i = 0; i < len; i++) {
            int r_pos = index[(int)matrix[i][1]]++;
            result.matrix[r_pos][0] = matrix[i][1];
            result.matrix[r_pos][1] = matrix[i][0];
            result.matrix[r_pos][2] = matrix[i][2];
        }
        return result;
    }

    public SparseMatrix multiplySparse(SparseMatrix sparseMatrix) {
        if (col != sparseMatrix.row) {
            System.out.println("multiplication not possible, invalid dimensions");
            System.exit(-1);
            return null;
        } else {
            sparseMatrix = sparseMatrix.transposeSparse();
            int pos_1, pos_2;
            SparseMatrix result = new SparseMatrix(row, sparseMatrix.row, (len * sparseMatrix.len));

            for (pos_1 = 0; pos_1 < len;) {
                int current_row = (int)matrix[pos_1][0];
                for (pos_2 = 0; pos_2 < sparseMatrix.len;) {
                    int current_column = (int)sparseMatrix.matrix[pos_2][0];

                    int temp_1 = pos_1;
                    int temp_2 = pos_2;

                    double sum = 0;

                    while (temp_1 < len && (int)matrix[temp_1][0] == current_row
                        && temp_2 < sparseMatrix.len && (int)sparseMatrix.matrix[temp_2][0] == current_column) {
                        if (matrix[temp_1][1] < sparseMatrix.matrix[temp_2][1]){
                            temp_1++;
                        } else if (matrix[temp_1][1] > sparseMatrix.matrix[temp_2][1]) {
                            temp_2++;
                        } else {
                            sum += matrix[temp_1++][2] * sparseMatrix.matrix[temp_2++][2];
                        }
                    }
                    if (sum != 0) {
                        result.insertSparse(current_row, current_column, sum);
                    }

                    while (pos_2 < sparseMatrix.len && (int)sparseMatrix.matrix[pos_2][0] == current_column) {
                        pos_2++;
                    }
                }
                while (pos_1 < len && (int)matrix[pos_1][0] == current_row) {
                    pos_1++;
                }
            }
        return result;
        }
    }

    public void printSparse(String name) {
        System.out.println("Dimension: " + row + "x" + col + "\nSparse Matrix ----> " + name + "\nRow Column Value");
        for (int i = 0; i < len; i++) {
            System.out.println(matrix[i][0] + " " + matrix[i][1] + " " + matrix[i][2]);
        }
    }

}
