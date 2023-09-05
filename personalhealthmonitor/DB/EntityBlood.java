package personalhealthmonitor.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;

@Entity
public class EntityBlood {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "Date")
    public String date;

    @ColumnInfo(name = "pressure")
    public String pressure;

    @ColumnInfo(name = "glycemicIndex")
    public String glycemicIndex;

    @ColumnInfo(name = "drug")
    public String drug;

}
