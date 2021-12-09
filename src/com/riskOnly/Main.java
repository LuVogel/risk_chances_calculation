package com.riskOnly;

import com.riskOnly.matrix.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Main {

    private static MatrixGenerator matrixGenerator;

    /**
     * run for creating a text-file
     * file stores all states from 0...75 attackers and 0..75 defender
     * including probabilities attacker/defender win
     * including expected remaining armies attacker/defender
     * @param args
     */
    public static void main(String[] args) {
        //create new instance of MatrixGenerator
        //creates all matrices and vectors needed for text-file
        //change parameters for other maximal attacker/defender
        //maximum is 75x75 = 5625, therefore 100x50=5500 would also be possible
        matrixGenerator = new MatrixGenerator(7, 7);
        //
        int attacker = matrixGenerator.getAttacker();
        int defender = matrixGenerator.getDefender();

        double[][] erMatrix = matrixGenerator.getCalculatedERMatrix();
        double[] pzAtt = matrixGenerator.getCalculatedPzAtt();
        double[] pkDef = matrixGenerator.getCalculatedPkDef();
        // generate for every input a new file
        String fileName = "probability_table_" + attacker + "_" + defender;


        try {
            // try to create a new file if not already exists
            File file = new File(fileName);
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
            // write attackCount, defendCount, expectedRemainingAttacker, expectedRemainingDefender,
            // probabilityAttackerWin, probabilityDefenderWin

            FileWriter writer = new FileWriter(fileName);
            int attack_count = 1;
            int defend_count = 1;
            for (int i = 0; i < erMatrix.length; i++) {
                writer.write(attack_count + " " + defend_count + " " + erMatrix[i][1] + " " + erMatrix[i][0] +
                        " " + pzAtt[i] + " " + pkDef[i] + "\n");
                //file is filled with in form (attacker,defender) => (1,1), (1,2), ...,
                // (1,75), (2,1), ...(2,75), ... (75,1), ..., (75,75)
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
    }
}







