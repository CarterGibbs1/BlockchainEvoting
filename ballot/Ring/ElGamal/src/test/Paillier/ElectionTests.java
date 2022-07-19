import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;

public class ElectionTests {

    private static final String m1 = "100100010";
    private static final String m2 = "010010100";
    private static final String m3 = "001100001";
    private static final String m4 = "100100100";
    private static final String m5 = "010010100";
    private static final String m6 = "001100010";

    private static final int SIZE = 15;
    private KeyPair keyPair;

    @Before
    public void init() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        keyPair = keygen.generateKeyPair();
    }

    // TEST ELECTION
    @Test
    public void createDatabase() {
        Election e = new Election();
        e.createDatabaseFile();
    }

    @Test
    public void testTallyVotes2() {
        Election e = new Election();
        String[] messages = new String[6];
        messages[0] = m1;
        messages[1] = m2;
        messages[2] = m3;
        messages[3] = m4;
        messages[4] = m5;
        messages[5] = m6;
        Random actual = new Random(1);
        Random expect = new Random(1);
        BigInteger total = new BigInteger(messages[expect.nextInt(6)]);
        for (int i = 1; i < 10; i++) {
            total = total.add(new BigInteger(messages[expect.nextInt(6)]));
        }
        for (int i = 0; i < 10; i++) {
            ArrayList<PublicKey> publicKeys = createPublicKeys();
            BigInteger seed = PaillierRing.rand(16);
            PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
            BigInteger encryptedM = e.getPk().encrypt(new BigInteger(messages[actual.nextInt(6)]));
            PaillierRingParameters param = pr.sign(encryptedM);
            e.addVoteToDatabase(param);
        }
        BigInteger result = e.tallyVotes();
        assertEquals(total, e.revealResult(result));
    }

    @Test
    public void testTallyVotes() {
        Election e = new Election();
        String[] messages = new String[3];
        messages[0] = m1;
        messages[1] = m3;
        messages[2] = m4;
        BigInteger expected_result = (new BigInteger(m1)).add(new BigInteger(m3)).add(new BigInteger(m4));
        for (int i = 0; i < 3; i++) {
            ArrayList<PublicKey> publicKeys = createPublicKeys();
            BigInteger seed = PaillierRing.rand(16);
            PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
            BigInteger encryptedM = e.getPk().encrypt(new BigInteger(messages[i]));
            PaillierRingParameters param = pr.sign(encryptedM);
            e.addVoteToDatabase(param);
        }
        BigInteger result = e.tallyVotes();
        assertEquals(expected_result, e.revealResult(result));
    }

    @Test
    public void addVoteToDatabase() {
        Election e = new Election();
        ArrayList<PublicKey> publicKeys = createPublicKeys();
        BigInteger seed = PaillierRing.rand(16);
        PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
        BigInteger encryptedM = e.getPk().encrypt(new BigInteger(m1));
        PaillierRingParameters param = pr.sign(encryptedM);
        boolean result = e.addVoteToDatabase(param);
        assert(result);
    }
    @Test
    public void testAddVote3() {
        byte[][][] vote1 = Election.convertVoteToByteArray("231", 3, 3 ,3);
        byte[][][] vote2 = Election.convertVoteToByteArray("221", 3, 3 ,3);
        Election e = new Election();
        String[] messages = new String[3];
        messages[0] = m1;
        messages[1] = m3;
        messages[2] = m4;
        BigInteger expected_result = (new BigInteger(m1)).add(new BigInteger(m3)).add(new BigInteger(m4));
        for (int i = 0; i < 3; i++) {
            ArrayList<PublicKey> publicKeys = createPublicKeys();
            BigInteger seed = PaillierRing.rand(16);
            PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
            BigInteger encryptedM = e.getPk().encrypt(new BigInteger(messages[i]));
            PaillierRingParameters param = pr.sign(encryptedM);
            e.addVoteToDatabase(param);
        }
        BigInteger result = e.tallyVotes();
        assertEquals(expected_result, e.revealResult(result));
    }

    @Test
    public void debugTest() {
        Election e = new Election();
        String[] messages = new String[24];
        messages[0] = m1;
        messages[1] = m2;
        messages[2] = m3;
        messages[3] = m4;
        messages[4] = m5;
        messages[5] = m6;
        messages[6] = m1;
        messages[7] = m2;
        messages[8] = m3;
        messages[9] = m4;
        messages[10] = m5;
        messages[11] = m6;
        messages[12] = m1;
        messages[13] = m2;
        messages[14] = m3;
        messages[15] = m4;
        messages[16] = m5;
        messages[17] = m6;
        messages[18] = m1;
        messages[19] = m2;
        messages[20] = m3;
        messages[21] = m4;
        messages[22] = m5;
        messages[23] = m6;
        for (int i = 0; i < 24; i++) {
            ArrayList<PublicKey> publicKeys = createPublicKeys();
            BigInteger seed = PaillierRing.rand(16);
            PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
            BigInteger encryptedM = e.getPk().encrypt(new BigInteger(messages[i], 16));
            PaillierRingParameters param = pr.sign(encryptedM);
            e.addVoteToDatabase(param);
        }
        var t = e.tallyVotes();
        System.out.println(e.revealResult(t).toString(16));
    }

    private static String byteArrayToString(byte[][][] array) {
        String retval = "";
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                for (int k = 0; k < array[0][0].length; k++) {
                    retval += (char) array[i][j][k];
                }
            }
        }
        return retval;
    }

    @Test
    public void testAddVoteArray2() {
        String vote1String = "231";
        String vote2String = "221";
        String vote3String = "212";
        String vote4String = "233";
        byte[][][] vote1 = Election.convertVoteToByteArray(vote1String, 3, 3 ,2);
        byte[][][] vote2 = Election.convertVoteToByteArray(vote2String, 3, 3 ,2);
        byte[][][] vote3 = Election.convertVoteToByteArray(vote3String, 3, 3 ,2);
        byte[][][] vote4 = Election.convertVoteToByteArray(vote4String, 3, 3 ,2);
        String[] messages = {byteArrayToString(vote1), byteArrayToString(vote2), byteArrayToString(vote3), byteArrayToString(vote4)};
        System.out.println(byteArrayToString(vote1));
        Election e = new Election();
        for (int i = 0; i < 24; i++) {
            ArrayList<PublicKey> publicKeys = createPublicKeys();
            BigInteger seed = PaillierRing.rand(16);
            PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
            BigInteger encryptedM = e.getPk().encrypt(new BigInteger(messages[i % 4], 16));
            PaillierRingParameters param = pr.sign(encryptedM);
            e.addVoteToDatabase(param);
        }
        var t = e.tallyVotes();
        System.out.println(e.revealResult(t).toString(16));
        //printArray(total);
    }

    @Test
    public void testAddVoteArray() {
        String vote1String = "231";
        String vote2String = "211";
        byte[][][] vote1 = Election.convertVoteToByteArray(vote1String, 3, 3 ,3);
        byte[][][] vote2 = Election.convertVoteToByteArray(vote2String, 3, 3 ,3);
        byte[][][] total = Election.addVoteArray(vote1, vote2);
        printArray(total);
    }

    @Test
    public void debug3() {
        BigInteger r = new BigInteger("15", 16);
        BigInteger t = new BigInteger("10", 16);
        System.out.println(r.add(t).toString(16));

    }

    @Test
    public void debug5() {
        Election e = new Election();
        String[] messages = new String[2];
        messages[0] = "b";
        messages[1] = "1";

        for (int i = 0; i < 2; i++) {
            ArrayList<PublicKey> publicKeys = createPublicKeys();
            BigInteger seed = PaillierRing.rand(16);
            PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
            BigInteger encryptedM = e.getPk().encrypt(new BigInteger(messages[i], 16));
            PaillierRingParameters param = pr.sign(encryptedM);
            e.addVoteToDatabase(param);
        }
        BigInteger result = e.tallyVotes();
        System.out.println(e.revealResult(result).toString());
    }

    @Test
    public void debug4() {
        Election e = new Election();
        String[] messages = new String[2];
        messages[0] = "16";
        messages[1] = "a";
        for (int i = 0; i < 2; i++) {
            ArrayList<PublicKey> publicKeys = createPublicKeys();
            BigInteger seed = PaillierRing.rand(16);
            PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
            BigInteger encryptedM = e.getPk().encrypt(new BigInteger(messages[i], 16));
            PaillierRingParameters param = pr.sign(encryptedM);
            e.addVoteToDatabase(param);
        }
        BigInteger result = e.tallyVotes();
        System.out.println(e.revealResult(result).toString(16));
    }

    @Test
    public void debug2() {
        BigInteger a = new BigInteger("16", 16);
        //BigInteger a1 = new BigInteger("16");
        BigInteger b = new BigInteger("a", 16);
        //BigInteger b1 = new BigInteger("10", 10);

        //System.out.println("Base 10: " + a1.add(b1).toString() + ", "+ a1.add(b1).toString(16));
        System.out.println("Base 16: " + a.add(b).toString() + ", "+ a.add(b).toString(16));
    }

    public ArrayList<PublicKey> createPublicKeys() {
        ArrayList<PublicKey> publicKeys = new ArrayList<>();
        KeyPairBuilder keygen = new KeyPairBuilder();
        for (int i = 0; i < SIZE - 1; i++) {
            keyPair = keygen.generateKeyPair();
            publicKeys.add(keyPair.getPublicKey());
        }
        return publicKeys;
    }

    public static void printArray(byte[][][] vote) {
        //System.out.println("Candidate 1  |  Candidate 2  |  Candidate 3");
        //System.out.println("-----------------------------------------------");
        for (int i = 0; i < vote.length; i++) {
            System.out.print("{");
            for (int j = 0; j < vote[i].length; j++) {
                System.out.print("{");
                for (int k=0; k< vote[i][j].length;k++) {
                    //System.out.print("{");
                    System.out.print((char) vote[i][j][k]);
                    //System.out.print("} ");
                }
                System.out.print("} ");
            }
            System.out.println("}, ");
        }
    }
}
