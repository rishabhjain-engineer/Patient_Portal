package networkmngr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ashish on 12/9/2015.
 */
public class HugeDataPassing implements Parcelable {

    public String sendCouponArray;
    public  String priceArray;
    public  String sendData;
    public  String testDetailBookingArray;

    public HugeDataPassing(String priceArray, String sendCouponArray, String sendData, String testDetailBookingArray) {
        this.priceArray = priceArray;
        this.sendCouponArray = sendCouponArray;
        this.sendData = sendData;
        this.testDetailBookingArray = testDetailBookingArray;
    }




    protected HugeDataPassing(Parcel in) {
        this.priceArray=in.readString();
        this.sendCouponArray=in.readString();
        this.sendData=in.readString();
        this.testDetailBookingArray=in.readString();


    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
       // dest.writeSparseArray(hmaplist);
        dest.writeString(priceArray);
        dest.writeString(sendCouponArray);

        dest.writeString(sendData);
        dest.writeString(testDetailBookingArray);
        //dest.writeString(sendCouponArray);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HugeDataPassing> CREATOR = new Creator<HugeDataPassing>() {
        @Override
        public HugeDataPassing createFromParcel(Parcel in) {
            return new HugeDataPassing(in);
        }

        @Override
        public HugeDataPassing[] newArray(int size) {
            return new HugeDataPassing[size];
        }
    };
}
