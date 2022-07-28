package election_basic.Paillier;

import java.io.*;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * A class that represents the private part of the examples.xo_java.Paillier key pair.
 */
public class PaillierPrivateKey implements Serializable {

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

    public BigInteger sign(String m) {
        BigInteger s = PaillierRing.sha1(m);
        return s.modPow(s, lambda);
    }

    @Override
    public String toString() {
        return lambda + ";" + preCalculatedDenominator;
    }

    public static void toFile(PaillierPrivateKey privateKey, String name) throws IOException {
        File f = new File(name);
        if (f.exists() || f.isFile()) {
            f.delete();
        }
        f.createNewFile();
        PrintWriter pw = new PrintWriter(f);
        pw.print(privateKey.toString());
        pw.close();
    }

    public static PaillierPrivateKey fromFile(String name) throws IOException, ClassNotFoundException {
        File f = new File(name);
        if (!f.exists() || !f.isFile()) {
            throw new FileNotFoundException("File does not exist");
        }
        Scanner s = new Scanner(f);
        s.useDelimiter(";");
        BigInteger lambda = new BigInteger(s.next());
        BigInteger pre = new BigInteger(s.next());
        s.close();

        return new PaillierPrivateKey(lambda, pre);
    }
}