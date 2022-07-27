package election_basic.experiments;

import static election_basic.experiments.ExperimentConstants.*;

public class messageSize {

    public static void main(String[] args) {
        long[] messageSizes = new long[4];
        for (int i = 0; i < NUM_CANDIDATES.length; i++) {
            messageSizes[0] = ((long) Math.ceil((Math.log(NUM_VOTERS[0]) / Math.log(BASE)))) * NUM_RACES * NUM_CANDIDATES[i];
            messageSizes[1] = ((long) Math.ceil((Math.log(NUM_VOTERS[1]) / Math.log(BASE)))) * NUM_RACES * NUM_CANDIDATES[i];
            messageSizes[2] = ((long) Math.ceil((Math.log(NUM_VOTERS[2]) / Math.log(BASE)))) * NUM_RACES * NUM_CANDIDATES[i];
            messageSizes[3] = ((long) Math.ceil((Math.log(NUM_VOTERS[3]) / Math.log(BASE)))) * NUM_RACES * NUM_CANDIDATES[i];
            printResults(NUM_CANDIDATES[i], messageSizes);
        }
    }

    public static void printResults(int num_can, long[] messageSizes) {
        System.out.println("------------ RESULTS -------------");
        System.out.println("NUMBER OF CANDIDATES: " + num_can);
        System.out.println("5000 Voters: " + messageSizes[0]);
        System.out.println("10000 Voters: " + messageSizes[1]);
        System.out.println("15000 Voters: " + messageSizes[2]);
        System.out.println("20000 Voters: " + messageSizes[3]);
        System.out.println();
    }
}
