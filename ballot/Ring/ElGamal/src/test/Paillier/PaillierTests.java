import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class PaillierTests {

    private static final int SIZE = 15;

    private KeyPair keyPair;
    PublicKey publicKey;

    @Before
    public void init() {
        KeyPairBuilder keygen = new KeyPairBuilder();
        keyPair = keygen.generateKeyPair();
        publicKey = keyPair.getPublicKey();

    }
/**
    @Test
    public void testAddingRings() {
        // Ring 1
        ArrayList<PublicKey> publicKeys1 = new ArrayList<>();
        KeyPairBuilder keygen1 = new KeyPairBuilder();
        KeyPair pair;
        for (int i = 0; i < SIZE - 1; i++) {
            pair = keygen1.generateKeyPair();
            publicKeys1.add(pair.getPublicKey());
        }
        BigInteger seed1 = PaillierRing.rand(16);
        PaillierRing pr1 = new PaillierRing(keyPair, publicKeys1, SIZE, seed1);
        PaillierRingParameters param1 = pr1.sign(new BigInteger("100100010");

        // Ring 2

        KeyPairBuilder keygen2 = new KeyPairBuilder();
        KeyPair keyPair2 = keygen2.generateKeyPair();
        ArrayList<PublicKey> publicKeys2 = new ArrayList<>();
        for (int i = 0; i < SIZE - 1; i++) {
            KeyPair pair2 = keygen2.generateKeyPair();
            publicKeys2.add(pair2.getPublicKey());
        }
        BigInteger seed2 = PaillierRing.rand(16);
        PaillierRing pr2 = new PaillierRing(keyPair2, publicKeys2, SIZE, seed2);
        PaillierRingParameters param2 = pr2.sign("111000111");

        // Add messages

    }

    @Test
    public void testVerifyingRing() {
        ArrayList<PublicKey> publicKeys = new ArrayList<>();
        KeyPairBuilder keygen = new KeyPairBuilder();
        for (int i = 0; i < SIZE - 1; i++) {
            keyPair = keygen.generateKeyPair();
            publicKeys.add(keyPair.getPublicKey());
        }
        BigInteger seed = PaillierRing.rand(16);
        PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
        PaillierRingParameters param = pr.sign("100100010");
        System.out.println(param.verifyRingSignature());
    }

    @Test
    public void testSigningMessage() {
        ArrayList<PublicKey> publicKeys = new ArrayList<>();
        KeyPairBuilder keygen = new KeyPairBuilder();
        for (int i = 0; i < SIZE - 1; i++) {
            keyPair = keygen.generateKeyPair();
            publicKeys.add(keyPair.getPublicKey());
        }
        BigInteger seed = PaillierRing.rand(16);
        PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
        PaillierRingParameters param = pr.sign("100100010");
    }

    @Test
    public void testCreatingRing() {
        ArrayList<PublicKey> publicKeys = new ArrayList<>();
        KeyPairBuilder keygen = new KeyPairBuilder();
        for (int i = 0; i < SIZE; i++) {
            keyPair = keygen.generateKeyPair();
            publicKeys.add(keyPair.getPublicKey());
        }
        BigInteger seed = PaillierRing.rand(16);
        PaillierRing pr = new PaillierRing(keyPair, publicKeys, SIZE, seed);
    }

    @Test
    public void testHomomorphicAddition() {
        BigInteger eleven = BigInteger.valueOf(11);

        BigInteger encryptedTen = publicKey.encrypt(BigInteger.TEN);
        BigInteger encryptedOne = publicKey.encrypt(BigInteger.ONE);

        BigInteger homomorphicEleven = encryptedTen.multiply(encryptedOne).mod(publicKey.getnSquared());

        assertEquals(eleven, keyPair.decrypt(homomorphicEleven));
    }
*/
    @Test
    public void testEncryption() {
        BigInteger plainData = BigInteger.valueOf(10);

        BigInteger encryptedData = publicKey.encrypt(plainData);

        assertNotEquals(plainData, encryptedData);
    }

    @Test
    public void testDecryption() {
        BigInteger plainData = BigInteger.valueOf(10);
        BigInteger encryptedData = publicKey.encrypt(plainData);

        BigInteger decryptedData = keyPair.decrypt(encryptedData);

        assertEquals(plainData, decryptedData);
    }

    @Test
    public void debugTest2() {
        SecureRandom r = new SecureRandom(BigInteger.valueOf(10).toByteArray());
        System.out.println(r.nextInt() + r.nextInt());
    }

    @Test
    public void debugTest1() {
        BigInteger b = new BigInteger("010100100");
        System.out.println(b);
    }
}
