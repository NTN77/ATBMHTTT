package model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class DS {
	KeyPair keyPair;
	Signature signature;
	public PublicKey publicKey;
	public PrivateKey privateKey;

	public DS(String alg) throws Exception {
		// Ensure we are using the right algorithm for RSA with SHA-256
		if (!"SHA256withRSA".equals(alg)) {
			throw new IllegalArgumentException("This class only supports SHA256withRSA");
		}
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		keyPair = keyPairGenerator.generateKeyPair();
		signature = Signature.getInstance("SHA256withRSA");
	}

	public DS( PublicKey publicKey, PrivateKey privateKey) throws Exception {
		signature = Signature.getInstance("SHA256withRSA");
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}


//	//Xác minh cữ ký số.
//	public boolean verify(String message, String sign) throws InvalidKeyException, SignatureException {
//		signature.initVerify(publicKey);
//		byte[] data = message.getBytes();
//		byte[] signValue = Base64.getDecoder().decode(sign);
//		signature.update(data);
//		return signature.verify(signValue);
//	}
public boolean verifySign(String orderHash, String signatureInput) throws Exception {
	// Cấu hình khóa công khai để xác minh chữ ký
	signature.initVerify(publicKey);

	// Cập nhật mã băm vào đối tượng Signature
	signature.update(orderHash.getBytes(StandardCharsets.UTF_8));

	// Giải mã chữ ký từ Base64
	byte[] signatureBytes = Base64.getDecoder().decode(signatureInput);

	// Xác minh chữ ký
	return signature.verify(signatureBytes);
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

	public static PublicKey loadPublicKeyFromPem(String pem) throws Exception {
		String publicKeyPEM = pem.replace("-----BEGIN PUBLIC KEY-----", "")
				.replace("-----END PUBLIC KEY-----", "")
				.replaceAll("\\s", "");
		byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
		return keyFactory.generatePublic(spec);
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
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
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






	public static void main(String[] args) {
		try {
			// Public key ở định dạng PEM
			String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n" +
					"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2mlIGzpi9oZm8cKcnHMI\n" +
					"7scdgPdlfyCFlwhfU7VHfTgICSU6ryEG8jIzOKWaBI1op7n+kRIQzzCpB4tOH/5u\n" +
					"H2m3Xfcpe2UCfnbBYuiEEGT/UL5i8HKl1CedMfWdfuwltHH/bmaKU7GRDjdCF2xq\n" +
					"nJ3eFKin8G0Lu+5r20yVs+ZlttDTO2O9xK0qCsfmIOzyGiRq6hzXM2R/Y2Ozf/z8\n" +
					"zYlzT7iN9G5LmPvNcns0D5LME08repm62RGtQAGblAKlF1nkH2TcokMlPKt+jdDT\n" +
					"H6e9x7NUh3WfGMnbbRShEsxdjKZjvKV3mFnyQ+6Ec4gdmMwocj0MABjxolh9DvYG\n" +
					"WwIDAQAB\n" +
					"-----END PUBLIC KEY-----";
			PublicKey publicKey = DS.loadPublicKeyFromPem(publicKeyPem);

			// Tạo đối tượng DS với public key
			DS ds = new DS(publicKey, null);

			// Mã băm của đơn hàng (SHA-256) và chữ ký (Base64)
			String orderHash = "ad9ea2fcc700aaa799c27123f3f50fa7779e7332276647bf64168dd9e680993b";
			String signatureInput = "VcCK4/YirJoZACjDqGUfcvRFzuf0jGrI5yEHXMymVE/CLI0PXQnZ7HAxCIhkGxM9wfmZW9eXgfWGp1HK+3J1UhwZXbiz1VQHImibHJikQxTnCRG8yxv5E5alWa05xaKosedAMmXT/Jp+gUm2/2Fx2QXm0YSZY7H4KAqkcBbkGjwfM6q2AKyAIm1yTl/DexkCwHWLgdJcAYSvRydHNO1t5NFGqpRGnPT3geV71xxU39WtCiUT5eF7vXh81nTd0RtkR8SUNQUqx9Ik62fqHBdd+VQxSbdB2XxasNw9G2Se1AQ4Mx27rRlzKV1Y4RgzKCpEAv6W9Wo7DEHwjz+Bve3T9A==";

			// Xác minh chữ ký và nhận mã hash
			boolean isValid  = ds.verifySign(orderHash, signatureInput);

			// In kết quả
			if (isValid) {
				System.out.println("Chữ ký hợp lệ!");
			} else {
				System.out.println("Chữ ký không hợp lệ!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}