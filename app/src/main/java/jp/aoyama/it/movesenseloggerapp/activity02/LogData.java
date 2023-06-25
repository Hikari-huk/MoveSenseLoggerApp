package jp.aoyama.it.movesenseloggerapp.activity02;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LogData {

    @SerializedName("Meas")
    public Meas meas;
    public class Meas{

        @SerializedName("Acc")
        public List<Acc> acc;

        @SerializedName("Gyro")
        public List<Gyro> gyro;

        @SerializedName("HR")
        public List<Hr> hr;


    }
    public class Acc{
        @SerializedName("Timestamp")
        public long timestamp;
        @SerializedName("ArrayAcc")
        public List<Data> arrayAcc;


    }
    public class Gyro{
        @SerializedName("Timestamp")
        public long timestamp;
        @SerializedName("ArrayGyro")
        public List<Data> arrayGyro;
    }

    public class Hr{
        @SerializedName("average")
        public double average;

        @SerializedName("rrData")
        public List<Integer> rrData;
    }

    public class Data{
        @SerializedName("x")
        public double x;
        @SerializedName("y")
        public double y;
        @SerializedName("z")
        public double z;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        if(meas.acc != null){
            for(Acc a: meas.acc){
                sb.append("Acc Timestamp: "+a.timestamp);
                for(Data data: a.arrayAcc) {
                    sb.append(", x:" + data.x + ", y:" + data.y + ", z:" + data.z+"\n");
                }
            }

        }
        sb.append("\n");
        if(meas.gyro != null){

            for(Gyro g: meas.gyro){
                sb.append("Gyro Timestamp: "+g.timestamp);
                for(Data data: g.arrayGyro) {
                    sb.append(", x:" + data.x + ", y:" + data.y + ", z:" + data.z+"\n");
                }
            }


        }
        sb.append("\n");
        if(meas.hr != null){
            for(Hr h: meas.hr){
                sb.append("HR Average: "+h.average);
                for(int rr: h.rrData) {
                    sb.append(",rrData: "+String.valueOf(rr)+"\n");
                }
            }


        }

        return sb.toString();
    }
}
