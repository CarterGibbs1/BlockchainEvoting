import org.cryptimeleon.craco.enc.EncryptionKeyPair;
import org.cryptimeleon.math.structures.rings.zn.HashIntoZn;
import org.cryptimeleon.math.structures.rings.zn.Zn;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Random;

public class Ring {

    public final int SIZE;

    private static EncryptionKeyPair keyPair;
    private static ArrayList<ElgamalPublicKey> publicKeys;
    private static ElgamalEncryption scheme;

    public Ring(EncryptionKeyPair keyPair, ArrayList<ElgamalPublicKey> publicKeys, ElgamalEncryption scheme, int size) {
        this.keyPair = keyPair;
        this.publicKeys = publicKeys;
        this.SIZE = size;
        this.scheme = scheme;
    }

    public RingParameters sign(String s) {
        BigInteger hashed_message = sha1(s);
        Zn.ZnElement r = scheme.getGroup().getUniformlyRandomExponent();
        ArrayList<BigInteger> x = new ArrayList<>();
        for (int i = 0; i< SIZE; i++) {
            x.add(rand(256));
        }
        x.add(sha1(String.valueOf(((ElgamalPrivateKey) keyPair.getSk()).getA().asInteger())));
        ArrayList<BigInteger> y = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            var encrypted_message = new HashIntoZn(scheme.getGroup().getZn()).hash(x.get(i).toString());
            y.add(trapdoor(encrypted_message, r));
        }
        var ys = solveRingEquation(y, hashed_message, r.asInteger());
        ArrayList<ElgamalPublicKey> copyOfPubKeys = (ArrayList<ElgamalPublicKey>) publicKeys.clone();
        copyOfPubKeys.add(new Random().nextInt(SIZE + 1), (ElgamalPublicKey) keyPair.getPk());
        ArrayList<BigInteger> copyX = (ArrayList<BigInteger>) x.clone();
        /**
         * Shuffle elements
        ArrayList<BigInteger> copyX = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            copyX.add(new Random().nextInt(copyX.size() + 1), x.get(i));
        }
         */
        return new RingParameters(hashed_message, copyOfPubKeys, ys, copyX, r);
    }

    // Helper methods

    /**
     * Used to apply trapdoor function
     *
     * @return - null, not used as of yet
     */
    public static BigInteger trapdoor(Zn.ZnElement encrypted_message, Zn.ZnElement r) {
        //var r = scheme.getGroup().getZn().getUniformlyRandomElement();
        var C = ((ElgamalPublicKey) keyPair.getPk()).getG().pow(encrypted_message).op(((ElgamalPublicKey) keyPair.getPk()).getH().pow(r));
        C.computeSync();
        return new BigInteger(C.getUniqueByteRepresentation()).mod(scheme.getGroup().size());
    }

    /**
    public static BigInteger trapdoor(BigInteger encrypted_message, Zn.ZnElement r) {
        //var r = scheme.getGroup().getZn().getUniformlyRandomElement();
        var C = ((ElgamalPublicKey) keyPair.getPk()).getG().pow(encrypted_message).op(((ElgamalPublicKey) keyPair.getPk()).getH().pow(r));
        C.computeSync();
        return new BigInteger(C.getUniqueByteRepresentation()).mod(scheme.getGroup().size());
    }
    */
    public static BigInteger solveRingEquation(ArrayList<BigInteger> y, BigInteger message, BigInteger u) {
        var v = encrypt(u, message);
        int i = 0;
        while (i < y.size() - 1) {
            v = encrypt(v.xor(y.get(i).modPow(new BigInteger(publicKeys.get(i).getH().getUniqueByteRepresentation()), scheme.getGroup().size())), message);
            i++;
        }
        return u.xor(v).modPow(((ElgamalPrivateKey) keyPair.getSk()).getA().asInteger(), scheme.getGroup().size());
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

    public static BigInteger encrypt(BigInteger message, BigInteger key) {
        return message.xor(key);
    }

    private static BigInteger decrypt(BigInteger ciphertext, BigInteger key) {
        return ciphertext.xor(key);
    }
}