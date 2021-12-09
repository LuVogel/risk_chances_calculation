package com.riskOnly.matrix;

import Jama.Matrix;

import java.util.Arrays;

import static com.riskOnly.matrix.MatrixOperations.*;

/**
 * generates all vectors and matrices needed
 */
public class MatrixGenerator {

    // calculations for transition probabilities are done in the matlab file: transition_probabilities.m
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


    // all information needed to write into file
    int attacker, defender;
    double[][] erMatrix;
    double[] pzAtt;
    double[] pkDef;

    /**
     * as soon constructor is called, matrices will be calculated
     * @param attacker count of maximum attacker
     * @param defender count of maximum defender
     */
     public MatrixGenerator(int attacker, int defender) {
         this.attacker = attacker;
         this.defender = defender;
         generateAllMatrices();
     }

    /**
     * generate all matrices needed for the information to write into the text-file
     */
    private void generateAllMatrices() {
         Matrix qMatrix = getMatrixQ();
         Matrix rMatrix = getMatrixR();
         Matrix eMatrix = getMatrixE();

         // comment time: the calculation for the fMatrix needs the most time, (especially for larger inputs > 30)
         // long time = System.currentTimeMillis();
         Matrix fMatrix = getMatrixF(rMatrix, qMatrix);
         // long afterF = System.currentTimeMillis();
         // System.out.println("generated f, elapsed Time: " + (afterF - time) + " millis");

         pzAtt = getProbabilityAttackerWin(fMatrix.getArray());
         pkDef = getProbabilityDefenderWin(fMatrix.getArray());
         erMatrix = getMatrixER(fMatrix, eMatrix);
     }

     //All getters needed for file
     public double[][] getCalculatedERMatrix() {
         return erMatrix;
     }
     public double[] getCalculatedPzAtt(){
         return pzAtt;
     }
     public double[] getCalculatedPkDef() {
         return pkDef;
     }
     public int getAttacker() {
         return attacker;
     }
     public int getDefender() {
         return defender;
     }


    /**
     * @return [attacker*defender][attacker*defender]: probabilities for transition only between transient states
     */
    public Matrix getMatrixQ() {
        double [][] qArray = fillWithZero(attacker*defender, attacker*defender);
        // store all initial states
        String[][] initialMatrix = getInitialQ();
        // store all transition states
        String[][] currentTransitionMatrix = getTransitionQ();

        int initialAttacker;
        int initialDefender;
        int transitionDefender;
        int transitionAttacker;
        for (int i = 0; i < qArray.length; i++){
            for (int j = 0; j < qArray[(i)].length; j++){
                String[] initialArray = initialMatrix[i][j].split(" ");
                String[] transitionArray = currentTransitionMatrix[i][j].split(" ");
                initialAttacker = Integer.parseInt(initialArray[0]);
                initialDefender = Integer.parseInt(initialArray[1]);
                transitionAttacker = Integer.parseInt(transitionArray[0]);
                transitionDefender = Integer.parseInt(transitionArray[1]);

                if (initialAttacker == 1 && initialDefender == 1) {
                    // case 11 -> only absorbing states possible
                    qArray[i][j] = 0.0;
                }
                if (initialAttacker == 2 && initialDefender == 1) {
                    if (transitionAttacker == 1 && transitionDefender == 1) {
                        // case 21->11
                        qArray[i][j] = a2d1_to_a1d1;
                    }
                }
                if (initialAttacker >= 3 && initialDefender == 1){
                    if (transitionAttacker == initialAttacker - 1 && transitionDefender == 1) {
                        // we're going to state a-1, 1
                        qArray[i][j] = a3d1_to_aMin1d1;
                    }
                }
                if (initialAttacker == 1 && initialDefender >= 2) {
                    if (transitionAttacker == 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state 1, d-1
                        qArray[i][j] = a1d2_to_a1dMin1;
                    }
                }
                if (initialAttacker == 2 && initialDefender >= 2) {
                    if (transitionAttacker == 2 && transitionDefender == initialDefender - 2) {
                        //we're going to state 2,d-2
                        qArray[i][j] = a2d2_to_a2dMin2;
                    } else if (transitionAttacker == 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state 1, d-1
                        qArray[i][j] = a2d2_to_a1dMin1;
                    }
                }

                if (initialAttacker >= 3 && initialDefender >= 2) {
                    //attacker has 3 dices, defender 2
                    if (transitionAttacker == initialAttacker && transitionDefender == initialDefender - 2) {
                        //we're going to state a,d-2
                        qArray[i][j] = a3d2_to_a3dMin2;
                    }
                    if (transitionAttacker == initialAttacker - 2 && transitionDefender == initialDefender) {
                        // we're going to state a-2, d
                        qArray[i][j] = a3d2_to_aMin2d2;
                    }
                    if (transitionAttacker == initialAttacker - 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state a-1, d-1
                        qArray[i][j] = a3d2_to_aMin1dMin1;
                    }
                }
            }
        }
        return new Matrix(qArray);
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
                transitionM[i][j] = (firstCounter+1) + " " + (secCounter+1);
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
            Arrays.fill(initialM[i], (firstCounter + 1) + " " + (secCounter + 1));
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

    // next few methods are need for calculating R-matrix
    /**
     *
     * @return [attacker+defender][attacker*defender]: one-step probabilities from a transient to an absorbing state
     */
    public Matrix getMatrixR() {
        double[][] rArray = fillWithZero(attacker*defender, attacker+defender);
        String[][] transitionM = getTransitionR();
        String[][] initialM = getInitialR();
        int currentInitialAttacker;
        int currentInitialDefender;
        int currentTransitionAttacker;
        int currentTransitionDefender;
        for (int i = 0; i < rArray.length; i++) {
            for (int j = 0; j < rArray[i].length; j++) {
                String[] initialArray = initialM[i][j].split(" ");
                currentInitialAttacker = Integer.parseInt(initialArray[0]);
                currentInitialDefender = Integer.parseInt(initialArray[1]);
                String[] transitionArray = transitionM[i][j].split(" ");
                currentTransitionAttacker = Integer.parseInt(transitionArray[0]);
                currentTransitionDefender = Integer.parseInt(transitionArray[1]);
                if (currentInitialAttacker == 1 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == 1) {
                        // we're going from 11->01
                        rArray[i][j] = a1d1_to_a0d1;
                    }
                }
                if (currentInitialAttacker == 1 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 1 && currentTransitionDefender == 0) {
                        // we're going from 11->10
                        rArray[i][j] = a1d1_to_a1ad0;
                    }
                }
                if (currentInitialAttacker == 2 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 2 && currentTransitionDefender == 0) {
                        // we're going from 21->20
                        rArray[i][j] = a2d1_to_a2d0;
                    }
                }
                if (currentInitialAttacker >= 3 && currentInitialDefender == 1) {
                    if (currentInitialAttacker == currentTransitionAttacker) {
                        if (currentTransitionDefender == 0) {
                            // we're going from a1->a0
                            rArray[i][j] = a3d1_to_a3d0;
                        }
                    }
                }
                if (currentInitialAttacker == 1 && currentInitialDefender >= 2) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == currentInitialDefender) {
                        // we're going from 1d->0d
                        rArray[i][j] = a1d2_to_a0d2;
                    }
                }
                if (currentInitialAttacker == 2 && currentTransitionDefender >= 2) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == currentInitialDefender){
                        // we're going from 2d->0d
                        rArray[i][j] = a2d2_to_a0d2;
                    }
                    if (currentTransitionAttacker == 2 && currentTransitionDefender == currentInitialDefender - 2) {
                        if (currentInitialDefender - 2 == 0) {
                            // we're going from 2d->2 d-2, and d-2 is zero
                            rArray[i][j] = a2d2_to_a2dMin2;
                        }
                    }
                }
                if (currentInitialAttacker >= 3 && currentInitialDefender >= 2) {
                    if (currentTransitionAttacker == currentInitialAttacker && currentTransitionDefender == currentInitialDefender - 2) {
                        if (currentInitialDefender - 2 == 0) {
                            // we're going from ad-> a d-2 and d-2 is zero
                            rArray[i][j] = a3d2_to_a3dMin2;
                        }
                    }
                }
            }
        }

        return new Matrix(rArray);
    }

    /**
     *
     * @return all initial states relevant for R
     */
    private String[][] getInitialR() {
        String[][] initialMatrix = new String[attacker*defender][attacker+defender];
        int secCounter = 0;
        int firstCounter = 0;
        for (int i = 0; i < attacker*defender; i++) {
            for (int j = 0; j < (attacker+defender); j++) {
                // fill states
                initialMatrix[i][j] = (firstCounter + 1) + " " + (secCounter + 1);
            }
            // going to next row
            secCounter++;
            if (secCounter >= defender) {
                secCounter = 0;
                firstCounter++;
            }

        }
        return initialMatrix;
    }

    /**
     *
     * @return all transient states relevant for R
     */
    private String[][] getTransitionR() {
        String[][] transitionMatrix = new String[attacker*defender][attacker+defender];
        int secCounter = 1;
        int firstCounter = 0;
        for (int i = 0; i < (attacker*defender); i++) {
            for (int j = 0; j < attacker+defender; j++) {
                if (j == 0) {
                    // starting new row
                    secCounter = 1;
                    firstCounter = 0;
                }
                transitionMatrix[i][j] = firstCounter + " " + secCounter;
                if (secCounter >= defender || secCounter == 0) {
                    secCounter = 0;
                    firstCounter++;
                } else {
                    secCounter++;
                }
            }
        }
        return transitionMatrix;
    }

     /**
     *
     * @param fMatrix double[attacker*defender][attacker+defender], probabilities for starting in a transient state,
     *                landing in an absorbing state
     * @param eMatrix double[attacker+defender][2], all transient state ([1...0D...0]; [0...1...A])
     * @return [attacker * defender][2] expected remaining armies (first column == defender, second column == attacker)
     *
     */
    public double[][] getMatrixER(Matrix fMatrix, Matrix eMatrix) {
        double[][] temp = fMatrix.times(eMatrix).getArray();
        for (int i = 0; i < temp.length; i++) {
            for (int j = 0; j < temp[0].length; j++) {
                temp[i][j] = Math.round(temp[i][j] * 100.0) / 100.0;
            }
        }
        return temp;
    }

    /**
     *
     * @param fMatrix [attacker*defender][2] expected remaining armies
     * @return probabilities that defender wins for start states (11,...,1D,21,...,2D,...,A1,...,AD)
     */
    public double[] getProbabilityDefenderWin(double[][] fMatrix) {
        double[] pk_def_temp = new double[fMatrix.length];
        for (int i = 0; i < defender; i++) {
            for (int j = 0; j < fMatrix.length; j++) {
                pk_def_temp[j] += fMatrix[j][i];
            }
        }
        for (int i = 0; i < fMatrix.length; i++) {
            pk_def_temp[i] = Math.round((pk_def_temp[i] * 100) * 100.0) / 100.0;
        }
        return pk_def_temp;
    }

    /**
     *
     * @param fMatrix [attacker*defender][2] expected remaining armies
     * @return probabilities that attacker wins for start states (11,...,1D,21,...,2D,...,A1,...,AD)
     */
    public double[] getProbabilityAttackerWin(double[][] fMatrix) {
        double[] pz_att_temp = new double[fMatrix.length];
        for (int i = defender; i < attacker+defender; i++) {
            for (int j = 0; j < fMatrix.length; j++) {
                pz_att_temp[j] += fMatrix[j][i];
            }
        }
        for (int i = 0; i < fMatrix.length; i++) {
            pz_att_temp[i] = Math.round((pz_att_temp[i] * 100) * 100.0) / 100.0;
        }
        return pz_att_temp;
    }

    /**
     * calculating F-matrix, needs most time
     * @param rMatrix see getMatrixR
     * @param qMatrix getMatrixQ
     * @return double[attacker*defender][attacker+defender], probabilities for starting in a transient state,
     *      *  landing in an absorbing state
     */
    public Matrix getMatrixF(Matrix rMatrix, Matrix qMatrix) {
        Matrix identityQMatrix = Matrix.identity(qMatrix.getRowDimension(), qMatrix.getRowDimension());
        Matrix subtractIQ = identityQMatrix.minus(qMatrix);
        Matrix inverseOfSubtractIQ = subtractIQ.inverse();
        return inverseOfSubtractIQ.times(rMatrix);
    }

    /**
     * @return double[attacker+defender][2], all transient state ([1...0D...0]; [0...1...A])
     */
    public Matrix getMatrixE() {
        double[][] eArray = new double[2][attacker+defender];
        int countFirstRow = 1;
        int countSecRow = 0;
        for (int i = 0; i < eArray[0].length; i++) {
            // fill first row (1...D and then 0)
            if (countFirstRow > defender) {
                countFirstRow = 0;
            }
            eArray[0][i] = countFirstRow;
            if (countFirstRow != 0) {
                countFirstRow++;
            }
        }
        for (int i = 0; i < eArray[1].length; i++) {
            // fill second row (zeros until D is reached, then 1...A)
            if (i >= defender) {
                countSecRow++;
            }
            eArray[1][i] = countSecRow;
        }
        Matrix eMatrix = new Matrix(eArray);
        return eMatrix.transpose();
    }


}
