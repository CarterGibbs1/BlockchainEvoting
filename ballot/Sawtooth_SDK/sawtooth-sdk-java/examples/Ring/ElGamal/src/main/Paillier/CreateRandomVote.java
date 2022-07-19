import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CreateRandomVote {

    private static final int NUM_RACES = 3;
    private static final int NUM_CANDIDATES = 3;
    private static final int NUM_BYTES = 3;

    private static final int SIZE = 10;

    private static final String[] messages = {"111", "112", "113","121","122","123","131","132","133",
                                       "211", "212", "213","221","222","223","231","232","233",
                                       "311", "312", "313","321","322","323","331","332","333"};

    public static void main(String[] args) throws Exception {
        final String electionPubKeyFile = args[0];
        File f = new File(electionPubKeyFile);
        if (!f.exists() && !f.isFile()) {
            throw new Exception("File does not exist");
        }
        Scanner s = new Scanner(f);
        s.useDelimiter(";");
        BigInteger n = s.nextBigInteger();
        BigInteger g = s.nextBigInteger();
        BigInteger nSquared = s.nextBigInteger();
        int bits = s.nextInt();
        PublicKey electionPubKey = new PublicKey(n, nSquared, g, bits);
        String m = messages[new Random().nextInt(messages.length)];
        byte[][][] vote = Election.convertVoteToByteArray(m, NUM_RACES,NUM_CANDIDATES,NUM_BYTES);
        BigInteger voteint = new BigInteger(Election.byteArraytoString(vote), 16);
        BigInteger vote_encrypted = electionPubKey.encrypt(voteint);

        // Ring Signature
        KeyPairBuilder keygen2 = new KeyPairBuilder();
        KeyPair keyPair2 = keygen2.generateKeyPair();
        ArrayList<PublicKey> publicKeys2 = new ArrayList<>();
        for (int i = 0; i < SIZE - 1; i++) {
            KeyPair pair2 = keygen2.generateKeyPair();
            publicKeys2.add(pair2.getPublicKey());
        }
        BigInteger seed2 = PaillierRing.rand(16);
        PaillierRing pr2 = new PaillierRing(keyPair2, publicKeys2, SIZE, seed2);
        PaillierRingParameters param2 = pr2.sign(vote_encrypted);
        if (!param2.verifyRingSignature()) throw new Exception("Ring signature not verified.");

        // Ballot File
        long i = 0;
        String ballotFileName ="";
        File bf;
        do {
            ballotFileName = "ballot_" + i + ".txt";
            bf = new File(ballotFileName);
            i++;
        } while (bf.exists() || bf.isFile());
        bf.createNewFile();
        FileWriter fw = new FileWriter(bf);
        PrintWriter pw = new PrintWriter(fw);
        pw.print(param2);
        pw.close();
        fw.close();
    }
}
