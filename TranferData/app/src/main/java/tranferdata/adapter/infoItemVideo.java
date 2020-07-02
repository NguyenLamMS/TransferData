package tranferdata.adapter;

import android.os.Parcel;
import android.os.Parcelable;

public class infoItemVideo implements Parcelable {
    String source, thumbnails;
    boolean select;
    int size;
    public infoItemVideo(String source, String thumbnails, boolean select, int size) {
        this.source = source;
        this.thumbnails = thumbnails;
        this.select = select;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public static Creator<infoItemVideo> getCREATOR() {
        return CREATOR;
    }

    protected infoItemVideo(Parcel in) {
        source = in.readString();
        thumbnails = in.readString();
        select = in.readByte() != 0;
    }

    public static final Creator<infoItemVideo> CREATOR = new Creator<infoItemVideo>() {
        @Override
        public infoItemVideo createFromParcel(Parcel in) {
            return new infoItemVideo(in);
        }

        @Override
        public infoItemVideo[] newArray(int size) {
            return new infoItemVideo[size];
        }
    };

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(source);
        dest.writeString(thumbnails);
        dest.writeByte((byte) (select ? 1 : 0));
    }
}
