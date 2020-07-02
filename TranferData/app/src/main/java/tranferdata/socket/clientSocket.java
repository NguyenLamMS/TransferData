package tranferdata.socket;
import android.app.Activity;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.PublicKey;
import java.util.List;
import tranferdata.tranfer.client;
import tranferdata.tranfer.tranferData;

public class clientSocket extends Thread {
    public String host;
    public Socket socket;
    tranferData context ;
    List<String> data;
    public static int SIZE_ALL_FILE = 0;
    public PublicKey publicKey;
    public static int size = 0;
    public clientSocket(String hostAdress, tranferData context, List<String> data){
        socket = new Socket();
        host = hostAdress;
        this.context = context;
        this.data = data;
    }
    @Override
    public void run() {
        super.run();
        try {

            socket.connect(new InetSocketAddress(host,8888),5000);
            System.out.println("Star client .................");

            // receive keyPublic from server
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            try {
                publicKey = (PublicKey) inputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            System.out.println("+++++++++++ public key " + publicKey);


            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dos = new DataOutputStream(bos);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
            File file[] = new File[data.size()];

            // get size all file
            int index = 0;
            size = SIZE_ALL_FILE = 0;
            for(String s : data){
                file[index] = new File(s);
                SIZE_ALL_FILE += file[index].length() * 0.000001;
                index++;
            }
            // get list item check
            boolean s[] = new boolean[client.listItem.size()];
            for(int i=0 ;i< client.listItem.size();i++){
                s[i] = client.listItem.get(i).isChecked();
            }
            objectOutput.writeObject(s);
            objectOutput.flush();
            dos.writeInt(data.size());
            dos.writeInt(SIZE_ALL_FILE);
            dos.flush();

            // send file
             index = 0;
//            String SECRET_KEY = "stackjava.com.if";
//            SecretKeySpec skeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            for(File f : file){
                long leng = f.length();
                dos.writeLong(leng);
                String name = f.getName();
                dos.writeUTF(data.get(index));
                index++;
                dos.flush();

//                try {
//                    encryptRSA.encode(f.getParent() + "/abc.txt",f.getParent() + "/lam.txt", skeySpec);
//                    encryptRSA.decode(f.getParent() + "/lam.txt",f.getParent() + "/trang.txt", skeySpec);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                FileInputStream fileInput = new FileInputStream(f);
                BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
                int theByte = 0;
                while ((theByte = bufferedInput.read()) != -1){
                    bos.write(theByte);
                }
                bufferedInput.close();
                size += leng * 0.000001;
                speedTranfer();
            }
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(socket != null){
                if(socket.isConnected()){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    void speedTranfer() {
        int sizeItem = 0;
        for(int i = 0 ;i < context.sizeItem.size() ; i++) {
            sizeItem += context.sizeItem.get(i).intValue();
            System.out.println("SIZE "+ sizeItem + " " + size);
            if (size >= sizeItem) {
                if (context.listItem.get(i).isStatusLoad()) {
                    context.listItem.get(i).setStatusLoad(false);
                     context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }
    }
}
