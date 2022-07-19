import org.cryptimeleon.math.structures.rings.zn.HashIntoZn;
import org.cryptimeleon.math.structures.rings.zn.Zn;

import java.math.BigInteger;
import java.util.ArrayList;

public class RingParameters {

    private static ElgamalCipherText message;
    private static ArrayList<ElgamalPublicKey> publicKeys;
    private static BigInteger v; // solution of ring equation
    private static ArrayList<BigInteger> x;
    private static Zn.ZnElement random_seed;

    public RingParameters(ElgamalCipherText message, ArrayList<ElgamalPublicKey> pk, BigInteger v, ArrayList<BigInteger> x, Zn.ZnElement random) {
        this.message = message;
        this.publicKeys = pk;
        this.v = v;
        this.x = x;
        random_seed = random;
    }

    public boolean verifyRingSignature() {
        ArrayList<BigInteger> y = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            var encrypted_message = new HashIntoZn(random_seed.getStructure()).hash(x.get(i).toString());
            y.add(Ring.trapdoor(encrypted_message, random_seed));
        }

        var a = Ring.solveRingEquation(y, Ring.sha1(message.toString()), random_seed.asInteger());
        return a.equals(v);
    }

    public String getRepresentation() {
        StringBuilder s = new StringBuilder();
        s.append("(" + message);
        for (int i = 0; i < publicKeys.size(); i++) {
            s.append(", " + new BigInteger(publicKeys.get(i).getH().getUniqueByteRepresentation()));
        }
        s.append(", " + v.hashCode() + ", ");
        for (int i = 0; i < x.size() - 1; i++) {
            s.append(x.get(i) + ", ");
        }
        s.append(x.get(x.size() - 1) + ")");
        return s.toString();
    }

    public ElgamalCipherText getMessage() {
        return message;
    }

    public BigInteger getV() {
        return v;
    }

    public ArrayList<BigInteger> getX() {
        return x;
    }

    public ArrayList<ElgamalPublicKey> getPublicKeys() {
        return publicKeys;
    }
}
