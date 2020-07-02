package tranferdata.socket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import tranferdata.encode.encryptRSA;
import tranferdata.tranfer.receiveData;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
public class serverSocket extends AsyncTask<String, String, String> {
    public Socket client;
    public ServerSocket serverSocket;
    Context context;
    public static int size = 0;
    public static int SIZE_ALL_FILE = 0;
    public static boolean listItem[];
    PublicKey publicKey;
    PrivateKey privateKey;
    public serverSocket(Context context){
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            serverSocket = new ServerSocket(8888);
           while (true){
               System.out.println("+++++++++++++++++++ wait client");
               client = serverSocket.accept();
               // create key RSA
               createKey();
               // send keyPublic to client
               ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
               outputStream.writeObject(publicKey);
               outputStream.flush();
               System.out.println("++++++++++ public key " + publicKey);


               size = SIZE_ALL_FILE = 0;
               BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
               DataInputStream dis = new DataInputStream(bis);
               InputStream inputStream = client.getInputStream();
               ObjectInputStream objectInput = new ObjectInputStream(inputStream);
               try {
                   listItem = (boolean[]) objectInput.readObject();
               } catch (ClassNotFoundException e) {
                   e.printStackTrace();
               }

               int filesCount = dis.readInt();
               SIZE_ALL_FILE = dis.readInt();

               Intent intent = new Intent(context, receiveData.class);
               context.startActivity(intent);

               for(int i = 0;i< filesCount;i++){
                   long fileLeng = dis.readLong();
                   String fileName = dis.readUTF();
                   System.out.println("++++++++ file name" + fileName);
                   File f = new File(fileName);
                   File dirs = new File(f.getParent());
                   if (!dirs.exists()) {
                        dirs.mkdirs();
                   }
                   FileOutputStream fileOutput = new FileOutputStream(f);
                   BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
                   float sumSize = 0;
                   for(long j = 0;j<fileLeng;j++){
                       sumSize += 0.000001;
                       if(sumSize == 1){
                           size++;
                           sumSize = 0;
                       }
                       bufferedOutput.write(bis.read());
                   }
                   bufferedOutput.close();
                 //  size += fileLeng * 0.000001);
               }
               dis.close();
           }

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // create privateKey vaf publicKey RSA
    void createKey(){
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
