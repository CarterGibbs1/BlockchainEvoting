package examples.Ring.src.main.Paillier;

import java.math.BigInteger;

public record PaillierCipherText(BigInteger text, PaillierPublicKey publicKey) {
    public PaillierCipherText hAdd(PaillierCipherText toAdd) {
        return new PaillierCipherText(text.multiply(toAdd.text).mod(publicKey.getnSquared()), publicKey);
    }
}
