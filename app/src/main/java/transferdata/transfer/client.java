package transferdata.transfer;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import transferdata.adapter.*;
import transferdata.connect.connect;
import transferdata.encode.encryptAES;
import transferdata.home.R;
import transferdata.media.app;
import transferdata.media.getAudio;
import transferdata.media.getDataImage;
import transferdata.media.getFile;
import transferdata.media.getVideo;
import transferdata.service.getApplication;
import transferdata.service.getCallLog;
import transferdata.service.getMessenger;
import transferdata.service.getContact;
public class client extends Activity {
    public static List<item> listItem;
    ListView listView_item;
    Button btn_start;
    Button btn_cancel;
    public static adapter adapter;
    getContact contact;
    getMessenger messenger;
    getCallLog callLog;
    getApplication application;
    getFile file;
    item item;
    String Adress;
    getDataImage getImage = new getDataImage();
    getVideo video = new getVideo(client.this);
    getAudio audio = new getAudio(client.this);
    encryptAES encrypt;
    public static int SIZE_ALL_ITEM[];
    final String info = "Selected : 0 item - 0 MB";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_item);
        listItem = new ArrayList<>();
        item = new item(true,R.drawable.ic_contact, "Contact", info, true);
        listItem.add(item);
        item = new item(true,R.drawable.call_log,"Call log", info, true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_message,"Messenger", info, true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_image,"Photo", info, true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_video,"Video", info, true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_apps,"App",  info, true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_audio,"Audio",  info, true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_file,"File",  info, true);
        listItem.add(item);
        adapter = new adapter(this, R.layout.list_view_item, listItem);
        listView_item = findViewById(R.id.list_item);
        listView_item.setAdapter(adapter);
        SIZE_ALL_ITEM = new int[listItem.size()];
        Intent intent = getIntent();
        Adress = intent.getStringExtra("address");
        click_button();

        //create key AES
        encrypt = new encryptAES();
        encrypt.createKey();

       // get all data
        getData();
    }
    void click_button(){
        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            connect.manager.requestConnectionInfo(connect.channel, new WifiP2pManager.ConnectionInfoListener() {
                @Override
                public void onConnectionInfoAvailable(WifiP2pInfo info) {
                    if(info.groupFormed){
                        Intent intent = new Intent(client.this, transferData.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("address", Adress);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(client.this, "Failed to connect ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            });
            }
        });
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connect.isConnect(client.this);
            }
        });
    }
    @Override
    public void onBackPressed() {
        connect.isConnect(client.this);
    }

    void getData(){
        // get all contact
        Thread threadContact = new Thread(new Runnable() {
            @Override
            public void run() {
                contact = new getContact(client.this);
                item = new item(true,R.drawable.ic_contact, "Contact", contact.backupContacts(), false);
                listItem.set(0, item);
                updateUI();
            }
        });
        threadContact.start();
        //get call log
        Thread threadCallLog = new Thread(new Runnable() {
            @Override
            public void run() {
                callLog = new getCallLog(client.this);
                item = new item(true,R.drawable.call_log, "Call log", callLog.backupCallogs(), false);
                listItem.set(1,item);
                updateUI();
            }
        });
        threadCallLog.start();
        // get all messenger
        Thread threadMessenger = new Thread(new Runnable() {
            @Override
            public void run() {
                messenger = new getMessenger(client.this);
                try {
                    item = new item(true,R.drawable.ic_message,"Messenger", messenger.backupMessages(), false);
                    listItem.set(2,item);
                    updateUI();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        threadMessenger.start();
        // get all image and video
        Thread threadGallery = new Thread(new Runnable() {
            @Override
            public void run() {
                //get image
                getImage.getImagesPath(client.this);
                String s = "Selected : " +getImage.getLeng() + " item - "+ getImage.getSize()+" MB";
                item = new item(true,R.drawable.ic_image,"Gallery", s, false);
                listItem.set(3,item);

                //get video
                video.getVideoPath();
                item = new item(true,R.drawable.ic_video,"Video", video.getleng(), false);
                listItem.set(4,item);
                updateUI();
            }
        });
        threadGallery.start();

        // get all application
        Thread threadApplication = new Thread(new Runnable() {
            @Override
            public void run() {
                application = new getApplication(client.this);
                item = new item(true,R.drawable.ic_apps,"App", application.backupApps(), false);
                listItem.set(5,item);
                updateUI();
            }
        });
        //get all audio
        Thread threadAudio = new Thread(new Runnable() {
            @Override
            public void run() {
                audio.getAllAudio();
                item = new item(true,R.drawable.ic_audio,"Audio",  audio.getSize(), false);
                listItem.set(6, item);
            }
        });
        threadAudio.start();
        // get all file
        Thread threadFie = new Thread(new Runnable() {
            @Override
            public void run() {
                file = new getFile(client.this);
                file.getAllFile();
                item = new item(true,R.drawable.ic_file,"File",  file.getSize(), false);
                listItem.set(7,item);
            }
        });
        threadFie.start();
        threadApplication.start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // reload data
        if(requestCode == 0 && resultCode == Activity.RESULT_OK){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    listItem.get(0).setStatusLoad(true);
                    updateUI();
                    item = new item(true,R.drawable.ic_contact, "Contact", contact.backupContacts(), false);
                    listItem.set(0, item);
                    updateUI();
                }
            }).start();
        }else if(requestCode == 2 && resultCode == Activity.RESULT_OK ){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        listItem.get(2).setStatusLoad(true);
                        updateUI();
                        item = new item(true,R.drawable.ic_message,"Messenger", messenger.backupMessages(), false);
                        listItem.set(2,item);
                        updateUI();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }else if(requestCode == 3 && resultCode == Activity.RESULT_OK ){
            listItem.get(3).setStatusLoad(true);
            updateUI();
            String s = "Selected : " +getImage.getLeng() + " item - "+ getImage.getSize()+" MB";
            item = new item(true,R.drawable.ic_image,"Gallery", s, false);
            listItem.set(3,item);
            updateUI();
        }else if(requestCode == 4 && resultCode == Activity.RESULT_OK ){
            listItem.get(4).setStatusLoad(true);
            updateUI();
            item = new item(true,R.drawable.ic_video,"Video", video.getleng(), false);
            listItem.set(4,item);
            updateUI();
        }else if(requestCode == 5 && resultCode == Activity.RESULT_OK ){
            listItem.get(5).setStatusLoad(true);
            updateUI();
            item = new item(true,R.drawable.ic_apps,"App", app.getInfo(), false);
            listItem.set(5,item);
            updateUI();
        }else if(requestCode == 6 && resultCode == Activity.RESULT_OK ){
            listItem.get(6).setStatusLoad(true);
            updateUI();
            item = new item(true,R.drawable.ic_audio,"Audio",  audio.getSize(), false);
            listItem.set(6,item);
            updateUI();
        }else if(requestCode == 7 && resultCode == Activity.RESULT_OK ) {
            listItem.get(7).setStatusLoad(true);
            updateUI();
            item = new item(true, R.drawable.ic_file, "File", file.getSize(), false);
            listItem.set(7, item);
            updateUI();
        }
    }
    void updateUI(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                client.adapter.notifyDataSetChanged();
            }
        });
    }
}


