package election_basic.Paillier;

import java.math.BigInteger;

public class PaillierCipherText {

    private BigInteger text;
    private PaillierPublicKey publicKey;

    public PaillierCipherText(BigInteger t, PaillierPublicKey pk) {
        text = t;
        publicKey = pk;
    }

    public PaillierCipherText hAdd(PaillierCipherText toAdd) {
        return new PaillierCipherText(text.multiply(toAdd.text).mod(publicKey.getnSquared()), publicKey);
    }

    public BigInteger getText() {
        return text;
    }

    public PaillierPublicKey getPublicKey() {
        return publicKey;
    }
}
