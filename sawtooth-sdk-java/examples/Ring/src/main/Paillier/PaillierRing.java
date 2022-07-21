package examples.Ring.src.main.Paillier;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;

public class PaillierRing {

    public final int SIZE;
    public final BigInteger SEED;

    private static KeyPair keyPair;
    private static ArrayList<PaillierPublicKey> publicKeys;

    public PaillierRing(KeyPair keyPair, ArrayList<PaillierPublicKey> publicKeys, int size, BigInteger seed) {
        if (publicKeys.size() != size - 1) {
            throw new IllegalArgumentException("The too many or too little public keys.");
        }
        this.keyPair = keyPair;
        this.publicKeys = publicKeys;
        this.SIZE = size;
        this.SEED = seed;
    }


    public PaillierRingParameters sign(BigInteger u) {
        SecureRandom rnd;
        try {
            rnd = getSecureRandom(SEED.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ArrayList<BigInteger> x = new ArrayList<>();
        ArrayList<BigInteger> y = new ArrayList<>();

        // generate x_i and y_i using trapdoor function
        publicKeys.add(keyPair.getPublicKey());
        x.add(keyPair.getPrivateKey().getLambda());
        for (int i = 0; i < SIZE - 1; i++) {
            x.add(rand(1024));
        }
        for (int i = 0; i < x.size(); i++) {
            y.add(x.get(i).multiply(BigInteger.valueOf(rnd.nextInt())));
        }
        ArrayList<BigInteger> x_clone = (ArrayList<BigInteger>) x.clone();
        BigInteger v = solveRingEquation(y, sha1(u.toString()));
        return new PaillierRingParameters(u, publicKeys, v, x_clone, SEED);
    }

    /**
    public PaillierRingParameters sign(String s) {
        SecureRandom rnd;
        try {
            rnd = getSecureRandom(SEED.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        var u = keyPair.getPublicKey().encrypt(new BigInteger(s));
        ArrayList<BigInteger> x = new ArrayList<>();
        ArrayList<BigInteger> y = new ArrayList<>();

        // generate x_i and y_i using trapdoor function
        publicKeys.add(keyPair.getPublicKey());
        x.add(keyPair.getPrivateKey().getLambda());
        for (int i = 0; i < SIZE - 1; i++) {
            x.add(rand(1024));
        }
        for (int i = 0; i < x.size(); i++) {
            y.add(x.get(i).multiply(BigInteger.valueOf(rnd.nextInt())));
        }
        ArrayList<BigInteger> x_clone = (ArrayList<BigInteger>) x.clone();
        BigInteger v = solveRingEquation(y, sha1(u.toString()));
        return new PaillierRingParameters(u, publicKeys, v, x_clone, SEED);
    }
     */

    // Helper methods

    /**
     public static BigInteger trapdoor(BigInteger encrypted_message, Zn.ZnElement r) {
     //var r = scheme.getGroup().getZn().getUniformlyRandomElement();
     var C = ((ElgamalPublicKey) keyPair.getPk()).getG().pow(encrypted_message).op(((ElgamalPublicKey) keyPair.getPk()).getH().pow(r));
     C.computeSync();
     return new BigInteger(C.getUniqueByteRepresentation()).mod(scheme.getGroup().size());
     }
     */
    public static BigInteger solveRingEquation(ArrayList<BigInteger> y, BigInteger u) {
        BigInteger v = sha1(u.toString());
        for (int i = 0; i < y.size(); i++) {
            v = sha1(v.xor(y.get(i).modPow(publicKeys.get(i).getG(), publicKeys.get(i).getN())).toString());
        }
        return v;
    }

    // Helper methods from https://github.com/katagaki/TheRing/
    static BigInteger rand(int bits) {
        try {
            BigInteger randomNumber;
            SecureRandom rng = SecureRandom.getInstance("SHA1PRNG");
            randomNumber = new BigInteger(bits, 1, rng);
            return randomNumber;

        } catch (Exception e) {
            return BigInteger.ZERO;
        }
    }

    public static BigInteger sha1(String message) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(message.getBytes("UTF-8"));

            return new BigInteger(String.format("%040x", new BigInteger(1, digest.digest())), 16);

        } catch (Exception e) {
            return BigInteger.ZERO;
        }
    }

    private static final String ALGORITHM = "SHA1PRNG";
    private static final String PROVIDER = "SUN";

    public static SecureRandom getSecureRandom(String seed) throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom sr = SecureRandom.getInstance(ALGORITHM, PROVIDER);
        sr.setSeed(seed.getBytes(StandardCharsets.UTF_8));
        return sr;
    }
}