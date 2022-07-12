import org.cryptimeleon.craco.enc.EncryptionKeyPair;
import org.cryptimeleon.math.structures.groups.elliptic.nopairing.Secp256k1;

import java.util.ArrayList;

public class Main {


    private final static int RING_SIZE = 15;

    public static void main(String[] args) throws Exception {
        String message = "Hello World";
        //var begin = System.currentTimeMillis();
        ElgamalEncryption scheme = new ElgamalEncryption(new Secp256k1());
        EncryptionKeyPair keyPair = scheme.generateKeyPair();
        ArrayList<ElgamalPublicKey> pubKeys = new ArrayList<>();

        for (int i = 0; i < RING_SIZE; i++) {
            pubKeys.add((ElgamalPublicKey) scheme.generateKeyPair().getPk());
        }

        Ring r = new Ring(keyPair, pubKeys, scheme, RING_SIZE);
        //var genRing = System.currentTimeMillis();
        RingParameters rparam = r.sign(message);
        //var signRing = System.currentTimeMillis();

        if(!rparam.verifyRingSignature()) {
            throw new Exception("Signature not verified.");
        }
        //var end = System.currentTimeMillis();

        // Stats

        /**
        System.out.println("Generate Ring: " + ((double)genRing - begin)/1000);
        System.out.println("Sign Ring: " + ((double)signRing - genRing)/1000);
        System.out.println("Verify Ring: " + ((double)end - signRing)/1000);
        System.out.println("Total Time: " + ((double)end - begin)/1000);
        */
    }
}
