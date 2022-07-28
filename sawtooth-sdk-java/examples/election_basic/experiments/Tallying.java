package election_basic.experiments;

import election_basic.Paillier.Election;
import election_basic.Paillier.KeyPair;
import election_basic.Paillier.PaillierPrivateKey;
import election_basic.Paillier.PaillierPublicKey;

import java.io.*;
import java.math.BigInteger;

import static election_basic.experiments.ExperimentConstants.*;

public class Tallying {
    //java Tallying
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        for (int i = 0; i < NUM_BALLOTS.length; i++) {
            runExperiment(NUM_BALLOTS[i]);
        }
    }

    private static void runExperiment(int num_ballots) throws IOException, ClassNotFoundException {
        Election e = getElectionFromFile();
        long[] time = new long[3];
        for (int i = 0; i < NUM_TRIALS; i++) {
            //System.out.println("Done generating blockchain");
            time[0] += System.currentTimeMillis();
            BigInteger total = e.tallyVotes();
            time[1] += System.currentTimeMillis();
            e.revealResult(total);
            time[2] += System.currentTimeMillis();
        }
        printResults(num_ballots, time);
    }

    public static void printResults(int numBallots, long[] time) {
        System.out.println("------------ RESULTS -------------");
        System.out.println("NUMBER OF BALLOTS: " + numBallots);
        System.out.println("HOMOMORPHIC ENCRYPTION: " + (time[1] - time[0]) / MS_TO_S / NUM_TRIALS);
        System.out.println("DECRYPTING: " + (time[2] - time[1]) / MS_TO_S / NUM_TRIALS);
        System.out.println("TOTAL: " + (time[2] - time[0]) / MS_TO_S / NUM_TRIALS);
        System.out.println();
    }

    public static Election getElectionFromFile() throws IOException, ClassNotFoundException {
        PaillierPublicKey pk = PaillierPublicKey.fromFile(electionPubFileName);
        PaillierPrivateKey sk = PaillierPrivateKey.fromFile(electionPrivFileName);
        KeyPair kp = new KeyPair(sk, pk, null);
        return new Election(kp);
    }
}
