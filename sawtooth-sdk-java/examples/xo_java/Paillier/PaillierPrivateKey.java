package xo_java.Paillier;

import java.math.BigInteger;

/**
 * A class that represents the private part of the examples.xo_java.Paillier key pair.
 */
class PaillierPrivateKey {

    private final BigInteger lambda;
    private final BigInteger preCalculatedDenominator;

    PaillierPrivateKey(BigInteger lambda, BigInteger preCalculatedDenominator) {
        this.lambda = lambda;

        this.preCalculatedDenominator = preCalculatedDenominator;
    }

    public BigInteger getLambda() {
        return lambda;
    }

    public BigInteger getPreCalculatedDenominator() {
        return preCalculatedDenominator;
    }
}