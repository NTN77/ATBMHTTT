package model.service;

import model.bean.Key;
import model.bean.KeyOrderDTO;
import model.dao.KeyDAO;

import java.sql.Timestamp;
import java.util.List;

public class KeyService {
    public static KeyService instance;

    public static KeyService getInstance() {
        if (instance == null) instance = new KeyService();
        return instance;
    }

    public void insertPublicKey(String title, String publicKey, int userId) {
        KeyDAO.insertPublicKey(title, publicKey, userId);
    }

    public List<Key> getPublicKeyActiveByUserId(int userId) {

        return KeyDAO.getPublicKeyActiveByUserId(userId);
    }

    public void updateKeyUseTime(int keyId, Timestamp updateTime) {
        KeyDAO.updateKeyUseTime(keyId, updateTime);
    }
    public boolean hasActiveKey(int userId) {
        return KeyDAO.hasActiveKey(userId);
    }

    public List<KeyOrderDTO> getKeyOrderDTOByUserId(int userId) {
        return KeyDAO.getInformationPublicKeyForPayment(userId);
    }

    public String getPublicKeyByKeyID(int keyID) {
        return KeyDAO.getPublicKeyByKeyID(keyID);
    }
    public boolean verifyPublicKey(int keyId) {
        return KeyDAO.verifyPublicKey(keyId);
    }



    public int getStatusPublicKeyByKeyID(int keyID) {
        return KeyDAO.getStatusKey(keyID);
    }

    public void removePublicKey(int keyID) {
        KeyDAO.removeKey(keyID);
    }

    public static void main(String[] args) {
        KeyService keyService = new KeyService();
        System.out.println(keyService.getPublicKeyActiveByUserId(15));
    }

    public boolean isExistPublicKey(int userId, String title) {
        return KeyDAO.isExistPublicKey(userId, title);
    }
}
