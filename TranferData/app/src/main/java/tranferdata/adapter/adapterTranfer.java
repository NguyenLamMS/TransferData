package tranferdata.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;
import tranferdata.home.R;

public class adapterTranfer extends BaseAdapter {
    Activity contex;
    int resource;
    List<item> listItem;
    GifImageView gifLoad;
    public adapterTranfer(Activity contex, int resource, List<item>listItem){
        this.listItem = listItem;
        this.resource = resource;
        this.contex = contex;
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
        LayoutInflater inflater = this.contex.getLayoutInflater();
        View row = inflater.inflate(this.resource, null);
        TextView txt_item = row.findViewById(R.id.txt_item_tranfer);
        txt_item.setText(listItem.get(position).name);
        gifLoad = row.findViewById(R.id.wait_tranfer);
        if (!listItem.get(position).statusLoad) {
            gifLoad.setImageResource(R.drawable.ic_done);
        }
        return row;
    }
}
