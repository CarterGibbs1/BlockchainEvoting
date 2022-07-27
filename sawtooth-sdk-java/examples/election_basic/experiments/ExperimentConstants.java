package election_basic.experiments;

public class ExperimentConstants {
    // generic system
    public static final double NUM_TRIALS = 30;
    public static final int BASE = 16;

    // message size
    public static final int[] NUM_CANDIDATES = {20, 40, 60, 80};
    public static final int[] NUM_VOTERS = {5000, 10000, 15000, 20000};

    // generating pub key
    public static final int[] numPubKeysToTest = {5, 10, 15, 20, 25};

    public static final int NUM_RACES = 3;
    public static final int NUM_BYTES = 7;

    // generating pubKey and message size
    public static final String[] messages = {
            "111",
            "123",
            "133",
            "321",
            "721",
            "785",
            "798",
            "434",
            "455",
            "654",
            "689",
            "354",
            "564"
    };
}
