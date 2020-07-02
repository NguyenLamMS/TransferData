package transferdata.media;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import transferdata.adapter.adapterDetail;
import transferdata.adapter.item;
import transferdata.callback.calbackDetail;
import transferdata.home.R;
import transferdata.service.getApplication;
import transferdata.transfer.client;

public class app extends Activity implements View.OnClickListener, calbackDetail {
    private TextView btnSave, btnCancel;
    private ListView lvListItem;
    public static adapterDetail adapterdetail;
    calbackDetail detail;
    CheckBox checkAll;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        TextView title = findViewById(R.id.detail_title);
        title.setText("Choose app");
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
        for(int i = 0; i< getApplication.listItem.size(); i++){
            if(!getApplication.listItem.get(i).isChecked()){
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
        adapterdetail = new adapterDetail(this,R.layout.list_item_detail, getApplication.listItem,detail);
        lvListItem.setAdapter(adapterdetail);
        checkAll(checkAll, adapterdetail);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.detail_save: {
                setResult(Activity.RESULT_OK);
                finish();
                break;
            }
            case R.id.detail_cancel: {
                onBackPressed();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        ArrayList<item>list = getIntent().getParcelableArrayListExtra("listApp");
       for(int i=0;i<list.size();i++){
           boolean check = list.get(i).isChecked();
           getApplication.listItem.get(i).setChecked(check);
       }
        super.onBackPressed();
    }

    public static String getInfo() {
        int dem = 0;
        long s = 0;
        for (item i: getApplication.listItem) {
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
                    for(int i = 0;i< getApplication.listItem.size();i++){
                        getApplication.listItem.get(i).setChecked(true);
                    }
                }else{
                    for(int i = 0;i< getApplication.listItem.size();i++){
                        getApplication.listItem.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
}
