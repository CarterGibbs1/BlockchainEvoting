package xo_java;

import com.google.protobuf.ByteString;
import xo_java.Paillier.PaillierPrivateKey;
import xo_java.Paillier.PaillierPublicKey;

import java.io.*;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.Flow;

public class elect_client {

    private String url;
    private PaillierPublicKey pubKey;
    private PaillierPrivateKey privKey;


    elect_client(String u, String[] keyFiles) throws IOException, ClassNotFoundException {
        url = u;
        File[] f = parseFiles(keyFiles);
        pubKey = openPaillierPubFile(f[0]);
        privKey = openPaillierPrivFile(f[1]);
    }

    // HELPER METHODS

    public String _send_election_txn(String name, String action, String cipher_key) throws IOException {
        //Serialization is just a delimited utf-8 encoded string
        String payload = (name + action + cipher_key);
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(payload);
        payload = "," + buffer;

        //Construct the address
        String address = _get_address(name);

        //Construct Header
        elect_transaction_header header = new elect_transaction_header(convertStringToHex(pubKey.toString()), Arrays.asList(new String()),
                Arrays.asList(address.split(",")), Arrays.asList(address.split(",")),
                randomHexInt(), hashInSha512(payload), convertStringToHex(pubKey.toString()));
        String serializedHeader = null;
        try {
            serializedHeader = header.serializeToString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BigInteger signature = privKey.sign(serializedHeader);

        elect_transaction transaction = new elect_transaction(ByteString.copyFrom(serializedHeader.getBytes()), signature.toString(), ByteString.copyFrom(payload.getBytes()));

        elect_batch_list batch_list = _create_batch_list(List.of(transaction));
        String batch_id = batch_list.batches.get(0).headerSignature;

        return _send_request("batches", batch_list.serializeToString(), "application/octet-stream");
    }

    private String _send_request(String suffix, String data, String content) {
        url = "http://{}/{}".format(url, suffix);
        HttpRequest.Builder result = HttpRequest.newBuilder();
        try {
            if (data != null) {
                result = result.POST(url, headers = headers, data = data);
            } else {
                result = requests.get(url, headers = headers);
            }
            if result.status_code == 404:
            raise XoException ("No such game: {}".format(name));

            if not result.ok:
            throw new Exception("Error {}: {}".format(result.status_code, result.reason));
        }
    }

    /**
     def _send_request(self,
     suffix,
     data=None,
     content_type=None,
     name=None,
     auth_user=None,
     auth_password=None):
     if self._base_url.startswith("http://"):
     url = "{}/{}".format(self._base_url, suffix)
     else:
     url = "http://{}/{}".format(self._base_url, suffix)

     headers = {}
     if auth_user is not None:
     auth_string = "{}:{}".format(auth_user, auth_password)
     b64_string = b64encode(auth_string.encode()).decode()
     auth_header = 'Basic {}'.format(b64_string)
     headers['Authorization'] = auth_header

     if content_type is not None:
     headers['Content-Type'] = content_type

     try:
     if data is not None:
     result = requests.post(url, headers=headers, data=data)
     else:
     result = requests.get(url, headers=headers)

     if result.status_code == 404:
     raise XoException("No such game: {}".format(name))

     if not result.ok:
     raise XoException("Error {}: {}".format(
     result.status_code, result.reason))

     except requests.ConnectionError as err:
     raise XoException(
     'Failed to connect to {}: {}'.format(url, str(err))) from err

     except BaseException as err:
     raise XoException(err) from err

     return result.text
     */

    private File[] parseFiles(final String[] files) throws FileNotFoundException {
        String pub = files[0];
        String priv = files[1];
        File[] retval = new File[2];
        retval[0] = new File(pub);
        retval[1] = new File(priv);
        if (!retval[0].isFile() || !retval[0].exists() || !retval[1].isFile() || !retval[1].exists()) {
            throw new FileNotFoundException("Public or private key file not found (at elect_cli.parseFiles)");
        }
        return retval;
    }

    private PaillierPublicKey openPaillierPubFile(File f) throws IOException, ClassNotFoundException {
        Scanner s = new Scanner(f);
        String str = "";
        while (s.hasNextLine()) {
            str += s.nextLine();
        }
        s.close();
        byte[] rawCipher = Base64.getDecoder().decode(str);
        ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
        ObjectInput in = new ObjectInputStream(bis);
        PaillierPublicKey pub_key = (PaillierPublicKey) in.readObject();
        in.close();
        bis.close();
        return pub_key;
    }

    private PaillierPrivateKey openPaillierPrivFile(File f) throws IOException, ClassNotFoundException {
        Scanner s = new Scanner(f);
        String str = "";
        while (s.hasNextLine()) {
            str += s.nextLine();
        }
        s.close();
        byte[] rawCipher = Base64.getDecoder().decode(str);
        ByteArrayInputStream bis = new ByteArrayInputStream(rawCipher);
        ObjectInput in = new ObjectInputStream(bis);
        PaillierPrivateKey pub_key = (PaillierPrivateKey) in.readObject();
        in.close();
        bis.close();
        return pub_key;
    }

    elect_batch_list _create_batch_list(List<elect_transaction> transactions) {
        String[] transaction_signatures = new String[transactions.size()];
        for (int i = 0; i < transactions.size(); i++) {
            transaction_signatures[i] = transactions.get(i).getHeaderSignature();
        }

        elect_batch_header header = new elect_batch_header(pubKey.toString(), Arrays.asList(transaction_signatures));

        String serializedHeader = null;
        try {
            serializedHeader = header.serializeToString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BigInteger signature = privKey.sign(serializedHeader);

        elect_batch batch = new elect_batch(ByteString.copyFrom(serializedHeader.getBytes()), signature.toString(), transactions);
        elect_batch_list batchList = new elect_batch_list(batch);
        return batchList;
    }

    String _get_prefix() {
        return hashInSha512(encodeInUTF("election"));
    }

    String _get_address(String name) {
        String elect_prefix = _get_prefix();
        String elect_address = hashInSha512(encodeInUTF(name)).substring(0,64);
        return elect_prefix + elect_address;
    }

    String encodeInUTF(String rawString) {
        ByteBuffer buffer = StandardCharsets.UTF_8.encode(rawString);
        return buffer.toString();
    }

    String hashInSha512(String rawString) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(rawString.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    private static String convertStringToHex(String str) {
        StringBuilder stringBuilder = new StringBuilder();

        char[] charArray = str.toCharArray();

        for (char c : charArray) {
            String charToHex = Integer.toHexString(c);
            stringBuilder.append(charToHex);
        }
        return stringBuilder.toString();
    }

    private static String randomHexInt() {
        Random r = new Random();

        int i = r.nextInt(Integer.MAX_VALUE);
        return Integer.toHexString(i);
    }
}
