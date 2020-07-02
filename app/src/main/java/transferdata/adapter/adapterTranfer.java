package transferdata.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import transferdata.home.R;

public class adapterTranfer extends BaseAdapter {
    Activity context;
    int resource;
    List<item> listItem;
    GifImageView gifLoad;
    LinkedList<Integer>size;
    public adapterTranfer(Activity contex, int resource, List<item>listItem, LinkedList<Integer>size){
        this.listItem = listItem;
        this.resource = resource;
        this.context = contex;
        this.size = size;
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
        TextView txt_item = row.findViewById(R.id.txt_item_tranfer);
        TextView txt_size = row.findViewById(R.id.tranfer_size_title);
        txt_item.setText(listItem.get(position).name);
        if(size.get(position) > 0){
            txt_size.setText(size.get(position) + " MB");
        }else{
            txt_size.setText("0.1 MB");
        }
        gifLoad = row.findViewById(R.id.wait_tranfer);
        if (!listItem.get(position).statusLoad) {
            gifLoad.setImageResource(R.drawable.ic_done);
        }
        return row;
    }
}
