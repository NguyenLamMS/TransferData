package tranferdata.media;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import tranferdata.adapter.infoImage;
import tranferdata.adapter.itemImage;
import tranferdata.tranfer.client;

public class getDataImage {
    public static List<itemImage>listImage = new ArrayList<>();
    public List<itemImage> getImagesPath(Activity activity) {
        Uri uri;
        List<String> listFolder = new ArrayList<>();
        List<itemImage> listItemImage = new ArrayList<>();
        Cursor cursor;
        String PathOfImage = null;
        int sizeImage;
        String nameImage;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        String folderName;
        // get all folder image
        while (cursor.moveToNext()) {
            folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
            if (!listFolder.contains(folderName)) {
                listFolder.add(folderName);
            }
        }
        // get image from folder
        for (String folder : listFolder) {
            cursor = activity.getContentResolver().query(uri, projection, null, null, null);
            List<infoImage> listPathImage = new ArrayList<>();
            while (cursor.moveToNext()) {
                folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                if (folder.equals(folderName)) {
                    PathOfImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    sizeImage = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                    nameImage = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    listPathImage.add(new infoImage(true, sizeImage, nameImage, PathOfImage));
                }
            }
            itemImage itemImage = new itemImage(folder, true, listPathImage);
            listItemImage.add(itemImage);
        }
        listImage = listItemImage;
        return listItemImage;
    }
    public String getSize(){
        int size = 0;
        int sizeRound = 0;
        for(itemImage item : listImage){
            for(infoImage info : item.getListPathImage()){
                if(info.isSelect()){
                    size += info.getSize();
                    sizeRound += info.getSize() * 0.000001;
                }
            }
        }
        client.SIZE_ALL_ITEM[3] = sizeRound;
        String s = String.format("%.02f", size*0.000001);
        return s;
    }
    public int getLeng(){
        int count = 0;
        for(itemImage item : listImage){
            for(infoImage info : item.getListPathImage()){
                if(info.isSelect()){
                    count++;
                }
            }
        }
        return count;
    }
}
