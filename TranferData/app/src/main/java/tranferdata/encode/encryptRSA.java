package tranferdata.encode;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class encryptRSA {
    public static void encode(String file_loc, String file_des, SecretKeySpec secretKeySpec) throws Exception {
        byte[] data = new byte[2048];
        int i;
        System.out.println(file_loc);
        System.out.println(file_des);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
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
        System.out.println("encrypted file created");
    }

    public static void decode(String file_loc, String file_des, SecretKeySpec secretKeySpec)
            throws Exception {
        byte[] data = new byte[1024];
        int i;
        System.out.println("start decyption");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
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
        System.out.println("decrypted file created");
    }

    public static void rsaEncrypt(String file_loc, String file_des, PublicKey pubKey) throws Exception {
        byte[] data = new byte[2048];
        int i;
        System.out.println(file_loc);
        System.out.println(file_des);
        Cipher cipher = Cipher.getInstance("RSA","BC");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
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
        System.out.println("encrypted file created");
    }

    public static void rsaDecrypt(String file_loc, String file_des, PrivateKey priKey)
            throws Exception {
        byte[] data = new byte[1024];
        int i;
        System.out.println("start decyption");
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
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
        System.out.println("decrypted file created");
    }

}
