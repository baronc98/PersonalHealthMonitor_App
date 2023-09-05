package personalhealthmonitor.DB;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


//definisco le tabelle del mio db, prendedole dalle loro definizioni classe per classe
@Database(version = 1, entities = {
    EntityReports.class,
    EntityTemperature.class,
    EntityInfo.class,
    EntityPains.class,
    EntityPhysicalActivity.class,
    EntityNotificationNote.class,
    EntityBlood.class,
})

//convertitore per oggetto data per poterlo salvare su room
//@TypeConverters({DateConverter.Converters.class})

public abstract class DB extends RoomDatabase {

    //richiamo le Dao
    public abstract EntityReportsDao entityReportsDao();
    public abstract EntityTemperatureDao entityTemperatureDao();
    public abstract EntityInfoDao entityInfoDao();
    public abstract EntityPainsDao entityPainsDao();
    public abstract EntityPhysicalActivityDao entityPhysicalActivityDao();
    public abstract EntityNotificationNoteDao entityNotificationNoteDao();
    public abstract EntityBloodDao entityBloodDao();


    private static DB DB;

    public static DB getInstance(Context context) {
        if (null == DB) {
            DB = buildDatabaseInstance(context);
        }
        return DB;
    }

    private static DB buildDatabaseInstance(Context context) {
        return Room.databaseBuilder(context,
                DB.class,
                "DB")
                .allowMainThreadQueries().build();
    }

    public void cleanUp(){
        DB = null;
    }


}















