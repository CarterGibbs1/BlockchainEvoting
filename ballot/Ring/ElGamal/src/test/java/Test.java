import org.cryptimeleon.craco.common.plaintexts.GroupElementPlainText;
import org.cryptimeleon.craco.enc.EncryptionKeyPair;
import org.cryptimeleon.math.structures.groups.GroupElement;
import org.cryptimeleon.math.structures.groups.elliptic.nopairing.Secp256k1;
import org.cryptimeleon.math.structures.rings.zn.Zn;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Random;

import static junit.framework.TestCase.assertEquals;

public class Test {

    // Constants
    private static final int RING_SIZE = 15;
    private static final double NUM_TRIALS = 100;
    private static final double M_TO_S = 1000;

    /**
     * Test Discrete Logs
     */
    @org.junit.Test
    public void testHomomorphicDiscreteLogs(){
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        EncryptionKeyPair keyPair = scheme.generateKeyPair();

        Zn zn = scheme.getGroup().getZn();
        Zn.ZnElement r = zn.getUniformlyRandomElement();

        GroupElement four = scheme.getGroup().getGenerator().pow(4);
        GroupElement five = scheme.getGroup().getGenerator().pow(5);

        BigInteger nineint = new BigInteger(scheme.getGroup().getGenerator().pow(9).getUniqueByteRepresentation());

        ElgamalCipherText encrypted_four = (ElgamalCipherText) scheme.encrypt(new GroupElementPlainText(four), keyPair.getPk(), r.asInteger());
        ElgamalCipherText encrypted_five = (ElgamalCipherText) scheme.encrypt(new GroupElementPlainText(five), keyPair.getPk(), r.asInteger());

        ElgamalCipherText encrypted_added_nine = encrypted_four.applyAddition(encrypted_five); // apply homomorphic addition

        GroupElement actual = ((ElgamalPlainText) (scheme.decrypt(encrypted_added_nine, keyPair.getSk()))).getPlaintext();
        BigInteger m = BigInteger.valueOf(ElgamalEncryption.discreteLogarithm(new BigInteger(actual.getUniqueByteRepresentation()), r.asInteger()));
        assertEquals(nineint, m);
    }

    @org.junit.Test
    public void testDiscreteLogs() {
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        Zn zn = scheme.getGroup().getZn();
        Zn.ZnElement r = zn.getUniformlyRandomElement();
        GroupElement g = scheme.getGroup().getGenerator();
        g.precomputePow();

        GroupElement four = g.pow(4);
        BigInteger m = BigInteger.valueOf(ElgamalEncryption.discreteLogarithm(new BigInteger(four.getUniqueByteRepresentation()), r.asInteger()));
        assertEquals(BigInteger.valueOf(4) ,m);
    }

    /**
     * Test homomorphic addition
     */
    @org.junit.Test
    public void testAdd(){
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        EncryptionKeyPair keyPair1 = scheme.generateKeyPair();

        Zn zn = scheme.getGroup().getZn();
        Zn.ZnElement r = zn.getUniformlyRandomElement();

        GroupElement four = scheme.getGroup().getGenerator().pow(4);
        GroupElement five = scheme.getGroup().getGenerator().pow(5);

        ElgamalCipherText encrypted_four = (ElgamalCipherText) scheme.encrypt(new GroupElementPlainText(four), keyPair1.getPk(), r.asInteger());
        ElgamalCipherText encrypted_five = (ElgamalCipherText) scheme.encrypt(new GroupElementPlainText(five), keyPair1.getPk(), r.asInteger());

        ElgamalCipherText encrypted_added_nine = encrypted_four.applyAddition(encrypted_five); // apply homomorphic addition

        GroupElement actual = ((ElgamalPlainText) (scheme.decrypt(encrypted_added_nine, keyPair1.getSk()))).getPlaintext();
        GroupElement expected = scheme.getGroup().getGenerator().pow(9);

        assertEquals(expected, actual);
    }
    @org.junit.Test
    public void debugTest5() {
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        var a = scheme.getGroup();
        System.out.println();
    }

    @org.junit.Test
    public void debugTest4() {
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        var a = scheme.getGroup().getGenerator();
        a.precomputePow();
        var b = new BigInteger(a.pow(Ring.sha1("Hello, how are you doing today")).getUniqueByteRepresentation()).toString().length();
        System.out.println(b);
    }

    @org.junit.Test
    public void debugTest3() {
        BigInteger val = BigInteger.ZERO;
        long t = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            val = val.add(new BigInteger(Ring.sha1(generateRandomString()).toString()));
        }
        System.out.println(val.toString().length());
        long e = System.currentTimeMillis();
        System.out.println((double) (e - t)/ 1000);
    }

    @org.junit.Test
    public void debugTest2() {
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        var a = new BigInteger(scheme.getGroup().getGenerator().getUniqueByteRepresentation()).toString().length();
        System.out.println(a);
    }


    /**
     * Test to show that 9 and 4+5 are the same after encryption
     */
    @org.junit.Test
    public void debugTest() {
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        EncryptionKeyPair keyPair1 = scheme.generateKeyPair();

        Zn zn = scheme.getGroup().getZn();
        Zn.ZnElement r = zn.getUniformlyRandomElement();

        GroupElement nine = scheme.commit(zn.valueOf(9), (ElgamalPublicKey) keyPair1.getPk(), r);
        GroupElement four_plus_five = scheme.commit(zn.valueOf(4 + 5), (ElgamalPublicKey) keyPair1.getPk(), r);

        ElgamalCipherText encrypted_nine = (ElgamalCipherText) scheme.encrypt(new GroupElementPlainText(nine), keyPair1.getPk(), r.asInteger());
        ElgamalCipherText encrypted_four_plus_five = (ElgamalCipherText) scheme.encrypt(new GroupElementPlainText(four_plus_five), keyPair1.getPk(), r.asInteger());

        GroupElement actual = ((ElgamalPlainText) (scheme.decrypt(encrypted_nine, keyPair1.getSk()))).getPlaintext();
        GroupElement expected = ((ElgamalPlainText) (scheme.decrypt(encrypted_four_plus_five, keyPair1.getSk()))).getPlaintext();

        assertEquals(expected, actual);

    }

    @org.junit.Test
    public void generateTimeData() {
        long[] time = new long[4];
        for (int j = 0; j < NUM_TRIALS; j++) {
            time[0] += System.currentTimeMillis();
            String message = generateRandomString();
            ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
            EncryptionKeyPair keyPair = scheme.generateKeyPair();
            ArrayList<ElgamalPublicKey> pubKeys = new ArrayList<>();

            for (int i = 0; i < RING_SIZE; i++) {
                pubKeys.add((ElgamalPublicKey) scheme.generateKeyPair().getPk());
            }

            Ring r = new Ring(keyPair, pubKeys, scheme, RING_SIZE);
            time[1] += System.currentTimeMillis();
            RingParameters rparam = r.sign(message);
            time[2] += System.currentTimeMillis();

            if(!rparam.verifyRingSignature()) {
                assert(false);
            }
            time[3] += System.currentTimeMillis();
        }
        printResults(time);
        assert(true);
    }

    // Helper methods
    public static String generateRandomString() {
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, Charset.forName("UTF-8"));
    }

    public static void printResults(long[] time) {
        System.out.println("Generating Ring: " + (time[1] - time[0]) / NUM_TRIALS / M_TO_S);
        System.out.println("Signing Ring: " + (time[2] - time[1]) / NUM_TRIALS / M_TO_S);
        System.out.println("Validating Ring: " + (time[3] - time[2]) / NUM_TRIALS / M_TO_S);
        System.out.println("Total Time: " + (time[3] - time[0]) / NUM_TRIALS / M_TO_S);
    }
}