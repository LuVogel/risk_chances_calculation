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
        SparseMatrix a = new SparseMatrix(4, 4, 4*4);


        SparseMatrix b = new SparseMatrix(4, 4, 4*4);

        a.insertSparse(1, 2, 10);
        a.insertSparse(1, 4, 12);
        a.insertSparse(3, 3, 5);
        a.insertSparse(4, 1, 15);
        a.insertSparse(4, 2, 12);
        b.insertSparse(1, 3, 8);
        b.insertSparse(2, 4, 23);
        b.insertSparse(3, 3, 9);
        b.insertSparse(4, 1, 20);
        b.insertSparse(4, 2, 25);



        SparseMatrix b1 = a.add(b);

        a.printSparse("1.");
        b.printSparse("2.");
        b1.printSparse("add");

**/
        matrixGenerator = new MatrixGenerator(33, 33);
        int attacker = matrixGenerator.getAttacker();
        int defender = matrixGenerator.getDefender();

        double[] erAtt = matrixGenerator.getEr_att();
        double[] erDef = matrixGenerator.getEr_def();

        double[] pkDef = matrixGenerator.getCalculatedPkDef();
        double[] pzAtt = matrixGenerator.getCalculatedPzAtt();

        String[][] generatedERMatrixWithStates = matrixGenerator.getCalculatedERWithStates();

        /*
        System.out.println("dimension: " + pzAtt.length);
        for (int i = 0; i < pzAtt.length; i++) {
            System.out.println("att: = " + pzAtt[i] + ", deff: = " + pkDef[i]);
        }


        System.out.println(erAtt[erAtt.length-1]);
        System.out.println(erDef[erDef.length-1]);
*/

/**
        printMatrix(qMatrix, "Q Matrix: ");
        printMatrix(eMatrix, " E Matrix :");
         printMatrix(rMatrix, "R Matrix");
        printMatrix(fMatrix, "fMatrix ");


        //Testing PkDef and PzAtt


**/


        System.out.println("When attacking with " + attacker + " units and defender has " + defender + " units. " +
                "\nexpected remaining attacking units: " +  erAtt[erAtt.length-1] +
                "\nexpected remaining defending units: " + erDef[erDef.length-1] +
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

/**
                System.out.println("When attacking with " + (attacker-attackerLosses) + " units and defender has "
                        + (defender-defenderLosses) + " units. " +
                        "\nexpected remaining attacking units: " + sparseER[currentIPosition][1] +
                        "\nexpected remaining defending units: " + sparseER[currentIPosition][0] +
                        "\nWinning probability that attacker wins: " + pzAtt[currentIPosition] +
                        "\nWinning probability that defender wins: " + pkDef[currentIPosition]);
            **/
            }
        }



        //testing length of each P vector
        //System.out.println(pkDef.length + " = pk\n" + pzAtt.length + " = pz\n" + sumPzPk.length + " = sum");

    }

}







