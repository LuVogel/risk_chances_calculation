package com.riskOnly;

import com.riskOnly.matrix.*;

import java.util.Scanner;


import static com.riskOnly.matrix.MatrixOperations.printMatrix;
import static com.riskOnly.matrix.MatrixOperations.transposeMatrix;

public class Main {



    private static MatrixGenerator matrixGenerator;

    // TODO: output winning probabilities if i attack and how many troops i am going to loose (expected)




    public static void main(String[] args) {
        int attacker = 7;
        int defender = 3;
        matrixGenerator = new MatrixGenerator(7, 3);
        System.out.println("generated class");
        double[][] qMatrix = matrixGenerator.getMatrixQ(attacker, defender);
        System.out.println("generated q");

        // rMatrix transposed, since it is filled in other direction
        double[][] rMatrix = transposeMatrix(matrixGenerator.getMatrixR(attacker,defender));
        System.out.println("generated r");
        // eMatrix transposed
        double[][] eMatrix = transposeMatrix(matrixGenerator.getMatrixE(attacker,defender));
        System.out.println("generated e");
        double[][] fMatrix = matrixGenerator.getMatrixF(attacker,defender,rMatrix,qMatrix);
        System.out.println("generated f");
        double[] pkDef = matrixGenerator.getProbabilityDefenderWin(fMatrix, defender);
        System.out.println("generated pkdef");
        double[] pzAtt = matrixGenerator.getProbabilityAttackerWin(fMatrix, attacker, defender);
        System.out.println("generated pkatt");

        double[][] erMatrix = matrixGenerator.getMatrixER(fMatrix, eMatrix);
        System.out.println("generated er");
        String[][] generatedERMatrixWithStates = matrixGenerator.generateMatrixForERStates(erMatrix, attacker, defender);
        System.out.println("generated erwithstates");





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
        int currentJPosition = 0;

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
                                currentJPosition = j;
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
        String[][] expectedLosses = matrixGenerator.generateMatrixForERStates(erMatrix, attacker, defender);


        //testing length of each P vector
        //System.out.println(pkDef.length + " = pk\n" + pzAtt.length + " = pz\n" + sumPzPk.length + " = sum");

    }

}







