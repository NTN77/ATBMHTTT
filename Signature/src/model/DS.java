package model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DS {
	KeyPair keyPair;
	Signature signature;
	public PublicKey publicKey;
	public PrivateKey privateKey;

		keyPair = keyPairGenerator.generateKeyPair();
	}

		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}


	signature.initVerify(publicKey);


}

	public String sign(String message) throws InvalidKeyException, SignatureException {
		byte[] data = message.getBytes();
		signature.initSign(privateKey);
		signature.update(data);
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



	public static PrivateKey loadPrivateKey(String filePath) throws Exception {
		String key = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)))
				.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "")
				.replaceAll("\\s", "");
		byte[] decodedKey = Base64.getDecoder().decode(key);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
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

		}
	}
}