package jp.aoyama.it.movesenseloggerapp.activity01;

import androidx.annotation.NonNull;

public class MovesenseModel {
    private final String serial;
    private final String macAddress;
    public boolean isConnect = false;

    public MovesenseModel(String serial, String macAddress){
        this.serial = serial;
        this.macAddress = macAddress;
    }

    public String getSerial() {
        return serial;
    }

    public String getMacAddress() {
        return macAddress;
    }


    @NonNull
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("serial:"+this.serial+"\n");
        sb.append("mac address:"+this.macAddress+"\n");

        return sb.toString();
    }
}
