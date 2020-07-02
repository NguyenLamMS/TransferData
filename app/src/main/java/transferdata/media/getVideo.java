package transferdata.media;

import android.app.Activity;
import android.database.Cursor;
import android.provider.MediaStore;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import transferdata.adapter.infoItemVideo;
import transferdata.adapter.itemVideo;
import transferdata.transfer.client;

public class getVideo {
    Activity context;
    public static List<itemVideo> listVideo = new ArrayList<>();
    List<String>folder = new ArrayList<>();
    public getVideo(Activity context){
        this.context = context;
    }
    public void getVideoPath(){
        Uri uri;
        Cursor cursor;
        folder.clear();
        listVideo.clear();
        int column_index_data, column_index_folder_name,column_id,thum, size;
        String source, thumbnails;
        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Thumbnails.DATA};

        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        cursor = context.getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);
        String folderName = null;
        while (cursor.moveToNext()) {
            folderName = cursor.getString(column_index_folder_name);
            System.out.println("++++++++++" + folderName);
            if(folderName == null){
               folderName = "home";
            }
            if(!folder.contains(folderName)){
                folder.add(folderName);
            }

//            Log.e("video Column", cursor.getString(column_index_data));
//            Log.e("video Folder", cursor.getString(column_index_folder_name));
//            Log.e("video column_id", cursor.getString(column_id));
//            Log.e("video thum", cursor.getString(thum));
        }
        for(String f : folder){
            cursor = context.getApplicationContext().getContentResolver().query(uri, projection, null, null, orderBy + " DESC");
            List<infoItemVideo> path = new ArrayList<>();
            while (cursor.moveToNext()){
                folderName = cursor.getString(column_index_folder_name);

                if(f.equals(folderName) || (f.equals("home") && folderName == null)){
                    source = cursor.getString(column_index_data);
                    thumbnails = cursor.getString(thum);
                    size = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    path.add(new infoItemVideo(source,thumbnails,true,size));
                }
            }
            itemVideo video = new itemVideo(f,true,path);
            listVideo.add(video);
        }
    }
    public String getleng(){
        int count = 0;
        int size = 0;
        int sizeRound = 0;
        for(itemVideo item : listVideo){
            for(infoItemVideo video : item.getListVideo()){
                if(video.isSelect()){
                    count++;
                    size += video.getSize();
                    sizeRound += video.getSize() * 0.000001;
                }
            }
        }
        client.SIZE_ALL_ITEM[4] = sizeRound;
        return "Selected : " + count + " item - "+ String.format("%.02f", size*0.000001) +" MB";
    }
}
