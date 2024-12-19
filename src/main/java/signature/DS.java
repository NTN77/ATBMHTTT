package signature;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DS {
    KeyPair keyPair;
    SecureRandom secureRandom;
    Signature signature;
    public PublicKey publicKey;
    public PrivateKey privateKey;

    public DS(String alg, String algRandom, String prov) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(alg, prov);
        secureRandom = SecureRandom.getInstance(algRandom, prov);
        keyPairGenerator.initialize(1024, secureRandom);
        keyPair = keyPairGenerator.generateKeyPair();
        signature = Signature.getInstance(alg, prov);
    }

    public DS(String alg, String prov, PublicKey publicKey, PrivateKey privateKey) throws Exception {
        signature = Signature.getInstance(alg, prov);
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public boolean genKey() {
        if (keyPair == null) {
            if (publicKey != null && privateKey != null)
                return true;
            return false;
        }
        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();
        return true;
    }

    public boolean verify(String message, String sign) throws InvalidKeyException, SignatureException {
        signature.initVerify(publicKey);
        byte[] data = message.getBytes();
        byte[] signValue = Base64.getDecoder().decode(sign);
        signature.update(data);
        return signature.verify(signValue);
    }

    public boolean verifyFile(String path, String sign) throws Exception {
        signature.initVerify(publicKey);
        byte[] signValue = Base64.getDecoder().decode(sign);

        signature.initVerify(publicKey);

        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        byte[] buff = new byte[1024];
        int read;
        while ((read = bis.read(buff)) != -1) {
            signature.update(buff, 0, read);
        }
        bis.close();

        return signature.verify(signValue);
    }

    public String sign(String message) throws InvalidKeyException, SignatureException {
        byte[] data = message.getBytes();
        signature.initSign(privateKey);
        signature.update(data);
        byte[] sign = signature.sign();
        return Base64.getEncoder().encodeToString(sign);
    }

    public String signFile(String srcFilePath) throws InvalidKeyException, SignatureException, IOException {
        signature.initSign(privateKey);
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFilePath));
        byte[] buff = new byte[1024];
        int read;
        while ((read = bis.read(buff)) != -1) {
            signature.update(buff, 0, read);
        }
        bis.close();
        byte[] sign = signature.sign();
        return Base64.getEncoder().encodeToString(sign);
    }
}