package tranferdata.media;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import tranferdata.adapter.adapterDetail;
import tranferdata.adapter.item;
import tranferdata.callback.calbackDetail;
import tranferdata.home.R;
import tranferdata.service.getContact;
import tranferdata.tranfer.client;

public class app extends Activity implements View.OnClickListener, calbackDetail {
    private TextView btnSave, btnCancel;
    private ListView lvListItem;
    public static ArrayList<item> listItem = new ArrayList<>();
    public static adapterDetail adapterdetail;
    calbackDetail detail;
    CheckBox checkAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        mapping();
        addItem();
    }

    public void mapping() {
        lvListItem = findViewById(R.id.list_detail);
        btnSave =  findViewById(R.id.detail_save);
        btnCancel = findViewById(R.id.detail_cancel);
        checkAll = findViewById(R.id.detail_checl_all);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    Boolean setCheckAll(){
        for(int i = 0; i< getContact.listItem.size(); i++){
            if(!getContact.listItem.get(i).isChecked()){
                return false;
            }
        }
        return true;
    }
    public void addItem() {
        checkAll.setChecked(setCheckAll());
        detail = new calbackDetail() {
            @Override
            public void statusCheck(boolean check) {
                checkAll.setChecked(check);
            }
        };
        adapterdetail = new adapterDetail(this,R.layout.list_item_detail,listItem,detail);
        lvListItem.setAdapter(adapterdetail);
        checkAll(checkAll, adapterdetail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_save: {
                Intent intent = new Intent(this,client.class);
                intent.putExtra("typeApp", "1");
                startActivity(intent);
                finish();
                break;
            }
            case R.id.detail_cancel: {
                onBackPressed();
                break;
            }
        }
    }

    public static String getInfo() {
        int dem = 0;
        long s = 0;
        for (item i: app.listItem) {
            if(i.isChecked()) {
                dem++;
                s += i.getSize();
            }
        }
        float size = (float) s / 1024;
        size = (float) (size * 0.001);
        client.SIZE_ALL_ITEM[5] = (int) size;
        String result = "Selected : " + dem + " item "+ " - " + String.format("%.03f", size) + " MB";
        return result;
    }

    @Override
    public void statusCheck(boolean check) {

    }
    void checkAll(final CheckBox checkBox, final adapterDetail adapter){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    for(int i = 0;i< listItem.size();i++){
                        listItem.get(i).setChecked(true);
                    }
                }else{
                    for(int i = 0;i<  listItem.size();i++){
                        listItem.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
