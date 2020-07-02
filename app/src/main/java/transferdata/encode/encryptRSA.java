package transferdata.encode;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class encryptRSA {
    public static PublicKey publicKey = null;
    public static PrivateKey privateKey = null;
    public byte[] encode(SecretKey key, PublicKey pubKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        byte encryptOut[] = cipher.doFinal(key.getEncoded());
        return encryptOut;
    }

    public SecretKey decode(byte[] data, PrivateKey priKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new SecretKeySpec(cipher.doFinal(data), "AES");
    }
    public void createKey(){
        try {
            SecureRandom sr = new SecureRandom();
            KeyPairGenerator kpg = null;
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(1024, sr);
            KeyPair kp = kpg.genKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
