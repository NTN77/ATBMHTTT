package model.service;

import model.dao.KeyDAO;

public class KeyService {
    public static KeyService instance;

    public static KeyService getInstance() {
        if (instance == null) instance = new KeyService();
        return instance;
    }

    public void insertPublicKey(String title, String publicKey, int userId) {
        KeyDAO.insertPublicKey(title, publicKey, userId);
    }

    public static void main(String[] args) {
        KeyService keyService = new KeyService();
        keyService.insertPublicKey("iphone6-KEQIC", "-----BEGIN PUBLIC KEY-----\n" +
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA2mlIGzpi9oZm8cKcnHMI\n" +
                "7scdgPdlfyCFlwhfU7VHfTgICSU6ryEG8jIzOKWaBI1op7n+kRIQzzCpB4tOH/5u\n" +
                "H2m3Xfcpe2UCfnbBYuiEEGT/UL5i8HKl1CedMfWdfuwltHH/bmaKU7GRDjdCF2xq\n" +
                "nJ3eFKin8G0Lu+5r20yVs+ZlttDTO2O9xK0qCsfmIOzyGiRq6hzXM2R/Y2Ozf/z8\n" +
                "zYlzT7iN9G5LmPvNcns0D5LME08repm62RGtQAGblAKlF1nkH2TcokMlPKt+jdDT\n" +
                "H6e9x7NUh3WfGMnbbRShEsxdjKZjvKV3mFnyQ+6Ec4gdmMwocj0MABjxolh9DvYG\n" +
                "WwIDAQAB\n" +
                "-----END PUBLIC KEY-----", 15);
    }
}
