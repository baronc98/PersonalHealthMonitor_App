package personalhealthmonitor.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityPhysicalActivity {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "Date")
    public String date;

    @ColumnInfo(name = "corpo libero")
    public Boolean corpoLibero;

    @ColumnInfo(name = "pesi")
    public Boolean pesi;

    @ColumnInfo(name = "corsa")
    public Boolean corsa;

    @ColumnInfo(name = "camminata")
    public Boolean camminata;

    @ColumnInfo(name = "sport di contatto")
    public Boolean sport;

    @ColumnInfo(name = "yoga")
    public Boolean yoga;

    @ColumnInfo(name = "tempo")
    public String time;

}
