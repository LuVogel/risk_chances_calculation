package com.riskOnly.matrix;

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
    String[][] generatedERWithStates;
    double[] pzAtt;
    double[] pkDef;
    double[] er_def;
    double[] er_att;
    public int position_helper;

     public MatrixGenerator(int attacker, int defender) {
         this.attacker = attacker;
         this.defender = defender;
         generateAllMatrices();
     }

     private void generateAllMatrices() {
         long startTime = System.currentTimeMillis();
         SparseMatrix sparseQ = generatedSparseQ();


         long afterQ = System.currentTimeMillis();
         System.out.println("generated q, elapsed Time: " + (afterQ - startTime) + " millis seconds");


         SparseMatrix sparseR = generateSparseR();
         long afterR = System.currentTimeMillis();
         System.out.println("generated r, elapsed Time: " + (afterR - afterQ) + " millis");


         SparseMatrix sparseE = generateSparseE();
         long afterE = System.currentTimeMillis();
         System.out.println("generated e, elapsed Time: " + (afterE - afterR) + " millis");


         SparseMatrix sparseF = generateSparseF(sparseR, sparseQ);
         long afterF = System.currentTimeMillis();
         System.out.println("generated f, elapsed Time: " + (afterF - afterE) + " millis");

         pkDef = getProbabilityDefenderWin(sparseF);
         long afterPDef = System.currentTimeMillis();
         System.out.println("generated pzDef, elapsed Time: "+ (afterPDef - afterF) + " millis");

         int position_helper = getPosition_helper();
         pzAtt = getProbabilityAttackerWin(sparseF, position_helper);
         long afterPAtt = System.currentTimeMillis();
         System.out.println("generated pkAtt, elapsed Time: " + (afterPAtt - afterPDef) + " millis");





         SparseMatrix sparseER = generateSparseER(sparseF, sparseE);

         er_def = generateDefenderER(sparseER);
         er_att = generateAttackerER(sparseER);
         long afterER = System.currentTimeMillis();
         System.out.println("generated er, elapsed Time: " + (afterER - afterPDef) + " millis");

         generatedERWithStates = generateMatrixForERStates(sparseER);
         long afterGeneratedStates = System.currentTimeMillis();
         System.out.println("generated erwithstates, elapsed Time: " + (afterGeneratedStates - afterER) + " millis");
     }

     public double[] generateDefenderER(SparseMatrix sparseER) {
         int position = 0;
         int res_counter = 0;
         double[] res = new double[sparseER.len / 2];
         while (position < sparseER.len) {
             res[res_counter] = sparseER.matrix[position][2];
             position++;
             position++;
             res_counter++;
         }
         return res;
     }

     public double[] generateAttackerER(SparseMatrix sparseER) {
         int position = 1;
         int res_counter = 0;
         double[] res = new double[sparseER.len / 2];
         while (position < sparseER.len) {
             res[res_counter] = sparseER.matrix[position][2];
             position++;
             position++;
             res_counter++;
         }
         return res;
     }

    public double[] getEr_def() {
         return er_def;
    }

    public double[] getEr_att() {
         return er_att;
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
    public SparseMatrix generatedSparseQ() {
        SparseMatrix sparseQ = new SparseMatrix(attacker*defender, attacker*defender, (attacker*defender)*(attacker*defender));
        String[][] initialMatrix = getInitialQ();
        String[][] currentTransitionMatrix = getTransitionQ();

        int initialAttacker = 0;
        int initialDefender = 0;
        int transitionDefender = 0;
        int transitionAttacker = 0;
        for (int i = 0; i < sparseQ.col; i++){
            for (int j = 0; j < sparseQ.row; j++){
                String[] initialArray = initialMatrix[i][j].split(" ");
                String[] transitionArray = currentTransitionMatrix[i][j].split(" ");
                initialAttacker = Integer.parseInt(initialArray[0]);
                initialDefender = Integer.parseInt(initialArray[1]);
                transitionAttacker = Integer.parseInt(transitionArray[0]);
                transitionDefender = Integer.parseInt(transitionArray[1]);

                if (initialAttacker == 2 && initialDefender == 1) {
                    if (transitionAttacker == 1 && transitionDefender == 1) {
                        // case 21->11
                        sparseQ.insertSparse(i, j, a2d1_to_a1d1);
                    }
                } else if (initialAttacker >= 3 && initialDefender == 1){
                    if (transitionAttacker == initialAttacker - 1 && transitionDefender == 1) {
                        // we're going to state a-1, 1
                        sparseQ.insertSparse(i, j, a3d1_to_aMin1d1);
                    }
                } else if (initialAttacker == 1 && initialDefender >= 2) {
                    if (transitionAttacker == 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state 1, d-1
                        sparseQ.insertSparse(i, j, a1d2_to_a1dMin1);
                    }
                } else if (initialAttacker == 2 && initialDefender >= 2) {
                    if (transitionAttacker == 2 && transitionDefender == initialDefender - 2) {
                        //we're going to state 2,d-2
                        sparseQ.insertSparse(i, j, a2d2_to_a2dMin2);
                    } else if (transitionAttacker == 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state 1, d-1
                        sparseQ.insertSparse(i, j, a2d2_to_a1dMin1);
                    }
                } else if (initialAttacker >= 3 && initialDefender >= 2) {
                    //attacker has 3 dices, defender 2
                    if (transitionAttacker == initialAttacker && transitionDefender == initialDefender - 2) {
                        //we're going to state a,d-2
                        sparseQ.insertSparse(i, j, a3d2_to_a3dMin2);
                    } else if (transitionAttacker == initialAttacker - 2 && transitionDefender == initialDefender) {
                        // we're going to state a-2, d
                        sparseQ.insertSparse(i, j, a3d2_to_aMin2d2);
                    } else if (transitionAttacker == initialAttacker - 1 && transitionDefender == initialDefender - 1) {
                        // we're going to state a-1, d-1
                        sparseQ.insertSparse(i, j, a3d2_to_aMin1dMin1);
                    }
                }
            }
        }
        return sparseQ;
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
    public SparseMatrix generateSparseR() {
        SparseMatrix sparseR = new SparseMatrix(attacker*defender, attacker+defender, (attacker*defender) + (attacker+defender));
        String[][] transitionM = getTransitionR();
        String[][] initialM = getInitialR();
        int currentInitialAttacker = 0;
        int currentInitialDefender = 0;
        int currentTransitionAttacker = 0;
        int currentTransitionDefender = 0;
        for (int i = 0; i < attacker*defender; i++) {
            for (int j = 0; j < attacker+defender; j++) {
                String[] initialArray = initialM[i][j].split(" ");
                currentInitialAttacker = Integer.parseInt(initialArray[0]);
                currentInitialDefender = Integer.parseInt(initialArray[1]);
                String[] transitionArray = transitionM[i][j].split(" ");
                currentTransitionAttacker = Integer.parseInt(transitionArray[0]);
                currentTransitionDefender = Integer.parseInt(transitionArray[1]);
                if (currentInitialAttacker == 1 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == 1) {
                        // we're going from 11->01
                        sparseR.insertSparse(i, j, a1d1_to_a0d1);
                    }
                }
                if (currentInitialAttacker == 1 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 1 && currentTransitionDefender == 0) {
                        // we're going from 11->10
                        sparseR.insertSparse(i, j, a1d1_to_a1ad0);
                    }
                }
                if (currentInitialAttacker == 2 && currentInitialDefender == 1) {
                    if (currentTransitionAttacker == 2 && currentTransitionDefender == 0) {
                        // we're going from 21->20
                        sparseR.insertSparse(i, j, a2d1_to_a2d0);
                    }
                }
                if (currentInitialAttacker >= 3 && currentInitialDefender == 1) {
                    if (currentInitialAttacker == currentTransitionAttacker) {
                        if (currentTransitionDefender == 0) {
                            // we're going from a1->a0
                            sparseR.insertSparse(i, j, a3d1_to_a3d0);
                        }
                    }
                }
                if (currentInitialAttacker == 1 && currentInitialDefender >= 2) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == currentInitialDefender) {
                        // we're going from 1d->0d
                        sparseR.insertSparse(i, j, a1d2_to_a0d2);
                    }
                }
                if (currentInitialAttacker == 2 && currentInitialDefender >= 2) {
                    if (currentTransitionAttacker == 0 && currentTransitionDefender == currentInitialDefender){
                        // we're going from 2d->0d
                        sparseR.insertSparse(i, j, a2d2_to_a0d2);
                    }
                    if (currentTransitionAttacker == 2 && currentTransitionDefender == currentInitialDefender - 2) {
                        if (currentInitialDefender - 2 == 0) {
                            // we're going from 2d->2 d-2, and d-2 is zero
                            sparseR.insertSparse(i, j, a2d2_to_a2dMin2);
                        }
                    }
                }
                if (currentInitialAttacker >= 3 && currentInitialDefender >= 2) {
                    if (currentTransitionAttacker == currentInitialAttacker && currentTransitionDefender == currentInitialDefender - 2) {
                        if (currentInitialDefender - 2 == 0) {
                            // we're going from ad-> a d-2 and d-2 is zero
                            sparseR.insertSparse(i, j, a3d2_to_a3dMin2);
                        }
                    }
                }
            }
        }
        return sparseR;
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
                initialMatrix[i][j] = (firstCounter + 1) + " " + (secCounter + 1);
            }
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
    public SparseMatrix generateSparseER(SparseMatrix fMatrix, SparseMatrix eMatrix) {
        return fMatrix.multiplySparse(eMatrix);
    }

    public String[][] generateMatrixForERStates(SparseMatrix erMatrix) {
        String[][] generatedStateMatrix = new String[erMatrix.len][1];
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
     * @param sparseF [attacker*defender][2] expected remaining armies
     * @return probabilities that defender wins for start states (11,...,1D,21,...,2D,...,A1,...,AD)
     */
    public double[] getProbabilityDefenderWin(SparseMatrix sparseF) {
        double[] pk_def = new double[sparseF.row];
        int position = 0;
        int attack_count = 0;
        int defend_count = 0;
        double sum = 0;
        int pk_counter = 0;

        while (position < sparseF.len) {
            if (sparseF.matrix[position][0] == attack_count && sparseF.matrix[position][1] == defend_count) {
                sum += sparseF.matrix[position][2];
                position++;
            }
            if (defend_count >= defender-1) {
                defend_count = 0;
                if (attack_count < attacker*defender-1) {
                    attack_count++;
                }
                pk_def[pk_counter] = sum;
                pk_counter++;
                sum = 0;
                while (sparseF.matrix[position][0] < attack_count) {
                    position++;
                }
            } else {
                defend_count++;
            }
            if (sparseF.matrix[position][0] == attacker*defender-1 && sparseF.matrix[position][1] == defender) {
                position = sparseF.len;
            }

        }
        position_helper = position;
        return pk_def;
    }

    public int getPosition_helper() {
        return position_helper;
    }

    /**
     *
     * @param sparseF [attacker*defender][2] expected remaining armies
     * @return probabilities that attacker wins for start states (11,...,1D,21,...,2D,...,A1,...,AD)
     */
    public double[] getProbabilityAttackerWin(SparseMatrix sparseF, int position) {
        double[] pz_att = new double[sparseF.row];
        int  attack_count = 0;
        int defend_count = defender;
        double sum = 0;
        int pz_counter = 0;

        while (position < sparseF.len) {
            if (sparseF.matrix[position][0] == attack_count && sparseF.matrix[position][1] == defend_count) {
                sum += sparseF.matrix[position][2];
                position++;
            }
            if (defend_count >= attacker+defender-1) {
                defend_count = 0;
                if (attack_count < attacker*defender-1) {
                    attack_count++;
                }
                pz_att[pz_counter] = sum;
                pz_counter++;
                sum = 0;
                while (sparseF.matrix[position][0] < attack_count) {
                    position++;
                }
            } else {
                defend_count++;
            }
            if (sparseF.matrix[position][0] == attacker*defender-1 && sparseF.matrix[position][1] == attacker+defender-1) {
                position = sparseF.len;
            }

        }
        return pz_att;
    }
    /**
     * F - Matrix calculations
     */
    /**
     *
     * @param sparseR
     * @param sparseQ
     * @return double[attacker*defender][attacker+defender], probabilities for starting in a transient state,
     *      *  landing in an absorbing state
     */
    public SparseMatrix generateSparseF(SparseMatrix sparseR, SparseMatrix sparseQ) {


        SparseMatrix sparseF = new SparseMatrix(attacker*defender, attacker+defender, (attacker*defender) * (attacker+defender));
        for (int n = 1; n < attacker+defender; n++) {
            SparseMatrix sparseExp = sparseQ.multiplySparseNTimes(n-1);
            SparseMatrix sparseQR = sparseExp.multiplySparse(sparseR);
            SparseMatrix tmp = sparseF;
            sparseF = tmp.add(sparseQR);
            /*if (n == 1) {
                sparseExp.printSparse("sparseExp");
                sparseQR.printSparse("multiply");

            }*/
        }
        //sparseF.printSparse("sparseF");
        return sparseF;
    }

    /**
     * E - Matrix calculations
     */
    /**
     * @return double[2][attacker*defender], all transient state (first row: 1..D0..0, second row: 0..01..A)
     */
    public SparseMatrix generateSparseE() {
        SparseMatrix sparseE = new SparseMatrix(attacker+defender, 2, (attacker+defender));
        int countFirstRow = 1;
        int countSecRow = 0;
        for (int i = 0; i < attacker+defender; i++) {
            if (countFirstRow > defender) {
                countFirstRow = 0;
            }
            if (countFirstRow != 0) {
                sparseE.insertSparse(i, 0, countFirstRow);
                countFirstRow++;
            }
        }
        for (int i = 0; i < attacker+defender; i++) {
            // fill second row (zeros until D is reached, then 1...A)
            if (i >= defender) {
                countSecRow++;
            }
            if (countSecRow != 0) {
                sparseE.insertSparse(i, 1, countSecRow);
            }
        }
        return sparseE;
    }


}
