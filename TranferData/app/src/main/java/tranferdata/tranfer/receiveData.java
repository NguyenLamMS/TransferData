package tranferdata.tranfer;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.daimajia.numberprogressbar.NumberProgressBar;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import pl.droidsonroids.gif.GifImageView;
import tranferdata.home.R;
import tranferdata.socket.serverSocket;
import tranferdata.service.*;

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
            if(serverSocket.listItem != null){
                restoreData();
            }
            }

        });
        thread.start();

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
        System.out.println("countThreadStop" + countThreadStop);
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

       if(serverSocket.listItem[0]){
           Thread threadContact = new Thread(new Runnable() {
               @Override
               public void run() {
                   getContact contact = new getContact(receiveData.this);
                   contact.restoreContacts();
                   countThreadStop--;
               }
           });
           threadContact.start();
       }
       if(serverSocket.listItem[1]){
           Thread threadCallLog = new Thread(new Runnable() {
               @Override
               public void run() {
                   getCallLog callLog = new getCallLog(receiveData.this);
                   callLog.restoreCallogs();
                   countThreadStop--;
               }
           });
           threadCallLog.start();
       }
       if(serverSocket.listItem[2]){
          Thread threadMessenger = new Thread(new Runnable() {
              @Override
              public void run() {
                  getMessenger messenger = new getMessenger(receiveData.this);
                  messenger.restoreMessages();
                  countThreadStop--;
              }
          });
          threadMessenger.start();
       }
       if(serverSocket.listItem[5]){
           Thread threadApp = new Thread(new Runnable() {
               @Override
               public void run() {
                   getApplication application = new getApplication(receiveData.this);
                   application.restoreApps();
                   countThreadStop--;
               }
           });
           threadApp.start();
       }
       // wait start show button done
      new Thread(new Runnable() {
          @Override
          public void run() {
              try {
                  while (running){
                      Thread.sleep(1000);
                      if(countThreadStop == 0){
                         runOnUiThread(new Runnable() {
                             @Override
                             public void run() {
                                 linear_progress.setVisibility(View.GONE);
                                 btn_done.setVisibility(View.VISIBLE);
                             }
                         });
                          break;
                      }
                  }
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
      }).start();
    }

    @Override
    public void onBackPressed() {
        // stop thread
        running = false;
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // restore messages
        if (requestCode == CODE_RESTORE_SMS) {
                executeRestore();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void executeRestore(){
        String path = getExternalFilesDir(null) + "/messages/messages.xml";
        File file = new File(path);
        Log.d("messenger", "executeRestore: " + path);
        if (file.exists()) {
            Log.d("messenger", "restoreMessages: ");
            try {
                Document dom;
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                db = dbf.newDocumentBuilder();
                dom = db.parse(file);
                NodeList nl = dom.getElementsByTagName("message");
                for (int i = 0; i < nl.getLength(); i++) {
                    ContentValues contentValues = new ContentValues();
                    NodeList childNode = nl.item(i).getChildNodes();
                    for (int j = 0; j < childNode.getLength(); j++) {
                        Node node = childNode.item(j);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            contentValues.put(node.getNodeName(), node.getTextContent());
                            Log.d("test:", node.getNodeName() + ": " + node.getTextContent());
                        }

                    }
                    getContentResolver().insert(Telephony.Sms.CONTENT_URI, contentValues);
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ;
        }else{
            Log.d("messenger", "restoreMessages: not" );
        }
    }
}

