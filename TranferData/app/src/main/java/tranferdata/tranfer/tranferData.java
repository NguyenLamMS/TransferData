package tranferdata.tranfer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import androidx.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import tranferdata.adapter.*;
import tranferdata.adapter.item;
import tranferdata.home.R;
import tranferdata.media.getDataImage;
import tranferdata.media.getFile;
import tranferdata.media.getVideo;
import tranferdata.socket.clientSocket;
public class tranferData extends Activity {
    ListView listView_tranfer;
    clientSocket socket;
    public List<item>listItem;
    String Address;
    item item;
    public adapterTranfer adapter;
    public LinkedList<Integer> sizeItem;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tranfer_data);
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
        item = new item(true,R.drawable.ic_file,"File",  "info", true);
        listItem.add(item);


        for(int i = client.listItem.size() - 1; i >= 0; i--){
           if(!client.listItem.get(i).isChecked()){
               listItem.remove(i);
           }else{
               sizeItem.addFirst(client.SIZE_ALL_ITEM[i]);
           }
        }
        adapter = new adapterTranfer(this, R.layout.list_item_tranfer, listItem);
        listView_tranfer = findViewById(R.id.list_tranfer);
        listView_tranfer.setAdapter(adapter);
        tranferAllData();
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
            path = getExternalFilesDir(null) + "/apps";
            File file = new File(path);
            if (file.exists()) {
                File[] fileApp = file.listFiles();
                for (File f : fileApp){
                    data.add(f.getPath());
                }
            }
        }
        // tràner file
        if(client.listItem.get(6).isChecked()){
            for(item it : getFile.listFile){
                if(it.isChecked()){
                    data.add(it.getSource());
                }
            }
        }
        socket = new clientSocket(Address, tranferData.this, data);
        socket.start();
    }
    // caculator speed tranfer file
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
