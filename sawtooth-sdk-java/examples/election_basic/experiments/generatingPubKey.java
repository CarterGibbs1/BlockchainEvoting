package election_basic.experiments;

import election_basic.Paillier.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import static election_basic.experiments.ExperimentConstants.*;

public class generatingPubKey {



    public static void main(String[] args) {
        for (int i = 0; i < numPubKeysToTest.length; i++) {
            long[] time = new long[4];
            for (int j = 0; j < NUM_TRIALS; j++) {
                KeyPairBuilder keygen = new KeyPairBuilder();
                time[0] += System.currentTimeMillis();
                KeyPair voterKeyPair = keygen.generateKeyPair();
                ArrayList<PaillierPublicKey> pubKeys = new ArrayList<>();
                for (int k = 0; k < numPubKeysToTest[i] - 1; k++) {
                    KeyPair keyPair = keygen.generateKeyPair();
                    pubKeys.add(keyPair.getPublicKey());
                }
                PaillierRing ring = new PaillierRing(voterKeyPair, pubKeys, numPubKeysToTest[i], BigInteger.valueOf(new Random().nextInt()));
                time[1] += System.currentTimeMillis();

                String voterMessage =convertVoterToMessage(messages[new Random().nextInt(messages.length)]);
                byte[] message = Election.toOneDimensionalArray(Election.convertVoteToByteArray(voterMessage, NUM_RACES, NUM_CANDIDATES[0], NUM_BYTES));
                time[2] -= System.currentTimeMillis();
                PaillierRingParameters rp = ring.sign(new BigInteger(message));
                time[2] += System.currentTimeMillis();
                rp.verifyRingSignature();
                time[3] += System.currentTimeMillis();
            }
            printResults(numPubKeysToTest[i], time);
        }
    }

    public static void printResults(int numKeys, long[] time) {
        System.out.println("------------ RESULTS -------------");
        System.out.println("NUMBER OF PUBLIC KEYS: " + numKeys);
        System.out.println("GENERATING RING: " + (time[1] - time[0]) / NUM_TRIALS);
        System.out.println("VALIDATING RING: " + (time[2] - time[1]) / NUM_TRIALS);
        System.out.println("SIGNING RING: " + (time[3] - time[2]) / NUM_TRIALS);
        System.out.println("TOTAL TIME: " + (time[3] - time[0]) / NUM_TRIALS);
        System.out.println();
    }

    public static String convertVoterToMessage(String voter) {
        String str = "";
        for (int i = 0; i < voter.length(); i++) {
            for (int j = 0; j < NUM_CANDIDATES[0]; j++) {
                if ((j == (int) voter.charAt(i))) {
                    str += "0000001";
                } else {
                    str += "0000000";
                }
            }
        }
        return str;
    }


}
