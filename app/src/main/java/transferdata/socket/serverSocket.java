package transferdata.socket;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.core.app.NotificationCompat;

import transferdata.connect.connect;
import transferdata.encode.encryptAES;
import transferdata.encode.encryptRSA;
import transferdata.home.R;
import transferdata.transfer.receiveData;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
public class serverSocket extends AsyncTask<String, String, String> {
    public Socket client;
    public ServerSocket serverSocket;
    Activity context;
    public static int size = 0;
    public static int SIZE_ALL_FILE = 0;
    public static boolean listItem[];
    encryptRSA RSA;
    encryptAES AES;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    int id = 2;
    public serverSocket(Activity context){
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {
        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("+++++++++++++++++++ wait client");
            client = serverSocket.accept();
           // send keyPublic to client
            ObjectOutputStream outputStream = new ObjectOutputStream(client.getOutputStream());
            RSA = new encryptRSA();
            RSA.createKey();
            outputStream.writeObject(encryptRSA.publicKey);
            outputStream.flush();
            System.out.println("++++++++++ public key " + encryptRSA.publicKey);

            size = SIZE_ALL_FILE = 0;
            BufferedInputStream bis = new BufferedInputStream(client.getInputStream());
            DataInputStream dis = new DataInputStream(bis);
            InputStream inputStream = client.getInputStream();
            ObjectInputStream objectInput = new ObjectInputStream(inputStream);

            // receice key AES from client
            byte[] keyEncode = (byte[]) objectInput.readObject();
            // decode key AES
            RSA = new encryptRSA();
            encryptAES.secretKey = RSA.decode(keyEncode, encryptRSA.privateKey);
            // receice list item check
            listItem = (boolean[]) objectInput.readObject();

            int filesCount = dis.readInt();
            SIZE_ALL_FILE = dis.readInt();
            showNotification();
            // start activity receive
            Intent intent = new Intent(context, receiveData.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
            context.finish();

            for(int i = 0;i< filesCount;i++){
                long fileLeng = dis.readLong();
                String fileName = dis.readUTF();
                System.out.println("++++++++ file name" + fileName);

                File f = new File(fileName);
                File dirs = new File(f.getParent());
                if (!dirs.exists()) {
                    dirs.mkdirs();
                }

                // if create dir parent false then save file to tranfer folder
                if(!dirs.exists()){
                    String path = Environment.getExternalStorageDirectory() +"/Transfer Data/" + f.getName();
                    f = new File(path);
                    dirs = new File(f.getParent());
                    if(!dirs.exists()){
                        dirs.mkdir();
                    }
                }

                FileOutputStream fileOutput = new FileOutputStream(f);
                BufferedOutputStream bufferedOutput = new BufferedOutputStream(fileOutput);
                float sumSize = 0;
                for(long j = 0;j<fileLeng;j++){
                    sumSize += 0.000001;
                    if(sumSize == 1){
                        size++;
                        sumSize = 0;

                        //set value progress notification
                        mBuilder.setProgress(SIZE_ALL_FILE, size, false);
                        float progress = (float)size/(float)SIZE_ALL_FILE * 100;
                        if(progress > 100) progress = 100;
                        mBuilder.setContentText((int)Math.round(progress) + " %");
                        // Displays the progress bar for the first time.
                        mNotifyManager.notify(id, mBuilder.build());

                    }
                    bufferedOutput.write(bis.read());

                }
                bufferedOutput.close();
                addInfoFile(f);
            }
            dis.close();
            serverSocket.close();
            client.close();
            // disconnect p2p
            connect.manager.removeGroup(connect.channel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onFailure(int reason) {

                }
            });

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                String message = "Transfer completed";
                if(size < SIZE_ALL_FILE){
                    message = "Transfer false";
                }
                mBuilder.setContentText(message)
                        // Removes the progress bar
                        .setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                serverSocket.close();
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void showNotification(){
        mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(context, "321");
        mBuilder.setContentTitle("Receive Data")
                .setSmallIcon(R.drawable.ic_icon_notification);

        String channelId = "receive";
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(
                    channelId, "Receive Data", NotificationManager.IMPORTANCE_LOW);
            mNotifyManager.createNotificationChannel(channel);
        }
        mBuilder.setChannelId(channelId);
        mBuilder.setProgress(SIZE_ALL_FILE, size, false);
        mBuilder.setContentText("0 %");
        // Displays the progress bar for the first time.
        mNotifyManager.notify(id, mBuilder.build());
    }

    private void addInfoFile(File f) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
