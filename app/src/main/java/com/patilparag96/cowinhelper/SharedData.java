package com.patilparag96.cowinhelper;

import android.os.Parcel;
import android.os.Parcelable;

public class SharedData implements Parcelable {
    String state;
    String district;
    String vaccine;

    public SharedData(String state, String district, String vaccine) {
        this.state = state;
        this.district = district;
        this.vaccine = vaccine;
    }

    protected SharedData(Parcel in) {
        state = in.readString();
        district = in.readString();
        vaccine = in.readString();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public SharedData createFromParcel(Parcel in) {
            return new SharedData(in);
        }

        @Override
        public SharedData[] newArray(int size) {
            return new SharedData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(state);
        parcel.writeString(district);
        parcel.writeString(vaccine);
    }
}
