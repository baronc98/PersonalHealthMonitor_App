package personalhealthmonitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import personalhealthmonitor.BloodSection;
import personalhealthmonitor.InfoSection;
import personalhealthmonitor.model.ModelTodayReport;
import personalhealthmonitor.PainsSection;
import personalhealthmonitor.PhysicalActivitySection;
import personalhealthmonitor.R;
import personalhealthmonitor.TemperatureSection;
import personalhealthmonitor.Utils;

public class MyAdapterTodayReport extends RecyclerView.Adapter<MyAdapterTodayReport.MyHolder>{

    Context c;
    ArrayList<ModelTodayReport> models;
    String date;

    public MyAdapterTodayReport(Context c, ArrayList<ModelTodayReport> models, String date) {
        this.c = c;
        this.models = models;
        this.date = date;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_today_report, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        holder.title.setText(models.get(position).getTitle());
        holder.imgView.setImageResource(models.get(position).getImg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.title.getText().toString().equals("Temperatura Corporea")) {
                    Intent newPage = new Intent(c, TemperatureSection.class);
                    newPage.putExtra("date", date);
                    newPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(newPage);
                }else if (holder.title.getText().toString().equals("Informazioni Generali")) {
                    Intent newPage = new Intent(c, InfoSection.class);
                    newPage.putExtra("date", date);
                    newPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(newPage);
                }else if (holder.title.getText().toString().equals("Dolori")) {
                    Intent newPage = new Intent(c, PainsSection.class);
                    newPage.putExtra("date", date);
                    newPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(newPage);
                }else if (holder.title.getText().toString().equals("Attività Fisica")) {
                    Intent newPage = new Intent(c, PhysicalActivitySection.class);
                    newPage.putExtra("date", date);
                    newPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(newPage);
                }else if (holder.title.getText().toString().equals("Valori Del Sangue")) {
                    Intent newPage = new Intent(c, BloodSection.class);
                    newPage.putExtra("date", date);
                    newPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    c.startActivity(newPage);
                }else{
                    Utils.generateToast("Error", c);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView imgView;
        TextView title;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.imgView = itemView.findViewById(R.id.imageCard);
            this.title = itemView.findViewById(R.id.title);

        }
    }


    }
