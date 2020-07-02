package transferdata.media;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.List;
import transferdata.adapter.adapterDetail;
import transferdata.adapter.item;
import transferdata.callback.calbackDetail;
import transferdata.home.R;
import transferdata.service.getMessenger;

public class message extends Activity implements View.OnClickListener, calbackDetail {
    private ListView lvListItem;
    private TextView btnSave, btnCancel;
    public static List<item> itemMain;
    public static adapterDetail adapterDetail;
    calbackDetail detail;
    CheckBox checkAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        TextView title = findViewById(R.id.detail_title);
        title.setText("Choose messenger");
        mapping();
        addItem();
    }

    public void mapping() {
        lvListItem = findViewById(R.id.list_detail);
        btnSave = findViewById(R.id.detail_save);
        btnCancel = findViewById(R.id.detail_cancel);
        checkAll = findViewById(R.id.detail_checl_all);
        btnSave.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }
    Boolean setCheckAll(){
        for(int i = 0;i<getMessenger.listItem.size();i++){
            if(!getMessenger.listItem.get(i).isChecked()){
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
        adapterDetail = new adapterDetail(message.this, R.layout.list_item_detail, getMessenger.listItem, detail);
        lvListItem.setAdapter(adapterDetail);
        checkAll(checkAll, adapterDetail);
    }
    boolean isEmtyCheck(){
        for(item it : getMessenger.listItem){
            if(it.isChecked()){
                return true;
            }
        }
        return false;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_save: {
                try {
                    if(isEmtyCheck()){
                        getMessenger.backupMessages();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }else{
                        Toast.makeText(message.this, "You must select at least 1 item", Toast.LENGTH_SHORT).show();
                    }
                    break;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            case R.id.detail_cancel: {
                onBackPressed();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        getMessenger.listItem = getIntent().getParcelableArrayListExtra("listMessenger");
        super.onBackPressed();
    }

    void checkAll(final CheckBox checkBox, final adapterDetail adapter){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    for(int i = 0;i< getMessenger.listItem.size();i++){
                        getMessenger.listItem.get(i).setChecked(true);
                    }
                }else{
                    for(int i = 0;i< getMessenger.listItem.size();i++){
                        getMessenger.listItem.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void statusCheck(boolean check) {

    }
}
