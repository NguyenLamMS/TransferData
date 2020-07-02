package transferdata.transfer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.daimajia.numberprogressbar.NumberProgressBar;
import pl.droidsonroids.gif.GifImageView;
import transferdata.connect.connect;
import transferdata.home.R;
import transferdata.socket.serverSocket;
import transferdata.service.*;

public class receiveData extends Activity{
     NumberProgressBar numberProgressBar;
     TextView txt_speed;
     TextView title_time;
     Button btn_done;
     LinearLayout linear_progress;
     int time[] = new int[3];
     Handler handler;
     boolean running;
     GifImageView restoreLoad;
     public static int countThreadStop = 0;
     private final int CODE_RESTORE_SMS = 121;
     int sizeOld = 0, sizeNew = 0, speed = 0, estimated_time = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receive_data);
        numberProgressBar = findViewById(R.id.number_progress_bar);
        txt_speed = findViewById(R.id.txt_speed);
        title_time = findViewById(R.id.title_time);
        btn_done = findViewById(R.id.btn_done);
        restoreLoad = findViewById(R.id.restore_load);
        linear_progress = findViewById(R.id.linear_progress);
        numberProgressBar.setMax(serverSocket.SIZE_ALL_FILE);
        numberProgressBar.setProgressTextSize(30);
        numberProgressBar.setReachedBarHeight(30);
        handler = new Handler();
        running = true;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
            while (serverSocket.size < serverSocket.SIZE_ALL_FILE && running == true){
                System.out.println("RUN....." + serverSocket.size + " " + serverSocket.SIZE_ALL_FILE );
                speedTranfile();
                sizeNew = serverSocket.size;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        numberProgressBar.setProgress(serverSocket.size);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(serverSocket.listItem != null && (serverSocket.size >= serverSocket.SIZE_ALL_FILE)){
                restoreData();
            }
            }

        });
        thread.start();
        clickButtonDone();
    }
    public void speedTranfile(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                speed = sizeNew - sizeOld;
                sizeOld = sizeNew;
                if(speed != 0){
                    estimated_time = (serverSocket.SIZE_ALL_FILE - serverSocket.size) / speed;
                    time[0] = estimated_time / 3600;
                    estimated_time = estimated_time - (time[0] * 3600);
                    time[1] = estimated_time / 60;
                    time[2] = estimated_time - (time[1] * 60);

                    if(time[0] != 0 && time[1] != 0 && time[2] != 0){
                        txt_speed.setText(time[0] + " h" + time[1] + " m " + time[2] +" s ");
                    }else if(time[0] == 0 && time[1] != 0 && time[2] != 0){
                        txt_speed.setText(time[1] + " m " + time[2] +" s ");
                    }else if(time[0] == 0 && time[1] == 0 && time[2] != 0){
                        txt_speed.setText(time[2] +" s ");
                    }
                }
            }
        });
    }
    void restoreData(){
        countThreadStop = 0;
        for(int i = 0; i<serverSocket.listItem.length ;i++){
            if(i == 3 || i == 4 || i ==6)
                continue;
            if(serverSocket.listItem[i]){
                countThreadStop++;
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                title_time.setVisibility(View.GONE);
                restoreLoad.setVisibility(View.VISIBLE);
                numberProgressBar.setMax(100);
                numberProgressBar.setProgress(100);
                txt_speed.setText("Restore Data");
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {

                // restore contact
                if(serverSocket.listItem[0]){
                    getContact contact = new getContact(receiveData.this);
                    contact.restoreContacts();
                }
                // restore call log
                if(serverSocket.listItem[1]){
                    getCallLog callLog = new getCallLog(receiveData.this);
                    callLog.restoreCallogs();
                }
                // restore messenger
                if(serverSocket.listItem[2]){
                    getMessenger messenger = new getMessenger(receiveData.this);
                    messenger.restoreMessages();
                }
                // restore application
                if(serverSocket.listItem[5]){
                    getApplication application = new getApplication(receiveData.this);
                    application.restoreApps();
                }
                // show button don and hidden progress bar
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linear_progress.setVisibility(View.GONE);
                        btn_done.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        connect.isConnect(receiveData.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // restore messages
        if (requestCode == CODE_RESTORE_SMS) {
            getMessenger.executeRestore();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    void clickButtonDone(){
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        running = false;
        super.onDestroy();
    }
}

