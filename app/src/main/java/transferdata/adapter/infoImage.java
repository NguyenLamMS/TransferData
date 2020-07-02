package transferdata.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class infoImage implements Parcelable {
    boolean select;
    int size;
    String name;
    String source;
    public infoImage(boolean select, int size, String name, String source) {
        this.select = select;
        this.size = size;
        this.name = name;
        this.source = source;
    }

    protected infoImage(Parcel in) {
        select = in.readByte() != 0;
        size = in.readInt();
        name = in.readString();
        source = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (select ? 1 : 0));
        dest.writeInt(size);
        dest.writeString(name);
        dest.writeString(source);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<infoImage> CREATOR = new Creator<infoImage>() {
        @Override
        public infoImage createFromParcel(Parcel in) {
            return new infoImage(in);
        }

        @Override
        public infoImage[] newArray(int size) {
            return new infoImage[size];
        }
    };

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
