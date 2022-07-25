package xo_java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class elect_cli {

    private static final String DEFAULT_URL = "http://rest-api-2:8008";
    private static final String[] DEFAULT_KEY_FILE = {"./election_pub_key.txt", "./election_priv_key"};

    private static elect_arguments parsedArgs;

    public static void main_wrapper(String[] args) {
        if (args.length < 3 || args.length >= 5) {
            throw new IllegalArgumentException("num args incorrect (from elect_cli.main_wrapper)");
        }
        parsedArgs = new elect_arguments(args);
        try {
            if (parsedArgs.getAction().equals("create")) {
                do_create();
            } else if (parsedArgs.getAction().equals("vote")) {
                do_vote();
            } else if (parsedArgs.getAction().equals("list")) {
                do_list();
            } else if (parsedArgs.getAction().equals("spoil")) {
                do_spoil();
            } else {
                throw new IllegalArgumentException("Illegal command");
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
    def do_create(args):
    name = args.name
    dimensions = args.dimensions
    url = _get_url(args)
    keyfile = _get_keyfile(args)
    auth_user, auth_password = _get_auth_info(args)

    client = XoClient(base_url=url, keyfile=keyfile)


    if args.wait and args.wait > 0:
    response = client.create(
    name, dimensions, wait=args.wait,
    auth_user=auth_user,
    auth_password=auth_password)
            else:
    response = client.create(
    name, dimensions, auth_user=auth_user,
    auth_password=auth_password)

    print("Response: {}".format(response))
     */
    private static void do_create() throws IOException, ClassNotFoundException {
        elect_client cli = new elect_client(DEFAULT_URL, DEFAULT_KEY_FILE);

    }

    private static void do_spoil() {
    }

    private static void do_list() {
    }

    private static void do_vote() {
    }
}
