package personalhealthmonitor.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class EntityTemperature {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "Date")
    public String date;

    @ColumnInfo(name = "Temperature1")
    public Float temperature1;

    @ColumnInfo(name = "Hours1")
    public String hours1;

    @ColumnInfo(name = "Temperature2")
    public Float temperature2;

    @ColumnInfo(name = "Hours2")
    public String hours2;

    @ColumnInfo(name = "Temperature3")
    public Float temperature3;

    @ColumnInfo(name = "Hours3")
    public String hours3;

}
