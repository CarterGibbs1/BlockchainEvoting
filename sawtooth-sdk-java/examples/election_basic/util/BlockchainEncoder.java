package election_basic.util;

import sawtooth.sdk.processor.Utils;
import sawtooth.sdk.processor.exceptions.InternalError;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class BlockchainEncoder {

    /**
     * Calculates and encoded value for Prefixing address. This method returns only
     * first 6 digits of prefix.
     *
     * @param prefix
     * @throws UnsupportedEncodingException
     */
    private String prefixAddress(String prefix) {
        try {
            return Utils.hash512(prefix.getBytes("UTF-8")).substring(0, 6);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Calculates an encoded value for a unique key address
     *
     * @param key
     * @throws InternalError
     */
    private String keyAddress(String key) {
        String address = null;
        try {
            String hashedName = Utils.hash512(key.getBytes("UTF-8"));
            address = hashedName.substring(hashedName.length() - 64);
        } catch (UnsupportedEncodingException usee) {
            usee.printStackTrace();
        }
        return address;
    }

    public String getBlockchainAddressFromKey(String prefix, String key) {
        return prefixAddress(prefix).concat(keyAddress(key));
    }

    public byte[] encode(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    public String decode(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString));
    }
}