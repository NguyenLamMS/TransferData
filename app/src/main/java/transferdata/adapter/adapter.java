package transferdata.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import pl.droidsonroids.gif.GifImageView;
import transferdata.media.*;
import java.util.ArrayList;
import java.util.List;
import transferdata.home.R;
import transferdata.service.getApplication;
import transferdata.service.getContact;
import transferdata.service.getMessenger;

public class adapter extends BaseAdapter {
    Activity context;
    int resource;
    List<item> listItem;
    Button btn_start;
    TextView total_item;
    getDataImage getImage = new getDataImage();
    GifImageView iconWait;
    Intent intent;
    public adapter(Activity context, int resource, List<item> list){
        this.context = context;
        this.resource = resource;
        this.listItem = list;
    }
    @Override
    public int getCount() {
        if(!listItem.isEmpty()){
            return listItem.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = this.context.getLayoutInflater();
        View row = inflater.inflate(this.resource, null);
        TextView txt_item = row.findViewById(R.id.txt_item);
        ImageView img_item = row.findViewById(R.id.img_item);
        CheckBox cb_item = row.findViewById(R.id.cb_item);
        iconWait = row.findViewById(R.id.iconWaitGetData);
        ImageView img_show_more = row.findViewById(R.id.img_show_more);
        TextView txt_total_item_select = row.findViewById(R.id.total_item_select);
        total_item = context.findViewById(R.id.total_item);
        btn_start = context.findViewById(R.id.btn_start);
        txt_item.setText(listItem.get(position).name);
        img_item.setImageResource(listItem.get(position).img_resource);
        cb_item.setChecked(listItem.get(position).checked);
        txt_total_item_select.setText(listItem.get(position).info);

        if(listItem.get(position).statusLoad){
            iconWait.setVisibility(View.VISIBLE);
        }
        else{
            iconWait.setVisibility(View.GONE);
        }
        countCheckbox();
        clickCheckbox(cb_item, position);
        clickShowMore(img_show_more, position);
        return row;
    }
    void clickCheckbox(final CheckBox cb, final int pos){
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cb.isChecked()){
                    listItem.get(pos).checked = true;
                }else{
                    listItem.get(pos).checked = false;
                }
                countCheckbox();
            }
        });
    }
    void countCheckbox(){
        int count = 0;
        for(item item : listItem){
            if(item.checked){
                count++;
            }
        }
        if(count == 0){
            btn_start.setEnabled(false);
        }
        else btn_start.setEnabled(true);
        total_item.setText(Integer.toString(count));
    }
    void clickShowMore(final ImageView img, final int pos){
        Intent intent;
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (pos){
                    case 0:
                        intent = new Intent(context, transferdata.media.contact.class);
                        intent.putParcelableArrayListExtra("listContact", getContact.listItem);
                        break;
                    case 1:
                        Toast.makeText(context, "No action", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        intent = new Intent(context, transferdata.media.message.class);
                        intent.putParcelableArrayListExtra("listMessenger", getMessenger.listItem);
                        break;
                    case 3:
                        intent = new Intent(context, transferdata.media.image.class);
                        intent.putParcelableArrayListExtra("listImage", (ArrayList<? extends Parcelable>) getImage.listImage);
                        break;
                    case 4:
                        intent = new Intent(context, transferdata.media.video.class);
                        intent.putParcelableArrayListExtra("listVideo", (ArrayList<? extends Parcelable>) getVideo.listVideo);
                        break;
                    case 5:
                        intent = new Intent(context, transferdata.media.app.class);
                        intent.putParcelableArrayListExtra("listApp", getApplication.listItem);
                        break;
                    case 6:
                        intent = new Intent(context, audio.class);
                        intent.putParcelableArrayListExtra("listAudio", (ArrayList<? extends Parcelable>) getAudio.listAudio);
                        break;
                    case 7:
                        intent = new Intent(context, file.class);
                        intent.putParcelableArrayListExtra("listFile", (ArrayList<? extends Parcelable>) getFile.listFile);
                        break;
                    default:
                        intent = new Intent();
                }
                if(pos != 1)
                    context.startActivityForResult(intent,pos);
            }
        });
    }
}
