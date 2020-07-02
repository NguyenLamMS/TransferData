package tranferdata.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class itemVideo implements Parcelable {
    String folderName;
    boolean select;
    List<infoItemVideo> listVideo;

    public itemVideo(String folderName, boolean select, List<infoItemVideo> listVideo) {
        this.folderName = folderName;
        this.select = select;
        this.listVideo = listVideo;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public static Creator<itemVideo> getCREATOR() {
        return CREATOR;
    }

    protected itemVideo(Parcel in) {
        folderName = in.readString();
        listVideo = in.createTypedArrayList(infoItemVideo.CREATOR);
    }

    public static final Creator<itemVideo> CREATOR = new Creator<itemVideo>() {
        @Override
        public itemVideo createFromParcel(Parcel in) {
            return new itemVideo(in);
        }

        @Override
        public itemVideo[] newArray(int size) {
            return new itemVideo[size];
        }
    };

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public List<infoItemVideo> getListVideo() {
        return listVideo;
    }

    public void setListVideo(List<infoItemVideo> listVideo) {
        this.listVideo = listVideo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(folderName);
        dest.writeTypedList(listVideo);
    }
}
