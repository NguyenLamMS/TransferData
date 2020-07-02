package tranferdata.tranfer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import tranferdata.adapter.*;
import tranferdata.callback.calbackMessenger;
import tranferdata.home.R;
import tranferdata.media.app;
import tranferdata.media.getDataImage;
import tranferdata.media.getFile;
import tranferdata.media.getVideo;
import tranferdata.service.getApplication;
import tranferdata.service.getCallLog;
import tranferdata.service.getMessenger;
import tranferdata.service.getContact;
public class client extends Activity implements calbackMessenger {
    public static List<item> listItem;
    ListView listView_item;
    Button btn_start;
    Button btn_cancel;
    public adapter adapter;
    getContact contact;
    getMessenger messenger;
    getCallLog callLog;
    getApplication application;
    getFile file;
    item item;
    String Adress;
    getDataImage getImage = new getDataImage();
    getVideo video = new getVideo(client.this);
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
        item = new item(true,R.drawable.ic_file,"File",  info, true);
        listItem.add(item);
        adapter = new adapter(this, R.layout.list_view_item, listItem);
        listView_item = findViewById(R.id.list_item);
        listView_item.setAdapter(adapter);
        SIZE_ALL_ITEM = new int[listItem.size()];
        Intent intent = getIntent();
        Adress = intent.getStringExtra("address");
        click_button();

    // get all data
        getData();
    }
    void click_button(){
        btn_start = findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(client.this, tranferData.class);
                intent.putExtra("address", Adress);
                startActivity(intent);
            }
        });
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(client.this, receiveData.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {
      //  getData();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    void getData(){
        // get all contact
        Thread threadContact = new Thread(new Runnable() {
            @Override
            public void run() {
                contact = new getContact(client.this);
                item = new item(true,R.drawable.ic_contact, "Contact", contact.backupContacts(), false);
                listItem.set(0, item);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        threadCallLog.start();
        // get all messenger
        Thread threadMessenger = new Thread(new Runnable() {
            @Override
            public void run() {
                messenger = new getMessenger(client.this);
                item = new item(true,R.drawable.ic_message,"Messenger", messenger.backupMessages(), false);
                listItem.set(2,item);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        // get all file
        Thread threadFie = new Thread(new Runnable() {
            @Override
            public void run() {
                file = new getFile(client.this);
                file.getAllFile();
                item = new item(true,R.drawable.ic_file,"File",  file.getSize(), false);
                listItem.set(6,item);
            }
        });
        threadFie.start();
        threadApplication.start();
    }

    @Override
    public void calbackMessenger(ArrayList<String> List) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("++++++++++++++++++++ on result");
    }
}


