package election_basic.Paillier;

import org.apache.commons.codec.binary.Hex;

import java.io.*;
import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;


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
public class PaillierPublicKey implements Serializable {
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
    public final PaillierCipherText encryptToCipherText(BigInteger m) {

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
    public final BigInteger encrypt(BigInteger m) {

        BigInteger r;
        do {
            r = new BigInteger(bits, new Random());
        } while (r.compareTo(n) >= 0);

        BigInteger result = g.modPow(m, nSquared);
        BigInteger x = r.modPow(n, nSquared);

        result = result.multiply(x);
        result = result.mod(nSquared);

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(bits + ";" + n + ";" + nSquared + ";" + g);
        return sb.toString();
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

    public static void toFile(PaillierPublicKey publicKey, String name) throws IOException {
        File f = new File(name);
        if (f.exists() || f.isFile()) {
            f.delete();
        }
        f.createNewFile();
        PrintWriter pw = new PrintWriter(f);
        pw.print(publicKey.toString());
    }

    public static PaillierPublicKey fromFile(String name) throws IOException {
        File f = new File(name);
        if (!f.exists() || !f.isFile()) {
            throw new FileNotFoundException("File does not exist");
        }
        Scanner s = new Scanner(f);
        s.useDelimiter(";");
        int bits = Integer.parseInt(s.next());
        BigInteger n = new BigInteger(s.next());
        BigInteger nSquared = new BigInteger(s.next());
        BigInteger g = new BigInteger(s.next());

        return new PaillierPublicKey(n, nSquared, g, bits);
    }

    public String toHex() {
        String s = this.toString();
        return Hex.encodeHexString(s.getBytes());
    }

    // Helper for Handler
    public PaillierCipherText getEmptyCipherText() {
        return new PaillierCipherText(BigInteger.ONE, this);
    }
}
