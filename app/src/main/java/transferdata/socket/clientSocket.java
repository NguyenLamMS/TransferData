package transferdata.socket;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

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
import transferdata.encode.encryptAES;
import transferdata.encode.encryptRSA;
import transferdata.home.R;
import transferdata.transfer.client;
import transferdata.transfer.transferData;
public class clientSocket extends Thread {
    public String host;
    public Socket socket;
    transferData context ;
    List<String> data;
    public static int SIZE_ALL_FILE = 0;
    public static int size = 0;
    encryptRSA RSA;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 1;
    public clientSocket(String hostAdress, transferData context, List<String> data){
        socket = new Socket();
        host = hostAdress;
        this.context = context;
        this.data = data;
    }
    @Override
    public void run() {
        super.run();
        try {

            socket.connect(new InetSocketAddress(host,8888),500);
            System.out.println("Star client .................");
            BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
            DataOutputStream dos = new DataOutputStream(bos);
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutput = new ObjectOutputStream(outputStream);
            File file[] = new File[data.size()];

            // receive keyPublic RSA from server
            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            encryptRSA.publicKey = (PublicKey) inputStream.readObject();
            System.out.println("+++++++++++ public key " + encryptRSA.publicKey);

            // encrypt key AES using RSA
            RSA = new encryptRSA();
            byte[] keyEncode = RSA.encode(encryptAES.secretKey, encryptRSA.publicKey);

            // send key AES to server
            objectOutput.writeObject(keyEncode);
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
            showNotification();
            objectOutput.writeObject(s);
            objectOutput.flush();
            dos.writeInt(data.size());
            dos.writeInt(SIZE_ALL_FILE);
            dos.flush();
            index = 0;
            for(File f : file){
                long leng = f.length();
                dos.writeLong(leng);
                dos.writeUTF(data.get(index));
                index++;
                dos.flush();
                FileInputStream fileInput = new FileInputStream(f);
                BufferedInputStream bufferedInput = new BufferedInputStream(fileInput);
                int theByte = 0;
                while ((theByte = bufferedInput.read()) != -1){
                    bos.write(theByte);
                }
                bufferedInput.close();
                size += leng * 0.000001;
                transferData.speedTranfer();
                //set value progress notification
                float progress = ((float)size / (float)SIZE_ALL_FILE) * 100;
                mBuilder.setProgress(SIZE_ALL_FILE, size, false);
                mBuilder.setContentText( (int)Math.round(progress) + " %");
                mNotifyManager.notify(id, mBuilder.build());
            }
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(socket != null){
                if(socket.isConnected()){
                    try {
                        String message = "Transfer completed";
                        if(size < SIZE_ALL_FILE){
                            message = "Transfer false";
                        }
                        mBuilder.setContentText(message)
                                // Removes the progress bar
                                .setProgress(0, 0, false);
                        mNotifyManager.notify(id, mBuilder.build());
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void showNotification(){
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context, "123");
        mBuilder.setContentTitle("Transfer Data")
                .setSmallIcon(R.drawable.ic_icon_notification);

        String channelId = "transfer";
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId, "Transfer Data", NotificationManager.IMPORTANCE_LOW);
            mNotifyManager.createNotificationChannel(channel);
        }
        mBuilder.setChannelId(channelId);
        mBuilder.setProgress(SIZE_ALL_FILE, size, false);
        mBuilder.setContentText("0 %");
        // Displays the progress bar for the first time.
        mNotifyManager.notify(id, mBuilder.build());
    }
}
