package election_basic.Paillier;

import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;

public class PaillierRingParameters implements Serializable {

    private static BigInteger message;
    private static ArrayList<PaillierPublicKey> publicKeys;
    private static BigInteger v; // solution of ring equation
    private static ArrayList<BigInteger> x;
    private static BigInteger random_seed;
    public static final String DELIMITER = ";";

    public PaillierRingParameters(BigInteger message, ArrayList<PaillierPublicKey> pk, BigInteger v, ArrayList<BigInteger> x, BigInteger random) {
        this.message = message;
        this.publicKeys = pk;
        this.v = v;
        this.x = x;
        random_seed = random;
    }

    public boolean verifyRingSignature() {
        try {
            SecureRandom rnd = PaillierRing.getSecureRandom(random_seed.toString());
            ArrayList<BigInteger> y = new ArrayList<>();
            for (int i = 0; i < x.size(); i++) {
                y.add(x.get(i).multiply(BigInteger.valueOf(rnd.nextInt())));
            }
            BigInteger v = PaillierRing.solveRingEquation(y, PaillierRing.sha1(message.toString()));
            return v.equals(this.v);
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public BigInteger getMessage() {
        return message;
    }

    public BigInteger getV() {
        return v;
    }

    public ArrayList<BigInteger> getX() {
        return x;
    }

    public ArrayList<PaillierPublicKey> getPublicKeys() {
        return publicKeys;
    }

    public static BigInteger getRandom_seed() {
        return random_seed;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage() + DELIMITER);
        for (int i = 0; i < publicKeys.size(); i++) {
            sb.append("[" +publicKeys.get(i).getN() + ", " + publicKeys.get(i).getG() + "],");
        }
        sb.append(getPublicKeys() + DELIMITER);
        sb.append(getV() + DELIMITER);
        sb.append(getX() + DELIMITER);
        sb.append(getRandom_seed());
        return sb.toString();
    }

    public static void toFile(PaillierRingParameters ringParameters, String name) throws IOException {
        File f = new File(name);
        if (f.exists() || f.isFile()) {
            f.delete();
        }
        f.createNewFile();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(ringParameters);
        FileOutputStream fos = new FileOutputStream(f);
        PrintWriter pw = new PrintWriter(fos);
        pw.print(bos.toByteArray());
        oos.close();
        bos.close();
        pw.close();
        fos.close();
    }

    public static PaillierRingParameters fromFile(String name) throws IOException, ClassNotFoundException {
        File f = new File(name);
        if (!f.exists() || !f.isFile()) {
            throw new FileNotFoundException("File does not exist");
        }
        FileInputStream fis = new FileInputStream(f);
        ObjectInputStream ois = new ObjectInputStream(fis);
        PaillierRingParameters rp = (PaillierRingParameters) ois.readObject();
        ois.close();
        fis.close();
        return rp;
    }
}
