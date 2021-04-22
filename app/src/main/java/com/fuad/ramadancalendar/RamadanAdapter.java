package com.fuad.ramadancalendar;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import static com.fuad.ramadancalendar.EnumData.dFormatter;
import static com.fuad.ramadancalendar.EnumData.firstRamadanDate;

public class RamadanAdapter extends RecyclerView.Adapter<RamadanAdapter.RamadanViewHolder> {

    private List<Ramadan> ramadanList;
    private Context context;
    private String currentDate;

    public RamadanAdapter(List<Ramadan> ramadanList, Context context) {
        this.ramadanList = ramadanList;
        this.context = context;
        this.currentDate = new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date());

    }

    class RamadanViewHolder extends RecyclerView.ViewHolder{
        TextView ramadanNo, day, date, sahr, itmam;
        RelativeLayout relativeLayout;
        RamadanViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.ramadan_day_layout);
            ramadanNo = itemView.findViewById(R.id.ramadan_no);
            day = itemView.findViewById(R.id.day);
            date = itemView.findViewById(R.id.date);
            sahr = itemView.findViewById(R.id.sahr);
            itmam = itemView.findViewById(R.id.itmam);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(ramadanList.get(position).getDate().equals(currentDate)){
            return R.layout.ramadan_day_item_selected;
        }
        return R.layout.ramadan_day_item;
    }

    @NonNull
    @Override
    public RamadanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RamadanViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RamadanViewHolder holder, int position) {
        Ramadan ramadan = ramadanList.get(position);

        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        String currentDate = new SimpleDateFormat("dd MMMM", Locale.getDefault()).format(new Date());

        holder.ramadanNo.setText(String.format(Locale.getDefault(),"%s", nf.format(position+1)));
        holder.day.setText(ramadan.getDay());
        holder.date.setText(ramadan.getDate());
        holder.sahr.setText(ramadan.getSahr());
        holder.itmam.setText(ramadan.getItmam());

        /*if(RamadanCalendarFragment.flag && ramadan.getDate().equals(currentDate)){
            final int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                holder.relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.border_bg) );
            } else {
                holder.relativeLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.border_bg));
            }
            RamadanCalendarFragment.flag = false;
        }*/

    }

    @Override
    public int getItemCount() {
        return ramadanList.size();
    }
}
