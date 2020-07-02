package transferdata.transfer;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import transferdata.adapter.*;
import transferdata.adapter.item;
import transferdata.connect.connect;
import transferdata.home.R;
import transferdata.media.getAudio;
import transferdata.media.getDataImage;
import transferdata.media.getFile;
import transferdata.media.getVideo;
import transferdata.service.getApplication;
import transferdata.socket.clientSocket;
public class transferData extends Activity {
    public static ListView listView_tranfer;
    public static Button btn_done;
    clientSocket socket;
    public static List<item>listItem;
    String Address;
    item item;
    public static adapterTranfer adapter;
    public static LinkedList<Integer> sizeItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_data);
        btn_done = findViewById(R.id.tranfer_done);
        Intent intent = this.getIntent();
        Address = intent.getStringExtra("address");
        listItem = new ArrayList<>();
        sizeItem = new LinkedList<>();
        item = new item(true,R.drawable.ic_contact, "Contact", "info", true);
        listItem.add(item);
        item = new item(true,R.drawable.call_log,"Call log", "info", true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_message,"Messenger", "info", true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_image,"Photo", "info", true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_video,"Video", "info", true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_apps,"App",  "info", true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_audio,"Audio",  "info", true);
        listItem.add(item);
        item = new item(true,R.drawable.ic_file,"File",  "info", true);
        listItem.add(item);


        for(int i = client.listItem.size() - 1; i >= 0; i--){
           if(!client.listItem.get(i).isChecked()){
               listItem.remove(i);
           }else{
               sizeItem.addFirst(client.SIZE_ALL_ITEM[i]);
           }
        }
        adapter = new adapterTranfer(this, R.layout.list_item_transfer, listItem, sizeItem);
        listView_tranfer = findViewById(R.id.list_tranfer);
        listView_tranfer.setAdapter(adapter);
        tranferAllData();
        clickButtonDone();
    }
    // start tranfer data
    void tranferAllData(){
        getDataImage getImage = new getDataImage();
        List<String> data = new ArrayList<>();

        String path;
        //tranfer contact
        if(client.listItem.get(0).isChecked()){
            path = getExternalFilesDir(null) + "/contacts/contacts.csv";
            data.add(path);
        }
        // tranfer call log
        if(client.listItem.get(1).isChecked()){
            path = getExternalFilesDir(null) + "/callogs/callogs.xml";
            data.add(path);
        }
        // tranfer messerger
        if(client.listItem.get(2).isChecked()){
            path = getExternalFilesDir(null) + "/messages/messages.xml";
            data.add(path);
        }

        // tranfer image;
        if(client.listItem.get(3).isChecked()){
            for(itemImage item : getImage.listImage){
                for(infoImage info : item.getListPathImage()){
                    if(info.isSelect()){
                        data.add(info.getSource());
                    }
                }
            }
        }
        // tranfer video
        if(client.listItem.get(4).isChecked()){
            for(itemVideo video : getVideo.listVideo){
                for(infoItemVideo info : video.getListVideo()){
                    if(info.isSelect()){
                        data.add(info.getSource());
                    }
                }
            }
        }
        // tranfer app
        if(client.listItem.get(5).isChecked()){
            for(item it : getApplication.listItem){
                if(it.isChecked()){
                    data.add(it.getSource());
                }
            }
        }
        // tranfer audio
        if(client.listItem.get(6).isChecked()){
            for(item it : getAudio.listAudio){
                if(it.isChecked()){
                    data.add(it.getSource());
                }
            }
        }
        // tranfer file
        if(client.listItem.get(7).isChecked()){
            for(item it : getFile.listFile){
                if(it.isChecked()){
                    data.add(it.getSource());
                }
            }
        }
        socket = new clientSocket(Address, transferData.this, data);
        socket.start();

    }
    // caculator speed tranfer file
    public static void speedTranfer() {
        System.out.println("SIZE " + clientSocket.size + " " + clientSocket.SIZE_ALL_FILE);
        int size_tranfer = 0;
        for(int i = 0 ;i < sizeItem.size() ; i++) {
            size_tranfer += sizeItem.get(i).intValue();
            System.out.println("SIZE " + clientSocket.size + " " + size_tranfer );
            if (clientSocket.size >= size_tranfer) {
                if (listItem.get(i).isStatusLoad()) {
                    listItem.get(i).setStatusLoad(false);
                    listView_tranfer.post(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
            if(clientSocket.size >= clientSocket.SIZE_ALL_FILE){
                btn_done.post(new Runnable() {
                    @Override
                    public void run() {
                        btn_done.setVisibility(View.VISIBLE);
                        listView_tranfer.setVisibility(View.GONE);
                    }
                });
            }
        }
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
    public void onBackPressed() {
        connect.isConnect(transferData.this);
    }
}
