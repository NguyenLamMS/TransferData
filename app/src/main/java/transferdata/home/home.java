package transferdata.home;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import transferdata.connect.connect;
import transferdata.transfer.client;

public class home extends AppCompatActivity {
    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.INSTALL_PACKAGES,
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        // allow changer system setting
//        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
//        startActivity(intent);
//
        checkAndRequestPermission();
        onclick_button();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static PublicKey getPublicKey(String s) throws Exception{
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(s));
        KeyFactory kf = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) kf.generatePublic(keySpecX509);
        return pubKey;
    }

    void onclick_button(){
        Intent intent = new Intent(home.this, connect.class);
        Button btn_new_phone = findViewById(R.id.btn_new_phone);
        Button btn_old_phone = findViewById(R.id.btn_old_phone);
        btn_old_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Tap old phone");
                Intent intent = new Intent(home.this, connect.class);
                intent.putExtra("NewPhone",false);
                startActivity(intent);
            }
        });

        btn_new_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Tap new phone ");
                Intent intent = new Intent(home.this, connect.class);
                intent.putExtra("NewPhone",true);
                startActivity(intent);
            }
        });
    }

    void checkAndRequestPermission(){
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
