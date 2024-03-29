package election_basic.Paillier;

import java.math.BigInteger;

/**
 * A class that holds a pair of associated public and private keys.
 */
public class KeyPair {

    private final PaillierPrivateKey privateKey;
    private final PaillierPublicKey publicKey;
    private final BigInteger upperBound;

    public KeyPair(PaillierPrivateKey privateKey, PaillierPublicKey publicKey, BigInteger upperBound) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.upperBound = upperBound;
    }

    public PaillierPrivateKey getPrivateKey() {
        return privateKey;
    }

    public PaillierPublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Decrypts the given ciphertext.
     *
     * @param t The ciphertext that should be decrypted.
     * @return The corresponding plaintext. If an upper bound was given to {@link KeyPairBuilder},
     * the result can also be negative. See {@link KeyPairBuilder#upperBound(BigInteger)} for details.
     */
    public final BigInteger decrypt(PaillierCipherText t) {
        BigInteger c = t.getText();
        BigInteger n = publicKey.getN();
        BigInteger nSquare = publicKey.getnSquared();
        BigInteger lambda = privateKey.getLambda();

        BigInteger u = privateKey.getPreCalculatedDenominator();

        BigInteger p = c.modPow(lambda, nSquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);

        if (upperBound != null && p.compareTo(upperBound) > 0) {
            p = p.subtract(n);
        }

        return p;
    }

    public final BigInteger decrypt(BigInteger c) {
        BigInteger n = publicKey.getN();
        BigInteger nSquare = publicKey.getnSquared();
        BigInteger lambda = privateKey.getLambda();

        BigInteger u = privateKey.getPreCalculatedDenominator();

        BigInteger p = c.modPow(lambda, nSquare).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n);

        if (upperBound != null && p.compareTo(upperBound) > 0) {
            p = p.subtract(n);
        }

        return p;
    }
}