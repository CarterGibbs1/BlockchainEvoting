package election_basic.experiments;

public class experimentClient {
    // experimentClient (experiment)
    public static void main(String[] args) {
        switch (args[0]) {
            case "tally":
                Talling.main(args);
                break;
            case "generate":
                generatingPubKey.main(args);
                break;
            case "size":
                messageSize.main(args);
                break;
	    case "help":
		System.out.println("tests: 'tally', 'generate', 'size'");
            default:
                System.err.println("Not an experiment");
        }
    }
}
