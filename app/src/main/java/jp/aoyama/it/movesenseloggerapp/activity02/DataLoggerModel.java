package jp.aoyama.it.movesenseloggerapp.activity02;

import android.util.Log;

import androidx.annotation.NonNull;

import jp.aoyama.it.movesenseloggerapp.activity01.MovesenseModel;

public class DataLoggerModel extends MovesenseModel {

    private String sensorPosition;


    public DataLoggerModel(String serial, String macAddress) {
        super(serial, macAddress);
    }

    @Override
    public String getMacAddress() {
        Log.d("DataLoggerActivity",super.getMacAddress());
        return super.getMacAddress();
    }

    @Override
    public String getSerial() {
        Log.d("DataLoggerActivity",super.getMacAddress());
        return super.getSerial();
    }

    public void setSensorPosition(String sensorPosition){
        this.sensorPosition = sensorPosition;
    }

    public String getSensorPosition(){
        return this.sensorPosition;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Serial:"+this.getSerial()+"\n");
        sb.append("MacAddress:"+this.getMacAddress()+"\n");
        sb.append("SensorPosition:"+this.sensorPosition+"\n");

        return sb.toString();
    }
}
