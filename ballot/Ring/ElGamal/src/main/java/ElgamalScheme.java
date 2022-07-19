import org.cryptimeleon.craco.common.plaintexts.PlainText;
import org.cryptimeleon.craco.enc.*;
import org.cryptimeleon.math.serialization.Representation;
import org.cryptimeleon.math.structures.groups.Group;
import org.cryptimeleon.math.structures.groups.GroupElement;
import org.cryptimeleon.math.structures.rings.zn.Zn;

import java.math.BigInteger;

public class ElgamalScheme implements AsymmetricEncryptionScheme {

    private static final BigInteger p = new BigInteger("11111111111111111111111");
    private Group groupG;

    public ElgamalScheme(Group g) {
        groupG = g;
    }

    @Override
    public EncryptionKeyPair generateKeyPair() {
        Zn zn_random = new Zn(p);
        GroupElement generator = groupG.getUniformlyRandomNonNeutral();
        Zn.ZnElement a = zn_random.getUniformlyRandomElement();
        GroupElement h = generator.pow(a);
        ElgamalPrivateKey privateKey = new ElgamalPrivateKey(groupG, generator, a, h);
        EncryptionKey publicKey = privateKey.getPublicKey();
        return new EncryptionKeyPair(publicKey, privateKey);
    }

    @Override
    public CipherText encrypt(PlainText plainText, EncryptionKey encryptionKey) {
        return null;
    }

    @Override
    public PlainText decrypt(CipherText cipherText, DecryptionKey decryptionKey) {
        return null;
    }

    @Override
    public PlainText restorePlainText(Representation representation) {
        return null;
    }

    @Override
    public CipherText restoreCipherText(Representation representation) {
        return null;
    }

    @Override
    public EncryptionKey restoreEncryptionKey(Representation representation) {
        return null;
    }

    @Override
    public DecryptionKey restoreDecryptionKey(Representation representation) {
        return null;
    }

    @Override
    public Representation getRepresentation() {
        return null;
    }

    public static BigInteger getP() {
        return p;
    }
}
