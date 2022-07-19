import org.cryptimeleon.craco.common.plaintexts.PlainText;
import org.cryptimeleon.math.hash.ByteAccumulator;
import org.cryptimeleon.math.hash.annotations.AnnotatedUbrUtil;
import org.cryptimeleon.math.serialization.Representation;
import org.cryptimeleon.math.serialization.annotations.ReprUtil;
import org.cryptimeleon.math.structures.groups.GroupElement;

public class ElgamalPlainText implements PlainText {
    private GroupElement plaintext; // GroupElement class contained in the math library

    public ElgamalPlainText(GroupElement plaintext) {
        this.plaintext = plaintext;
    }

    public GroupElement getPlaintext() {
        return plaintext;
    }

    @Override
    public ByteAccumulator updateAccumulator(ByteAccumulator byteAccumulator) {
        return AnnotatedUbrUtil.autoAccumulate(byteAccumulator, this);
    }

    @Override
    public Representation getRepresentation() {
        return ReprUtil.serialize(this);
    }
}