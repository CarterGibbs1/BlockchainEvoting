package election_basic.Paillier;

import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

public class Election {

    public static final String FILE_DIR = "./votes.txt";

    private static KeyPair keyPair;

    public Election() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        keyPair = keygen.generateKeyPair();
        createDatabaseFile();
    }

    public void createDatabaseFile() {
        try {
            File f = new File(FILE_DIR);
            if (f.isFile() || f.exists()) {
                f.delete();
            }
            f.createNewFile();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public BigInteger tallyVotes() {
        try {
            BigInteger total;
            File f = new File(FILE_DIR);
            if (!f.exists() || !f.isFile()) throw new IllegalArgumentException("File does not exist. No votes to add");
            Scanner s = new Scanner(f);
            s.useDelimiter(PaillierRingParameters.DELIMITER);
            total = new BigInteger(s.next());
            s.nextLine();
            while (s.hasNextLine()) {
                BigInteger vote = new BigInteger(s.next());
                total = homomorphicAdd(total, vote);
                s.nextLine();
            }
            return total;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean addVoteToDatabase(PaillierRingParameters vote) {
        if (!vote.verifyRingSignature()) {
            return false;
        }
        try {
            File f = new File(FILE_DIR);
            if (!f.exists() || !f.isFile()) {
                createDatabaseFile();
            }
            FileWriter fileWriter = new FileWriter(f, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(vote);
            printWriter.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static BigInteger homomorphicAdd(BigInteger first, BigInteger second) {
        return first.multiply(second).mod(keyPair.getPublicKey().getnSquared());
    }

    public PaillierPublicKey getPk() {
        return keyPair.getPublicKey();
    }

    public BigInteger revealResult(PaillierCipherText encrypted) {
        return keyPair.decrypt(encrypted);
    }

    // HEX VOTES
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static byte[][][] convertVoteToByteArray(String vote, final int NUM_RACES, final int NUM_CANDIDATES, final int NUM_BYTES) {
        byte[][][] voteInBytes = new byte[NUM_RACES][NUM_CANDIDATES][NUM_BYTES];
        for (int i = 0; i < NUM_RACES; i++) {
            for (int j = 0; j < NUM_CANDIDATES; j++) {
                for (int k = 0; k < NUM_BYTES;k++) {
                    voteInBytes[i][j][k] = (byte) HEX_ARRAY[0];
                }
            }
            int index = hex2int((byte) vote.charAt(i)) - 1;
            voteInBytes[i][index][NUM_BYTES - 1] = (byte) HEX_ARRAY[1];
        }
        return voteInBytes;
    }

    public static byte[][][] addVoteArray(byte[][][] vote1, byte[][][] vote2) {
        final int NUM_RACES = vote1.length;
        final int NUM_CANDIDATES = vote1[0].length;
        final int NUM_BYTES = vote1[0][0].length;
        byte[][][] totalVote = new byte[NUM_RACES][NUM_CANDIDATES][NUM_BYTES];
        for (int i = 0; i < NUM_RACES; i++) {
            for (int k = 0; k < NUM_CANDIDATES; k++) {
                for (int j = 0; j < NUM_BYTES; j++) {
                    totalVote[i][k][j] = '0';
                }
                for (int j = NUM_BYTES - 1; j >= 0; j--) {
                    int total = (byte) (hex2int(totalVote[i][k][j]) + hex2int(vote1[i][k][j]) + hex2int(vote2[i][k][j]));
                    int counter = 0;
                    while (total >= 16) {
                        total -= 16;
                        counter++;
                    }
                    totalVote[i][k][j] = (byte) int2hex(total);
                    if (counter != 0) {
                        totalVote[i][k][j - 1] += counter;
                    }
                }
            }
        }
        return totalVote;
    }

    private static int hex2int(byte ch)
    {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;
        return -1;
    }

    private static char int2hex(int ch)
    {
        return HEX_ARRAY[ch];
    }

    /**
     * byte[] bytes = new byte[array.length];
     * for ( int i = 0; i < array.length; i++ ) {
     *     bytes[i] = (byte)Integer.parseInt( array[i], 16 );
     * }
     */
}
