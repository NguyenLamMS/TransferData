package tranferdata.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tranferdata.callback.calbackDetail;
import tranferdata.home.R;

public class adapterDetail extends BaseAdapter {
    List<item> list;
    Activity context;
    int resource;
    calbackDetail detail;
    public adapterDetail(Activity context, int resource, List<item>list, calbackDetail detail){
        this.list = list;
        this.context = context;
        this.resource = resource;
        this.detail = detail;
    }
    @Override
    public int getCount() {
        if(!list.isEmpty()){
            return list.size();
        }
        return 0;
    }
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(this.resource, null);
        final CheckBox cb_detail = row.findViewById(R.id.detail_check);
        TextView txt_name = row.findViewById(R.id.detail_name);
        ImageView img_detail = row.findViewById(R.id.detail_icon);
        txt_name.setText(list.get(position).name);
        cb_detail.setChecked(list.get(position).isChecked());
        img_detail.setImageResource(list.get(position).img_resource);
        if(context.getClass().getSimpleName().contains("message")){
            img_detail.setVisibility(View.GONE);
        }
        if(context.getClass().getSimpleName().contains("app")){
            img_detail.setImageDrawable(list.get(position).imgDrawable);
        }

        clickCheckBox(cb_detail, position);
        return row;
    }
    void clickCheckBox(final CheckBox checkBox, final int pos){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    list.get(pos).setChecked(true);
                }else{
                    list.get(pos).setChecked(false);
                }
                if(setCheckAll()){
                    detail.statusCheck(true);
                }else{
                    detail.statusCheck(false);
                }
            }
        });
    }
    boolean setCheckAll(){
        for(int i = 0; i< list.size() ;i++){
            if(!list.get(i).isChecked()){
                return false;
            }
        }
        return true;
    }
}
