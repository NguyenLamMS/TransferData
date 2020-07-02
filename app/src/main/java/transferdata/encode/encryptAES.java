package transferdata.encode;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
public class encryptAES {
    public static SecretKey secretKey  = null;
    public static void encode(String file_loc, String file_des, SecretKey secretKey) throws Exception {
        byte[] data = new byte[2048];
        int i;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        FileInputStream fileIn = new FileInputStream(file_loc);
        FileOutputStream fileOut = new FileOutputStream(file_des);
        CipherOutputStream cipherOut = new CipherOutputStream(fileOut, cipher);
        // Read in the data from the file and encrypt it
        while ((i = fileIn.read(data)) != -1) {
            cipherOut.write(data, 0, i);
        }
        // Close the encrypted file
        cipherOut.close();
        fileIn.close();
    }

    public static void decode(String file_loc, String file_des, SecretKey secretKey)
            throws Exception {
        byte[] data = new byte[1024];
        int i;
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        FileInputStream fileIn = new FileInputStream(file_loc);
        CipherInputStream cipherIn = new CipherInputStream(fileIn, cipher);
        FileOutputStream fileOut = new FileOutputStream(file_des);
        // Write data to new file
        while ((i = cipherIn.read()) != -1) {
            fileOut.write(i);
        }
        // Close the file
        fileIn.close();
        cipherIn.close();
        fileOut.close();
    }
    public void createKey(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            secretKey  =  keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public void encrypt(File file){
        try {
            File fileTemp = new File(file.getParent() + "/temp");
            encode(file.getPath(), fileTemp.getPath(), secretKey);
            file.delete();
            fileTemp.renameTo(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public File decrypt(File file){
        File fileTemp = new File(file.getParent() + "/temp");
        try {
            decode(file.getPath(), fileTemp.getPath(), secretKey);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileTemp;
    }
}
