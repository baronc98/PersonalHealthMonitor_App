package personalhealthmonitor.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityInfo {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "Date")
    public String date;

    @ColumnInfo(name = "Weight")
    public String weight;

    @ColumnInfo(name = "Height")
    public String height;

    @ColumnInfo(name = "Age")
    public String age;

}
