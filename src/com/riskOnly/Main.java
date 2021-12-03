package com.riskOnly;

import com.riskOnly.matrix.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;



public class Main {



    private static MatrixGenerator matrixGenerator;





    public static void main(String[] args) {
        matrixGenerator = new MatrixGenerator(75, 75);
        int attacker = matrixGenerator.getAttacker();
        int defender = matrixGenerator.getDefender();

        double[][] erMatrix = matrixGenerator.getCalculatedERMatrix();
        double[] pzAtt = matrixGenerator.getCalculatedPzAtt();
        double[] pkDef = matrixGenerator.getCalculatedPkDef();
        String[][] generatedERMatrixWithStates = matrixGenerator.getCalculatedERWithStates();

        try {
            File file = new File("probability_table.txt");
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter("probability_table.txt");
            int attack_count = 1;
            int defend_count = 1;
            for (int i = 0; i < erMatrix.length; i++) {
                writer.write(attack_count + " " + defend_count + " " +erMatrix[i][1] + " " + erMatrix[i][0] +
                        " " + pzAtt[i] + " " + pkDef[i] + "\n");
                if (defend_count < defender) {
                    defend_count++;
                } else if (defend_count == defender) {
                    defend_count = 1;
                    attack_count++;
                }
            }
            writer.close();
            System.out.println("successfully wrote to file");

        } catch (IOException e) {
             System.out.println("an error occurred.");
             e.printStackTrace();
        }


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
                "\nWinning probability that attacker wins: " + pzAtt[pzAtt.length-1] + "%" +
                "\nWinning probability that defender wins: " + pkDef[pkDef.length-1] + "%");

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







