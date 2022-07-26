package election_basic.Paillier;

import java.io.*;
import java.math.BigInteger;

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

    public static void toFile(PaillierPrivateKey privateKey, String name) throws IOException {
        File f = new File(name);
        if (f.exists() || f.isFile()) {
            f.delete();
        }
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(privateKey);
        FileOutputStream fos = new FileOutputStream(f);
        PrintWriter pw = new PrintWriter(fos);
        pw.print(bos.toByteArray());
        oos.close();
        bos.close();
        pw.close();
        fos.close();
    }

    public static PaillierPrivateKey fromFile(String name) throws IOException, ClassNotFoundException {
        File f = new File(name);
        if (!f.exists() || !f.isFile()) {
            throw new FileNotFoundException("File does not exist");
        }
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fis);
        PaillierPrivateKey pk = (PaillierPrivateKey) ois.readObject();
        ois.close();
        fis.close();
        return pk;
    }
}