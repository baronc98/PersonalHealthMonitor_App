package personalhealthmonitor.DB;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class EntityReports {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "Report Date")
    public String date;

    @ColumnInfo(name = "info")
    public boolean infoClickable = false;

    @ColumnInfo(name = "temperature")
    public boolean temperatureClickable = false;

    @ColumnInfo(name = "pains")
    public boolean painsClickable = false;

    @ColumnInfo(name = "physicalActivity")
    public boolean physicalActivityClickable = false;

    @ColumnInfo(name = "pressure")
    public boolean pressureClickable = false;

    @ColumnInfo(name = "glycemicIndex")
    public boolean glycemicIndexClickable = false;


    @ColumnInfo(name = "drug")
    public boolean drugClickable = false;


    //constructor
    public EntityReports(String date){
        this.date = date;
    }

}
