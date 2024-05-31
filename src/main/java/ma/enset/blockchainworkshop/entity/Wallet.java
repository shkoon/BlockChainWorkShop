package ma.enset.blockchainworkshop.entity;

import lombok.Getter;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.security.spec.InvalidKeySpecException;

@Getter
public class Wallet {

    private PrivateKey privateKey;
    private PublicKey publicKey;
    private String address;

    public Wallet() {
        generateKeyPair();
        this.address = getAddressFromPublicKey(publicKey);
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public String getAddress() {
        return address;
    }

    private void generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstanceStrong();
            keyGen.initialize(2048, random);

            KeyPair pair = keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String getAddressFromPublicKey(PublicKey publicKey) {
        byte[] publicKeyBytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(publicKeyBytes);
    }

    public static PublicKey getPublicKeyFromAddress(String address) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyBytes = Base64.getDecoder().decode(address);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }

    public static String signTransaction(Transaction transaction, PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initSign(privateKey);

        String data = transaction.toString();
        rsa.update(data.getBytes());

        byte[] signature = rsa.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    public static boolean verifyTransaction(Transaction transaction, PublicKey publicKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        Signature rsa = Signature.getInstance("SHA256withRSA");
        rsa.initVerify(publicKey);

        String data = transaction.toString();
        rsa.update(data.getBytes());

        byte[] signature = Base64.getDecoder().decode(transaction.getSignature());
        return rsa.verify(signature);
    }

    public Transaction createTransaction(String recipient, double amount) {
        try {
            Transaction transaction = new Transaction(this.address, recipient, amount);
            String signature = signTransaction(transaction, this.privateKey);
            transaction.setSignature(signature);
            return transaction;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create transaction", e);
        }
    }
}