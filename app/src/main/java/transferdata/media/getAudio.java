package transferdata.media;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.List;

import transferdata.adapter.item;
import transferdata.home.R;
import transferdata.transfer.client;

public class getAudio {
    Activity context;
    public static List<item> listAudio = new ArrayList<>();
    public getAudio(Activity context){
        this.context = context;
    }
    public void getAllAudio(){
        listAudio.clear();
        String[] projection = { MediaStore.Audio.AudioColumns.DATA ,MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.SIZE};
        String name, path;
        int size;
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
        while (cursor.moveToNext()){
            name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
            path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
            size = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
            item item = new item();
            item.setName(name);
            item.setChecked(true);
            item.setSize(size);
            item.setSource(path);
            item.setImg_resource(R.drawable.ic_item_audio);
            listAudio.add(item);
        }
        cursor.close();
    }
    public String getSize(){
        int count = 0;
        int size = 0;
        int sizeRound = 0;
        for(item it : listAudio){
            if(it.isChecked()){
                size += it.getSize();
                sizeRound += it.getSize() * 0.000001;
                count++;
            }
        }
        client.SIZE_ALL_ITEM[6] = sizeRound;
        return "Selected : " + count + " item - "+ String.format("%.02f", size*0.000001) +" MB";
    }
}
