package utils;

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
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {
    public static void saveOrderHashToFile(String orderHash, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(orderHash);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadOrderHashFromFile(String filePath) {
        StringBuilder signature = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                signature.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signature.toString();
    }

    public static void saveSignatureToFile(String signature, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(signature);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadSignatureFromFile(String filePath) {
        StringBuilder signature = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                signature.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return signature.toString();
    }

    public static void savePublicKey(String publicKeyPath, PublicKey publicKey) throws Exception {
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(publicKeyPath))) {
            writer.write(publicKeyBase64);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PublicKey loadPublicKey(String publicKeyPath) throws Exception {
        StringBuilder publicKeyBase64 = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(publicKeyPath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                publicKeyBase64.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64.toString());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static void savePrivateKey(String privateKeyPath, PrivateKey privateKey) throws Exception {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(privateKeyPath))) {
            oos.writeObject(privateKey);
        }
    }

    public static PrivateKey loadPrivateKey(String privateKeyPath)
            throws ClassNotFoundException, FileNotFoundException, IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(privateKeyPath))) {
            return (PrivateKey) ois.readObject();
        }
    }
}
