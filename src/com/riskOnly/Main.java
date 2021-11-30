package com.riskOnly;

import com.riskOnly.matrix.*;

import java.util.Scanner;


import static com.riskOnly.matrix.MatrixOperations.printMatrix;
import static com.riskOnly.matrix.MatrixOperations.transposeMatrix;

public class Main {



    private static MatrixGenerator matrixGenerator;

    // TODO: output winning probabilities if i attack and how many troops i am going to loose (expected)




    public static void main(String[] args) {
        /**
        SparseMatrix sparseMatrix = new SparseMatrix(4, 4, 4*4);
        sparseMatrix.insertSparse(1, 2, 10);
        sparseMatrix.insertSparse(1, 4, 12);
        sparseMatrix.insertSparse(3, 3, 5);
        sparseMatrix.insertSparse(4, 1, 15);
        sparseMatrix.insertSparse(4, 2, 12);

        SparseMatrix b = sparseMatrix.multiplySparse(sparseMatrix);
        sparseMatrix.printSparse("1.");
        b.printSparse("transposed");
         **/

        matrixGenerator = new MatrixGenerator(7, 3);
        int attacker = matrixGenerator.getAttacker();
        int defender = matrixGenerator.getDefender();

        double[][] erMatrix = matrixGenerator.getCalculatedERMatrix();
        double[] pzAtt = matrixGenerator.getCalculatedPzAtt();
        double[] pkDef = matrixGenerator.getCalculatedPkDef();
        String[][] generatedERMatrixWithStates = matrixGenerator.getCalculatedERWithStates();




/**
        printMatrix(qMatrix, "Q Matrix: ");
        printMatrix(eMatrix, " E Matrix :");
         printMatrix(rMatrix, "R Matrix");
        printMatrix(fMatrix, "fMatrix ");


        //Testing PkDef and PzAtt


**/
        //printMatrix(erMatrix, "ER Matrix");

        System.out.println("When attacking with " + attacker + " units and defender has " + defender + " units. " +
                "\nexpected remaining attacking units: " + erMatrix[erMatrix.length-1][1] +
                "\nexpected remaining defending units: " + erMatrix[erMatrix.length-1][0] +
                "\nWinning probability that attacker wins: " + pzAtt[pzAtt.length-1] +
                "\nWinning probability that defender wins: " + pkDef[pkDef.length-1]);

        boolean waitingOnInput = true;
        int currentIPosition = 0;

        Scanner userInput = new Scanner(System.in);
        while (waitingOnInput) {
            System.out.println("Please enter attacker and defender losses");
            String input = userInput.nextLine();
            if (input.equals("exit")) {
                waitingOnInput = false;
            } else {
                int attackerLosses = Integer.parseInt(input.substring(0, 1));
                int defenderLosses = Integer.parseInt(input.substring(2, 3));
                if (attacker - attackerLosses <= 0) {
                    System.out.println("Defender has won!");
                    waitingOnInput = false;
                } else if (defender - defenderLosses <= 0) {
                    System.out.println("Attacker has won!");
                } else {
                    for (int i = 0; i < generatedERMatrixWithStates.length; i++) {
                        for (int j = 0; j < generatedERMatrixWithStates[i].length; j++) {
                            System.out.println("generated Matrix current state = " + generatedERMatrixWithStates[i][j] +
                                    " attacker losses and defender losses as state = " + String.valueOf(attacker-attackerLosses) + String.valueOf(defender-defenderLosses));
                            if (generatedERMatrixWithStates[i][j].equals(String.valueOf(attacker-attackerLosses)+String.valueOf(defender-defenderLosses))) {
                                currentIPosition = i;

                            }
                        }
                    }

                }


                System.out.println("When attacking with " + (attacker-attackerLosses) + " units and defender has "
                        + (defender-defenderLosses) + " units. " +
                        "\nexpected remaining attacking units: " + erMatrix[currentIPosition][1] +
                        "\nexpected remaining defending units: " + erMatrix[currentIPosition][0] +
                        "\nWinning probability that attacker wins: " + pzAtt[currentIPosition] +
                        "\nWinning probability that defender wins: " + pkDef[currentIPosition]);
            }
        }



        //testing length of each P vector
        //System.out.println(pkDef.length + " = pk\n" + pzAtt.length + " = pz\n" + sumPzPk.length + " = sum");

    }

}







