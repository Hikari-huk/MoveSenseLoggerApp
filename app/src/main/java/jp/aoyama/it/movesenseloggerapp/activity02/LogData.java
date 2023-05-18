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

        for(Acc a: meas.acc){
            sb.append("Acc Timestamp: "+a.timestamp);
            for(Data data: a.arrayAcc) {
                sb.append(", x:" + data.x + ", y:" + data.y + ", z:" + data.z+"\n");
            }
        }
        sb.append("\n");
        for(Gyro g: meas.gyro){
            sb.append("Gyro Timestamp: "+g.timestamp);
            for(Data data: g.arrayGyro) {
                sb.append(", x:" + data.x + ", y:" + data.y + ", z:" + data.z+"\n");
            }
        }


        return sb.toString();
    }
}
