package tranferdata.adapter;

import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import tranferdata.home.R;
import tranferdata.media.video;

public class adapterVideo extends BaseAdapter {
    List<itemVideo> listVideo;
    int resource;
    video context;
    Boolean folder;
    public  adapterVideo(video context, int resource, List<itemVideo> list, Boolean folder){
        this.context = context;
        this.resource = resource;
        this.listVideo = list;
        this.folder = folder;
    }
    @Override
    public int getCount() {
        if(!listVideo.isEmpty()){
            if(folder){
                return listVideo.size();
            }else{
                return listVideo.get(0).listVideo.size();
            }
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
        LayoutInflater inflater = context.getLayoutInflater();
        View row = inflater.inflate(this.resource, null);
        ImageView img = row.findViewById(R.id.img_item_grid_view);
        TextView total_image = row.findViewById(R.id.total_image);
        TextView folder_name = row.findViewById(R.id.folder_name);
        CheckBox checkBox = row.findViewById(R.id.cb_select_image);
        ImageView play_video = row.findViewById(R.id.play_video);
        play_video.setVisibility(View.VISIBLE);
        //get width screen
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        img.setMinimumHeight(width / 3);
        img.setMinimumWidth(width / 3);
        if(folder){
            int total = listVideo.get(position).listVideo.size();
            String name = listVideo.get(position).folderName;
            total_image.setText(Integer.toString(total));
            folder_name.setText(name);
            row.setPadding(1,0,1,30);

            checkBox.setChecked(listVideo.get(position).select);
            clickCheckBoxFolder(checkBox, position);
            Glide
                .with(context)
                .load(listVideo.get(position).listVideo.get(0).thumbnails)
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.color.cardview_light_background)
                .into(img);
        }else{
            clickCheckBoxVideo(checkBox, position);
            checkBox.setChecked(listVideo.get(0).listVideo.get(position).select);
            Glide
                .with(context)
                .load(listVideo.get(0).listVideo.get(position).thumbnails)
                .centerCrop()
                .thumbnail(0.1f)
                .placeholder(R.color.cardview_light_background)
                .into(img);
        }

        return row;
    }
    void clickCheckBoxFolder(final CheckBox checkBox, final int pos){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    listVideo.get(pos).select = true;
                    checkVideo(true,pos);
                }else{
                    listVideo.get(pos).select = false;
                    checkVideo(false, pos);
                }
            }
        });
    }
    void clickCheckBoxVideo(final CheckBox checkBox, final int pos){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    listVideo.get(0).listVideo.get(pos).select = true;
                }else{
                    listVideo.get(0).listVideo.get(pos).select = false;
                }
                listVideo.get(0).select = checkFolder(0);
                context.adapter.notifyDataSetChanged();
            }
        });
    }
    void checkVideo(Boolean check, int pos){
        for(infoItemVideo video : listVideo.get(pos).listVideo){
            video.select = check;
        }
        context.adapterVideo.notifyDataSetChanged();
    }
    boolean checkFolder(int pos){
        for(infoItemVideo video : listVideo.get(pos).listVideo){
            if(video.select){
                return true;
            }
        }
        return false;
    }
}
