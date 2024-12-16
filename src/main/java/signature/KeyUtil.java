package signature;

import java.io.*;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyUtil {
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

    public static void savePublicKey(String publicKeyPath, PublicKey publicKey) {
        try (PrintWriter out = new PrintWriter(publicKeyPath)) {
            String pubKeyPEM = convertToPEM(publicKey, "PUBLIC KEY");
            out.println(pubKeyPEM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PublicKey loadPublicKey(String filePath) throws Exception {
        String key = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)))
                .replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePublic(spec);
    }

    public static void savePrivateKey(String privateKeyPath, PrivateKey privateKey) {
        try (PrintWriter out = new PrintWriter(privateKeyPath)) {
            String priKeyPEM = convertToPEM(privateKey, "PRIVATE KEY");
            out.println(priKeyPEM);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static PrivateKey loadPrivateKey(String filePath) throws Exception {
        String key = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)))
                .replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decodedKey = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("DSA");
        return keyFactory.generatePrivate(spec);
    }

    private static String convertToPEM(Key key, String type) {
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        StringBuilder pemFormat = new StringBuilder();
        pemFormat.append("-----BEGIN ").append(type).append("-----\n");
        // Chia base 64 thành các dòng nhỏ.
        for (int i = 0; i < encodedKey.length(); i += 64) {
            int end = Math.min(i + 64, encodedKey.length());
            pemFormat.append(encodedKey, i, end).append("\n");
        }
        pemFormat.append("-----END ").append(type).append("-----");

        // Trả về chuỗi PEM mà không thay thế ký tự dòng mới và ký tự điều khiển
        return pemFormat.toString();
    }
    public static void main(String[] args) throws Exception {
        DS ds;
        for (int i = 1; i <= 5; i++) {
            ds = new DS("DSA", "SHA1PRNG", "SUN");
            ds.genKey();
            KeyUtil.savePublicKey("public_" + i + ".txt", ds.publicKey);
            KeyUtil.savePrivateKey("private_" + i + ".txt", ds.privateKey);
        }
    }
}
