package personalhealthmonitor.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import personalhealthmonitor.model.ModelReportCollection;
import personalhealthmonitor.R;
import personalhealthmonitor.ReportVisualization;
import personalhealthmonitor.Utils;

public class MyAdapterReportCollection extends RecyclerView.Adapter<MyAdapterReportCollection.MyHolder>{

    Context c;
    ArrayList<ModelReportCollection> models;

    public MyAdapterReportCollection(Context c, ArrayList<ModelReportCollection> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_report_collaction, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        holder.title.setText(models.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                if (holder.title.getText().equals(dateFormat.format(calendar.getTime()))){
                    //MainActivity.returnToSalute();
                    Utils.generateToast("Report giornaliero... SWIPE A DESTRA!", c);
                }else {
                    Intent newPage = new Intent(c, ReportVisualization.class);
                    newPage.putExtra("date", holder.title.getText());
                    c.startActivity(newPage);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return models.size();
    }


    public class MyHolder extends RecyclerView.ViewHolder {

        TextView title;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.title = itemView.findViewById(R.id.title);

        }
    }


}
