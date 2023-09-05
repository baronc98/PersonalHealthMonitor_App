package personalhealthmonitor.adapter;

import android.content.Context;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import personalhealthmonitor.model.ModelNotificationNote;
import personalhealthmonitor.R;

public class MyAdapterNotificationNote  extends RecyclerView.Adapter<MyAdapterNotificationNote.MyHolder> {

    Context c;
    ArrayList<ModelNotificationNote> models;

    public MyAdapterNotificationNote(Context c, ArrayList<ModelNotificationNote> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public MyAdapterNotificationNote.MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_notification_note, viewGroup, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {
        holder.title.setText(models.get(position).getTitle());
        holder.text.setText(models.get(position).getText());
        holder.imgView.setImageResource(models.get(position).getImg());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.hiddenCard.getVisibility() == View.VISIBLE) {
//                    TransitionManager.beginDelayedTransition(holder.card,
//                            new AutoTransition());
                    holder.hiddenCard.setVisibility(View.GONE);
                }else {
                    TransitionManager.beginDelayedTransition(holder.card,
                            new AutoTransition());
                    holder.hiddenCard.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (models != null) return models.size();
        return 0;
    }


    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView imgView;
        TextView title;
        TextView text;
        LinearLayout hiddenCard;
        CardView card;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            this.card = itemView.findViewById(R.id.cardNotificationNote);
            this.imgView = itemView.findViewById(R.id.imageCardNotificationNote);
            this.title = itemView.findViewById(R.id.titleNotificationNote);
            this.text = itemView.findViewById(R.id.textHiddenCard);
            this.hiddenCard = itemView.findViewById(R.id.hiddenCard);
        }
    }
}
