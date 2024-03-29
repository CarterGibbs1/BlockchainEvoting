package election_basic.experiments;

public class experimentClient {
    // experimentClient (experiment)
    public static void main(String[] args) {
        try {
            switch (args[0]) {
                case "tally":
                    Tallying.main(args);
                    break;
                case "generate":
                    generatingPubKey.main(args);
                    break;
                case "size":
                    messageSize.main(args);
                    break;
                case "create":
                    createVotes.main(args);
                    break;
                case "help":
                    System.out.println("tests: 'tally', 'generate', 'size'");
                    break;
                default:
                    System.err.println("Not an experiment");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
