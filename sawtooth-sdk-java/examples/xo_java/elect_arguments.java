package xo_java;

public class elect_arguments {
    String action;
    String name;
    String pubKeys_or_cipher;

    public elect_arguments(String a, String n, String p) {
        action = a;
        name = n;
        pubKeys_or_cipher = p;
    }

    public elect_arguments(String[] args) {
        if (args.length != 3) throw new IllegalArgumentException("Need 3 arguments (from elect_arguments)");
        action = args[0];
        name = args[1];
        pubKeys_or_cipher = args[2];
    }

    public String getAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public String getPubKeys_or_cipher() {
        return pubKeys_or_cipher;
    }
}
