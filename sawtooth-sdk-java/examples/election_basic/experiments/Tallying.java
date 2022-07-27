package election_basic.experiments;

import election_basic.Paillier.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import static election_basic.experiments.ExperimentConstants.*;

public class Tallying {
    //java Tallying
    public static void main(String[] args) {
        for (int i = 0; i < NUM_BALLOTS.length; i++) {
            runExperiment(NUM_BALLOTS[i]);
        }
    }

    private static void runExperiment(int num_ballots) {
        for (int j = 0; j < numPubKeysToTest.length; j++) {
            Election e = new Election();
            long[] time = new long[3];
            for (int i = 0; i < num_ballots; i++) {
                e.createDatabaseFile();
                KeyPairBuilder keygen = new KeyPairBuilder();
                KeyPair voterKeyPair = keygen.generateKeyPair();
                ArrayList<PaillierPublicKey> pubKeys = new ArrayList<>();
                for (int k = 0; k < numPubKeysToTest[j] - 1; k++) {
                    KeyPair keyPair = keygen.generateKeyPair();
                    pubKeys.add(keyPair.getPublicKey());
                }
                PaillierRing ring = new PaillierRing(voterKeyPair, pubKeys, numPubKeysToTest[i], BigInteger.valueOf(new Random().nextInt()));
                byte[] message = Election.toOneDimensionalArray(Election.convertVoteToByteArray(messages[new Random().nextInt(messages.length)], NUM_RACES, NUM_CANDIDATES[0], NUM_BYTES));
                //System.out.println("DEBUG: " + new String(message));
                PaillierRingParameters ringParam = ring.sign(new BigInteger(message));
                e.addVoteToDatabase(ringParam);
            }
            System.out.println("Done generating blockchain");
            time[0] = System.currentTimeMillis();
            BigInteger total = e.tallyVotes();
            time[1] = System.currentTimeMillis();
            e.revealResult(total);
            time[2] = System.currentTimeMillis();
            printResults(num_ballots, numPubKeysToTest[j],time);
        }
    }

    public static void printResults(int numBallots,int numKeys, long[] time) {
        System.out.println("------------ RESULTS -------------");
        System.out.println("NUMBER OF BALLOTS: " + numBallots);
        System.out.println("NUMBER OF PUB_KEYS: " + numKeys);
        System.out.println("HOMOMORPHIC ENCRYPTION: " + (time[1] - time[0]));
        System.out.println("DECRYPTING: " + (time[2] - time[1]));
        System.out.println("TOTAL: " + (time[2] - time[0]));
        System.out.println();
    }
}
