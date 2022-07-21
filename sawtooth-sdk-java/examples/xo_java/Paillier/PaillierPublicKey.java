package xo_java.Paillier;
import java.math.BigInteger;
import java.util.Random;

/**
 * A class that represents the public part of the examples.xo_java.Paillier key pair.
 * <p>
 * As in all asymmetric cryptographic systems it is responsible for the
 * encryption.
 * <p>
 * Additional instructions for the decryption can be found on {@link KeyPair}.
 *
 * @see KeyPair
 */
public class PaillierPublicKey {
    private final int bits;
    private final BigInteger n;
    private final BigInteger nSquared;
    private final BigInteger g;

    PaillierPublicKey(BigInteger n, BigInteger nSquared, BigInteger g, int bits) {
        this.n = n;
        this.nSquared = nSquared;
        this.bits = bits;
        this.g = g;
    }

    public int getBits() {
        return bits;
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getnSquared() {
        return nSquared;
    }

    public BigInteger getG() {
        return g;
    }

    /**
     * Encrypts the given plaintext.
     *
     * @param m The plaintext that should be encrypted.
     * @return The corresponding ciphertext.
     */
    public final PaillierCipherText encrypt(BigInteger m) {

        BigInteger r;
        do {
            r = new BigInteger(bits, new Random());
        } while (r.compareTo(n) >= 0);

        BigInteger result = g.modPow(m, nSquared);
        BigInteger x = r.modPow(n, nSquared);

        result = result.multiply(x);
        result = result.mod(nSquared);

        return new PaillierCipherText(result, this);
    }

    /**
     * Encrypts the given plaintext.
     *
     * @param m The plaintext that should be encrypted.
     * @return The corresponding ciphertext.
     */
    public static final BigInteger encrypt(PaillierPublicKey pk, BigInteger m) {

        BigInteger r;
        do {
            r = new BigInteger(pk.getBits(), new Random());
        } while (r.compareTo(pk.getN()) >= 0);

        BigInteger result = pk.getG().modPow(m, pk.getnSquared());
        BigInteger x = r.modPow(pk.getN(), pk.getnSquared());

        result = result.multiply(x);
        result = result.mod(pk.getnSquared());

        return result;
    }

    // Helper for Handler
    public PaillierCipherText getEmptyCipherText() {
        return new PaillierCipherText(BigInteger.ONE, this);
    }
}
