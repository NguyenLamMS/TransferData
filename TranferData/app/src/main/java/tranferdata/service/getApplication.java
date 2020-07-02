package tranferdata.service;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import tranferdata.adapter.item;
import tranferdata.media.app;
import tranferdata.tranfer.client;

public class getApplication {
    Activity context;
    public getApplication(Activity context){
        this.context = context;
    }
    public String backupApps() {
        int countApp = 0;
        long sizeApp = 0;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List pkgAppList = context.getPackageManager().queryIntentActivities(mainIntent, 0);
        File file = new File(context.getExternalFilesDir(null), "apps");
        if (!file.exists()) {
            file.mkdir();
        }
        app.listItem.clear();
        for (Object object : pkgAppList) {
            ResolveInfo info = (ResolveInfo) object;

            if (info.activityInfo.applicationInfo.publicSourceDir.indexOf("/system/") < 0) {
                try {
                    countApp++;
                    File f1 = new File(info.activityInfo.applicationInfo.publicSourceDir);
                    String fileName = info.loadLabel(context.getPackageManager()).toString();

                    File fileApp = new File(file, fileName + ".apk");
                    if (!fileApp.exists()){
                        fileApp.createNewFile();
                        InputStream inputStream = new FileInputStream(f1);
                        OutputStream outputStream = new FileOutputStream(fileApp);
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buf)) > 0) {
                            outputStream.write(buf, 0, len);
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                    // xu ly item ben app class
                    Drawable icon = info.loadIcon(context.getPackageManager());
                    item i = new item(true,-1,fileName,"",false);
                    i.setImgDrawable(icon);
                    i.setSize((int) fileApp.length());

                    app.listItem.add(i);
                    if(app.adapterdetail != null){
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                app.adapterdetail.notifyDataSetChanged();
                            }
                        });
                    }
                    sizeApp += fileApp.length();
//                    if (fileApp.exists()) {
//                        InputStream inputStream = new FileInputStream(f1);
//                        OutputStream outputStream = new FileOutputStream(fileApp);
//                        byte[] buf = new byte[1024];
//                        int len;
//                        while ((len = inputStream.read(buf)) > 0) {
//                            outputStream.write(buf, 0, len);
//                        }
//                        inputStream.close();
//                        outputStream.close();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // get size all application
        float size = (float) sizeApp / 1024;
        size = (float) (size * 0.001);
        client.SIZE_ALL_ITEM[5] = (int) size;
        String result = "Selected : " + countApp + " item "+ " - " + String.format("%.03f", size) + " MB";
        return String.valueOf(result);
    }
    public void restoreApps() {
        String path = context.getExternalFilesDir(null) + "/apps";
        File directory = new File(path);
        if (directory.exists()) {
            File[] files = directory.listFiles();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(files[files.length - 2]), "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
    }

}
