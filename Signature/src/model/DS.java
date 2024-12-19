package model;

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
		String dir = "key";
		DS ds;
		for (int i = 1; i <= 10; i++) {
			ds = new DS("DSA", "SHA1PRNG", "SUN");
			ds.genKey();
			savePublicKey(dir + "/public_" + i + ".txt", ds.publicKey);
			savePrivateKey(dir + "/private_" + i + ".txt", ds.privateKey);
		}

//		PublicKey loadedPublicKey = loadPublicKey(dir + "/public_" + 1 + ".pem");
//		System.out.println(loadedPublicKey);
//
//		PrivateKey loadedPrivateKey = loadPrivateKey(dir + "/private_" + 1 + ".pem");
//		System.out.println(loadedPrivateKey);
	}
}