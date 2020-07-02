package transferdata.adapter;
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
import transferdata.home.R;
import transferdata.media.image;


public class adapterImage extends BaseAdapter {
    List<itemImage> listImage;
    int resource;
    image context;
    Boolean folder;
    public  adapterImage(image context, int resource, List<itemImage> list, Boolean folder){
        this.context = context;
        this.resource = resource;
        this.listImage = list;
        this.folder = folder;
    }
    @Override
    public int getCount() {
        if(!listImage.isEmpty()){
            if(folder){
                return listImage.size();
            }else{
                return listImage.get(0).listPathImage.size();
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
        //get width screen
        Display display = context.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        img.setMinimumHeight(width / 3);
        img.setMinimumWidth(width / 3);
        if(folder){
            int total = listImage.get(position).listPathImage.size();
            String name = listImage.get(position).folderName;
            total_image.setText(Integer.toString(total));
            folder_name.setText(name);
            row.setPadding(1,0,1,30);
            checkBox.setChecked(listImage.get(position).select);
            clickCheckBoxFolder(checkBox, position);
            Glide
            .with(context)
            .load(listImage.get(position).listPathImage.get(0).source)
            .centerCrop()
            .thumbnail(0.1f)
            .placeholder(R.color.cardview_light_background)
            .into(img);
        }else{
            clickCheckBoxImage(checkBox, position);
            checkBox.setChecked(listImage.get(0).listPathImage.get(position).select);
            Glide
            .with(context)
            .load(listImage.get(0).listPathImage.get(position).source)
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
                    listImage.get(pos).select = true;
                    checkImage(true,pos);
                }else{
                    listImage.get(pos).select = false;
                    checkImage(false, pos);
                }
            }
        });
    }
    void clickCheckBoxImage(final CheckBox checkBox, final int pos){
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkBox.isChecked()){
                    listImage.get(0).listPathImage.get(pos).select = true;
                }else{
                    listImage.get(0).listPathImage.get(pos).select = false;
                }
                listImage.get(0).select = checkFolder(0);
                context.adapter.notifyDataSetChanged();
            }
        });
    }
    void checkImage(Boolean check, int pos){
        for(infoImage infoImage : listImage.get(pos).listPathImage){
            infoImage.select = check;
        }
        context.adapterImage.notifyDataSetChanged();
    }
    boolean checkFolder(int pos){
        for(infoImage infoImage : listImage.get(pos).listPathImage){
            if(infoImage.select){
                return true;
            }
        }
        return false;
    }
}
