package personalhealthmonitor;

import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityNotificationNote;
import personalhealthmonitor.adapter.MyAdapterNotificationNote;
import personalhealthmonitor.model.ModelNotificationNote;

public class NotificationNote extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapterNotificationNote adapter;
    private ArrayList<ModelNotificationNote> cards = new ArrayList<>();
    public Context context;
    public DB DBinstance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.notification_note);

        context = getApplicationContext();

        DBinstance = DB.getInstance(this);

        recyclerView = findViewById(R.id.notificationNoteRecycleview);

        initRecycleView();

    }

    private void initRecycleView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        cards = setCards();

        adapter = new MyAdapterNotificationNote(context, cards);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(adapter);

        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);
        recyclerView.setLayoutAnimation(layoutAnimationController);
    }

    private ArrayList<ModelNotificationNote> setCards(){

        List<EntityNotificationNote> entityNotificationNoteList = DBinstance.entityNotificationNoteDao().getAllNotiticationNote();

        if (entityNotificationNoteList.size() == 0){
            Utils.generateToast("Nessuna notifica da mostrare", context);
            finish();
        }else {
            for (int i = entityNotificationNoteList.size() - 1; i >= 0; i--){
                EntityNotificationNote entityNotificationNote = entityNotificationNoteList.get(i);
                ModelNotificationNote m = new ModelNotificationNote();
                m.setTitle(entityNotificationNote.title);
                m.setText(entityNotificationNote.text);
                m.setId(entityNotificationNote.id);
                if (entityNotificationNote.type.equals("doctor")){
                    m.setImg(R.drawable.doctor);
                }else if (entityNotificationNote.type.equals("sendNotification")){
                    m.setImg(R.drawable.app_icon);
                }else if (entityNotificationNote.type.equals("virus")){
                    m.setImg(R.drawable.virus);
                }
                cards.add(m);
            }
        }
        return cards;
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int item = viewHolder.getAdapterPosition();
            ModelNotificationNote modelNotificationNote = cards.get(item);
            cards.remove(item);
            DBinstance.entityNotificationNoteDao().deleteNotificationNoteById(modelNotificationNote.getId());
            adapter.notifyDataSetChanged();
            if (cards.size() == 0){
                Utils.generateToast("Nessuna notifica", context);
                finish();
            }else {
                Utils.generateToast("Notifica rimossa", context);
            }
        }
    };

}