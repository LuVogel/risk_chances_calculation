package com.riskOnly.matrix;

import static com.riskOnly.matrix.MatrixOperations.*;

/**
 * generates all vectors and matrices needed
 */
public class MatrixGenerator {
    static double a2d1_to_a1d1 = 0.4213;
    static double a3d1_to_a3d0 = 0.6597;
    static double a3d1_to_aMin1d1 = 0.3403;
    static double a1d2_to_a0d2 = 0.2546;
    static double a1d2_to_a1dMin1 = 0.7454;
    static double a2d2_to_a2dMin2 = 0.1518;
    static double a2d2_to_a0d2 = 0.3725;
    static double a2d2_to_a1dMin1 = 0.4757;
    static double a3d2_to_a3dMin2 = 0.2865;
    static double a3d2_to_aMin2d2 = 0.2074;
    static double a3d2_to_aMin1dMin1 = 0.5061;
    static double a1d1_to_a1ad0 = 0.4167;
    static double a1d1_to_a0d1 = 0.5833;
    static double a2d1_to_a2d0 = 0.5787;

    int attacker, defender;
    double[][] erMatrix;
    String[][] generatedERWithStates;
    double[] pzAtt;
    double[] pkDef;

     public MatrixGenerator(int attacker, int defender) {
         this.attacker = attacker;
         this.defender = defender;
         generateAllMatrices();
     }

     private void generateAllMatrices() {
         long startTime = System.currentTimeMillis();
         double[][] qMatrix = getMatrixQ();
         long afterQ = System.currentTimeMillis();
         System.out.println("generated q, elapsed Time: " + (afterQ - startTime) + " millis seconds");
         double[][] rMatrix = transposeMatrix(getMatrixR());
         long afterR = System.currentTimeMillis();
         System.out.println("generated r, elapsed Time: " + (afterR - afterQ) + " millis");
         double[][] eMatrix = transposeMatrix(getMatrixE());
         long afterE = System.currentTimeMillis();
         System.out.println("generated e, elapsed Time: " + (afterE - afterR) + " millis");
         double[][] fMatrix = getMatrixF(rMatrix, qMatrix);
         long afterF = System.currentTimeMillis();
         System.out.println("generated f, elapsed Time: " + (afterF - afterE) + " millis");
         pzAtt = getProbabilityAttackerWin(fMatrix);
         long afterPAtt = System.currentTimeMillis();
         System.out.println("generated pkAtt, elapsed Time: " + (afterPAtt - afterF) + " millis");
         pkDef = getProbabilityDefenderWin(fMatrix);
         long afterPDef = System.currentTimeMillis();
         System.out.println("generated pzDef, elapsed Time: "+ (afterPDef - afterPAtt) + " millis");
         erMatrix = getMatrixER(fMatrix, eMatrix);
         long afterER = System.currentTimeMillis();
         System.out.println("generated er, elapsed Time: " + (afterER - afterPDef) + " millis");
         generatedERWithStates = generateMatrixForERStates(erMatrix);
         long afterGeneratedStates = System.currentTimeMillis();
         System.out.println("generated erwithstates, elapsed Time: " + (afterGeneratedStates - afterER) + " millis");
     }

     public double[][] getCalculatedERMatrix() {
         return erMatrix;
     }
     public double[] getCalculatedPzAtt(){
         return pzAtt;
     }
     public double[] getCalculatedPkDef() {
         return pkDef;
     }
     public String[][] getCalculatedERWithStates() {
         return generatedERWithStates;
     }
     public int getAttacker() {
         return attacker;
     }
     public int getDefender() {
         return defender;
     }


    /**
     * Q - Matrix calculation
     *
     *
     */
    /**
     * @return [attacker*defender][attacker*defender]: probabilities for transition only between transient states
     */
    public double[][] getMatrixQ() {
        double [][] qMatrix = fillWithZero(attacker*defender, attacker*defender);
        String[][] initialMatrix = getInitialQ();
        String[][] currentTransitionMatrix = getTransitionQ();

        int initialAttacker = 0;
        int initialDefender = 0;
        int transitionDefender = 0;
        int transitionAttacker = 0;
        for (int i = 0; i < qMatrix.length; i++){
            for (int j = 0; j < qMatrix[(i)].length; j++){
                String[] initialArray = initialMatrix[i][j].split(" ");
                String[] transitionArray = currentTransitionMatrix[i][j].split(" ");
                initialAttacker = Integer.parseInt(initialArray[0]);
                initialDefender = Integer.parseInt(initialArray[1]);
                transitionAttacker = Integer.parseInt(transitionArray[0]);
                transitionDefender = Integer.parseInt(transitionArray[1]);

                if (initialAttacker == 1 && initialDefender == 1) {
                    // case 11 -> only absorbing states possible
                    qMatrix[i][j] = 0.0;
                }
                if (initialAttacker == 2 && initialDefender == 1) {
                    if (transitionAttacker == 1 && transitionDefender == 1) {
                        // case 21->11
                        qMatrix[i][j] = a2d1_to_a1d1;
                    }
                }
                if (initialAttacker >= 3 && initialDefender == 1){
                    if (transitionAttacker == initialAttacker - 1 && transitionDefender == 1) {
                        // we're going to state a-1, 1
                        qMatrix[i][j] = a3d1_to_aMin1d1;
                    }
                }
                if (initialAttacker == 1 && initialDefender >= 2) {
                    if (transitionAttacker == 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state 1, d-1
                        qMatrix[i][j] = a1d2_to_a1dMin1;
                    }
                }
                if (initialAttacker == 2 && initialDefender >= 2) {
                    if (transitionAttacker == 2 && transitionDefender == initialDefender - 2) {
                        //we're going to state 2,d-2
                        qMatrix[i][j] = a2d2_to_a2dMin2;
                    } else if (transitionAttacker == 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state 1, d-1
                        qMatrix[i][j] = a2d2_to_a1dMin1;
                    }
                }

                if (initialAttacker >= 3 && initialDefender >= 2) {
                    //attacker has 3 dices, defender 2
                    if (transitionAttacker == initialAttacker && transitionDefender == initialDefender - 2) {
                        //we're going to state a,d-2
                        qMatrix[i][j] = a3d2_to_a3dMin2;
                    }
                    if (transitionAttacker == initialAttacker - 2 && transitionDefender == initialDefender) {
                        // we're going to state a-2, d
                        qMatrix[i][j] = a3d2_to_aMin2d2;
                    }
                    if (transitionAttacker == initialAttacker - 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state a-1, d-1
                        qMatrix[i][j] = a3d2_to_aMin1dMin1;
                    }
                }
            }
        }
        return qMatrix;
    }

    /**
     *
     * @return all possible transition states for current attack/defender count
     */
    private String[][] getTransitionQ() {
        String[][] transitionM = new String[attacker*defender][defender*attacker];
        int secCounter = 0;
        int firstCounter = 0;
        for (int i = 0; i < (attacker*defender); i++) {
            for (int j = 0; j < transitionM[i].length; j++) {
                if (j == 0) {
                    // if we start a new row, let counter be 0
                    secCounter = 0;
                }
                if (secCounter >= defender) {
                    // as soon we reach defenders maximum, reset to 0
                    secCounter = 0;
                    firstCounter++;
                }
                transitionM[i][j] = Integer.toString(firstCounter+1) + " " + Integer.toString(secCounter+1);
                // increase counter for second value in string
                secCounter++;
            }
            // set first value back to zero, since we reached maximum
            firstCounter = 0;

        }
        return transitionM;
    }

    /**
     *

     * @return return all possible initial states up to (A,D)
     */
    private String[][] getInitialQ() {
        String[][] initialM = new String[attacker*defender][defender*attacker];
        int secCounter = 0;
        int firstCounter = 0;
        for (int i = 0; i < (defender * attacker); i++) {
            for (int j = 0; j < initialM[i].length; j++) {
                initialM[i][j] = Integer.toString(firstCounter + 1) + " " + Integer.toString(secCounter + 1);
            }
            // increase second value after each line
            secCounter++;
            if (secCounter >= defender) {
                // defender maximum reached, increase first value and set second back to zero
                secCounter = 0;
                firstCounter++;
            }

        }
        return initialM;
    }

    /**
     * R - Matrix calculations
     */
    /**
     *
     * @return [attacker+defender][attacker*defender]: one-step probabilities from a transient to an absorbing state
     */
    public double[][] getMatrixR() {
        double[][] rMatrix = fillWithZero(attacker+defender, attacker*defender);
        String[][] transitionM = getTransitionR();
        String[][] initialM = getInitialR();
        int currentInitialAttacker = 0;
        int currentInitialDefender = 0;
        int currentTransitionAttacker = 0;
        int currentTransitionDefender = 0;
        for (int i = 0; i < rMatrix.length; i++) {
            for (int j = 0; j < rMatrix[i].length; j++) {
                String[] initialArray = initialM[i][j].split(" ");
                currentInitialAttacker = Integer.parseInt(initialArray[0]);
                currentInitialDefender = Integer.parseInt(initialArray[1]);
                String[] transitionArray = transitionM[i][j].split(" ");
                currentTransitionAttacker = Integer.parseInt(transitionArray[0]);
                currentTransitionDefender = Integer.parseInt(transitionArray[1]);
                if (currentInitialAttacker == 1 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == 1) {
                        // we're going from 11->01
                        rMatrix[i][j] = a1d1_to_a0d1;
                    }
                }
                if (currentInitialAttacker == 1 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 1 && currentTransitionDefender == 0) {
                        // we're going from 11->10
                        rMatrix[i][j] = a1d1_to_a1ad0;
                    }
                }
                if (currentInitialAttacker == 2 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 2 && currentTransitionDefender == 0) {
                        // we're going from 21->20
                        rMatrix[i][j] = a2d1_to_a2d0;
                    }
                }
                if (currentInitialAttacker >= 3 && currentInitialDefender == 1) {
                    if (currentInitialAttacker == currentTransitionAttacker) {
                        if (currentTransitionDefender == 0) {
                            // we're going from a1->a0
                            rMatrix[i][j] = a3d1_to_a3d0;
                        }
                    }
                }
                if (currentInitialAttacker == 1 && currentInitialDefender >= 2) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == currentInitialDefender) {
                        // we're going from 1d->0d
                        rMatrix[i][j] = a1d2_to_a0d2;
                    }
                }
                if (currentInitialAttacker == 2 && currentTransitionDefender >= 2) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == currentInitialDefender){
                        // we're going from 2d->0d
                        rMatrix[i][j] = a2d2_to_a0d2;
                    }
                    if (currentTransitionAttacker == 2 && currentTransitionDefender == currentInitialDefender - 2) {
                        if (currentInitialDefender - 2 == 0) {
                            // we're going from 2d->2 d-2, and d-2 is zero
                            rMatrix[i][j] = a2d2_to_a2dMin2;
                        }
                    }
                }
                if (currentInitialAttacker >= 3 && currentInitialDefender >= 2) {
                    if (currentTransitionAttacker == currentInitialAttacker && currentTransitionDefender == currentInitialDefender - 2) {
                        if (currentInitialDefender - 2 == 0) {
                            // we're going from ad-> a d-2 and d-2 is zero
                            rMatrix[i][j] = a3d2_to_a3dMin2;
                        }
                    }
                }
            }
        }
        return rMatrix;
    }

    /**
     *
     * @return all initial states relevant for R
     */
    private String[][] getInitialR() {
        String[][] initialMatrix = new String[attacker+defender][attacker*defender];
        int secCounter = 0;
        int firstCounter = 0;
        for (int i = 0; i < (attacker+defender); i++) {
            for (int j = 0; j < initialMatrix[i].length; j++) {
                if (j == 0) {
                    secCounter = 0;
                }
                if (secCounter >= defender) {
                    secCounter = 0;
                    firstCounter++;
                }
                initialMatrix[i][j] = Integer.toString(firstCounter + 1) + " " +  Integer.toString(secCounter + 1);
                secCounter++;
            }
            firstCounter = 0;
        }
        return initialMatrix;
    }

    /**
     *
     * @return all transient states relevant for R
     */
    private String[][] getTransitionR() {
        String[][] transitionMatrix = new String[attacker+defender][attacker*defender];
        int secCounter = 1;
        int firstCounter = 0;
        for (int i = 0; i < (attacker+defender); i++) {
            for (int j = 0; j < transitionMatrix[i].length; j++) {
                transitionMatrix[i][j] = Integer.toString(firstCounter) + " " + Integer.toString(secCounter);
            }
            if (secCounter >= defender) {
                secCounter = 0;
                firstCounter++;
            } else if (secCounter != 0){
                secCounter++;
            } else {
                firstCounter++;
            }
        }
        return transitionMatrix;
    }

    /**
     * ER - Matrix calculation
     */
    /**
     *
     * @param fMatrix double[attacker*defender][attacker+defender], probabilities for starting in a transient state,
     *                landing in an absorbing state
     * @param eMatrix double[attacker+defender][2], all transient state ([1...0D...0]; [0...1...A])
     * @return [attacker * defender][2] expected remaining armies (first column == defender, second column == attacker)
     *
     */
    public double[][] getMatrixER(double[][] fMatrix, double[][] eMatrix) {
        return multiplyMatrix(fMatrix, eMatrix);
    }

    public String[][] generateMatrixForERStates(double[][] erMatrix) {
        String[][] generatedStateMatrix = new String[erMatrix.length][1];
        int firstDigit = 1;
        int secondDigit = 1;
        for (int i = 0; i < generatedStateMatrix.length; i++) {
            generatedStateMatrix[i][0] = String.valueOf(firstDigit) + String.valueOf(secondDigit);
            if (secondDigit >= defender) {
                secondDigit = 1;
                firstDigit++;
            } else {
                secondDigit++;

            }
            if (firstDigit > attacker) {
                firstDigit = 1;
            }
        }

        return generatedStateMatrix;
    }

    /**
     * Probability that one side wins
     */
    /**
     *
     * @param erMatrix [attacker*defender][2] expected remaining armies
     * @return probabilities that defender wins for start states (11,...,1D,21,...,2D,...,A1,...,AD)
     */
    public double[] getProbabilityDefenderWin(double[][] erMatrix) {
        double[] pKDef = new double[erMatrix.length];
        for (int i = 0; i < defender; i++) {
            for (int j = 0; j < erMatrix.length; j++) {
                pKDef[j] += erMatrix[j][i];
            }
        }
        return pKDef;
    }

    /**
     *
     * @param erMatrix [attacker*defender][2] expected remaining armies
     * @return probabilities that attacker wins for start states (11,...,1D,21,...,2D,...,A1,...,AD)
     */
    public double[] getProbabilityAttackerWin(double[][] erMatrix) {
        double[] pzAtt = new double[erMatrix.length];
        for (int i = defender; i < attacker+defender; i++) {
            for (int j = 0; j < erMatrix.length; j++) {
                pzAtt[j] += erMatrix[j][i];
            }
        }
        return pzAtt;
    }
    /**
     * F - Matrix calculations
     */
    /**
     *
     * @param rMatrix
     * @param qMatrix
     * @return double[attacker*defender][attacker+defender], probabilities for starting in a transient state,
     *      *  landing in an absorbing state
     */
    public double[][] getMatrixF(double[][] rMatrix, double[][] qMatrix) {
        double[][] fMatrix = fillWithZero(attacker*defender, attacker+defender);
        for (int n = 1; n < attacker+defender; n++) {
            double[][] expMatrix = multiplyMatrixNTimes(qMatrix, n-1);
            double[][] multiplyQRMatrix = multiplyMatrix(expMatrix, rMatrix);
            for (int i = 0; i < fMatrix.length; i++) {
                for (int j = 0; j < fMatrix[i].length; j++) {
                    fMatrix[i][j] += multiplyQRMatrix[i][j];
                }
            }
        }
        return fMatrix;
    }

    /**
     * E - Matrix calculations
     */
    /**
     * @return double[attacker+defender][2], all transient state ([1...0D...0]; [0...1...A])
     */
    public double[][] getMatrixE() {
        double[][] eMatrix = new double[2][attacker+defender];
        int countFirstRow = 1;
        int countSecRow = 0;
        for (int i = 0; i < eMatrix[0].length; i++) {
            // fill first row (1...D and then 0)
            if (countFirstRow > defender) {
                countFirstRow = 0;
            }
            eMatrix[0][i] = countFirstRow;
            if (countFirstRow != 0) {
                countFirstRow++;
            }
        }
        for (int i = 0; i < eMatrix[1].length; i++) {
            // fill second row (zeros until D is reached, then 1...A)
            if (i >= defender) {
                countSecRow++;
            }
            eMatrix[1][i] = countSecRow;
        }
        return eMatrix;
    }


}
