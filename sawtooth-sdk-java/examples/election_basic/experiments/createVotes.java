package election_basic.experiments;

import election_basic.Paillier.*;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import static election_basic.experiments.ExperimentConstants.*;

public class createVotes {
    public static void main(String[] args) throws IOException {
        Election e = new Election();
        e.createDatabaseFile();
        for (int i = 0; i < NUM_BALLOTS[NUM_BALLOTS.length - 1]; i++) {
            KeyPairBuilder keygen = new KeyPairBuilder();
            KeyPair voterKeyPair = keygen.generateKeyPair();
            ArrayList<PaillierPublicKey> pubKeys = new ArrayList<>();
            for (int k = 0; k < numPubKeysToTest[0] - 1; k++) {
                KeyPair keyPair = keygen.generateKeyPair();
                pubKeys.add(keyPair.getPublicKey());
            }
            PaillierRing ring = new PaillierRing(voterKeyPair, pubKeys, numPubKeysToTest[0], BigInteger.valueOf(new Random().nextInt()));
            byte[] message = Election.toOneDimensionalArray(Election.convertVoteToByteArray(messages[new Random().nextInt(messages.length)], NUM_RACES, NUM_CANDIDATES[0], NUM_BYTES));
            //System.out.println("DEBUG: " + new String(message));
            PaillierRingParameters ringParam = ring.sign(new BigInteger(message));
            e.addVoteToDatabase(ringParam);
        }
        createElectionOutput(e);
    }

    public static void createElectionOutput(Election e) throws IOException {
        File f = new File(electionFileName);
        if (f.exists() || f.isFile()) f.delete();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        FileOutputStream fos = new FileOutputStream(f);
        oos.writeObject(e);
        fos.write(bos.toByteArray());
    }
}
