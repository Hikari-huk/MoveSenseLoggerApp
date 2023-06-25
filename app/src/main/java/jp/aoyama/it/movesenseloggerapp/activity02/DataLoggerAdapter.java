package jp.aoyama.it.movesenseloggerapp.activity02;

import static jp.aoyama.it.movesenseloggerapp.activity02.DataLoggerActivity.fetchAllEntriesLog;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import jp.aoyama.it.movesenseloggerapp.R;

public class DataLoggerAdapter extends RecyclerView.Adapter<DataLoggerAdapter.DataLoggerViewHolder> {

    private ArrayList<DataLoggerModel> dataLoggerModelArrayList;


    public static class DataLoggerViewHolder extends RecyclerView.ViewHolder{
        private final TextView tvMacAddress;
        private final TextView tvSerial;
        private Spinner spSensorPosition;

        private Button btnFetch;


        public DataLoggerViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvMacAddress = itemView.findViewById(R.id.tvConnectMacAddress);
            this.tvSerial = itemView.findViewById(R.id.tvConnectSerialNumber);
            this.spSensorPosition = itemView.findViewById(R.id.spSensorPosition);
            this.btnFetch = itemView.findViewById(R.id.btnFetchLog);
        }

        public TextView getTvConnectedMacAddress(){
            return tvMacAddress;
        }

        public TextView getTvConnectedSerial(){return tvSerial;}
    }

    public DataLoggerAdapter(ArrayList<DataLoggerModel> list){
        this.dataLoggerModelArrayList = list;
    }


    @NonNull
    @Override
    public DataLoggerAdapter.DataLoggerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_datalogger,parent, false);
        DataLoggerViewHolder holder = new DataLoggerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DataLoggerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.getTvConnectedMacAddress().setText(dataLoggerModelArrayList.get(position).getMacAddress());
        holder.getTvConnectedSerial().setText(dataLoggerModelArrayList.get(position).getSerial());

        ArrayAdapter<CharSequence> adapterSensorPosition = ArrayAdapter.createFromResource(holder.itemView.getContext(), R.array.sensor_position_array, android.R.layout.simple_spinner_item);
        adapterSensorPosition.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spSensorPosition.setAdapter(adapterSensorPosition);
        holder.spSensorPosition.setSelection(0);
        holder.spSensorPosition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int p, long id) {
                dataLoggerModelArrayList.get(position).setSensorPosition(parent.getItemAtPosition(p).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        holder.btnFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId()==R.id.btnFetchLog){
                    fetchAllEntriesLog(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataLoggerModelArrayList.size();
    }

}
