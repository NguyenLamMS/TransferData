package tranferdata.tranfer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import tranferdata.home.R;
import tranferdata.service.getMessenger;
import tranferdata.socket.serverSocket;
public class server extends Activity{
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    Button btn_disconnect;
    TextView txtNameHost;
    serverSocket socket;
    Handler handler;
    Message msg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_conect);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(),null);
        btn_disconnect = findViewById(R.id.btn_disconnect);
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
        // start socket server
        handler = new Handler();
        msg = new Message();
        socket = new serverSocket(server.this);
        socket.execute();

        show_name_server();
    }
    @Override
    public void onBackPressed() {
        disconnect();
        // super.onBackPressed();
    }
    private void show_name_server(){
        manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                txtNameHost = findViewById(R.id.name_server);
                txtNameHost.setText(group.getOwner().deviceName);
            }
        });
    }
    public void disconnect(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you disconnect device ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(getApplicationContext(),"Disconnect device", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(int reason) {

                    }
                });
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}

