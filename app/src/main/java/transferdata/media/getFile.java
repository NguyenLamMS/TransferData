package transferdata.media;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import transferdata.adapter.item;
import transferdata.home.R;
import transferdata.transfer.client;

public class getFile {
    Activity context;
    public static List<item> listFile = new ArrayList<>();
    public getFile(Activity context){
        this.context = context;
    }
    public void getAllFile(){
        listFile.clear();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        List<String> extensions = new ArrayList<>();
        extensions.add("pdf");
        extensions.add("csv");
        extensions.add("doc");
        extensions.add("docx");
        extensions.add("xls");
        extensions.add("xlsx");
        extensions.add("zip");
        extensions.add("ppt");
        extensions.add("pptm");
        extensions.add("pptx");

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
        Cursor cursor =  contentResolver.query(uri, null, selection, null, null );
        if(cursor != null){
          int pathColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
          int sizeColumnIndex, titleColumnIndex, nameColumnIndex;
          String filePath, fileDisplayName, fileTitle;
          int fileSize;
            while (cursor.moveToNext()){
                filePath = cursor.getString(pathColumnIndex);
                String extension = getExtensionByPath(filePath);
                if(extensions.contains(extension)){
                    sizeColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE);
                    titleColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.TITLE);
                    nameColumnIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME);
                    fileSize = cursor.getInt(sizeColumnIndex);
                    fileDisplayName = cursor.getString(nameColumnIndex);
                    fileTitle = cursor.getString(titleColumnIndex);
                    item item = new item();
                    item.setName(fileTitle);
                    item.setChecked(true);
                    item.setSize(fileSize);
                    item.setSource(filePath);
                    if(extension.contains("doc") || extension.contains("docx")){
                        item.setImg_resource(R.drawable.ic_word);
                    }else if(extension.contains("csv") || extension.contains("xls") || extension.contains("xlsx")){
                        item.setImg_resource(R.drawable.ic_excel);
                    }else if(extension.contains("ppt") || extension.contains("pptm") || extension.contains("pptx")){
                        item.setImg_resource(R.drawable.ic_powerpoint);
                    }else if(extension.contains("zip")){
                        item.setImg_resource(R.drawable.ic_zip);
                    }else if(extension.contains("pdf")){
                        item.setImg_resource(R.drawable.ic_pdf);
                    }
                    listFile.add(item);
                }
            }
        }
         cursor.close();
    }
    public static String getExtensionByPath(@NonNull String path) {
        String result = "%20";
        int i = path.lastIndexOf('.');
        if (i > 0) {
            result = path.substring(i + 1);
        }
        return result;
    }
    public String getSize(){
        int count = 0;
        int size = 0;
        int sizeRound = 0;
        for(item it : listFile){
            if(it.isChecked()){
                size += it.getSize();
                sizeRound += it.getSize() * 0.000001;
                count++;
            }
        }
        client.SIZE_ALL_ITEM[7] = sizeRound;
        return "Selected : " + count + " item - "+ String.format("%.02f", size*0.000001) +" MB";
    }

}
