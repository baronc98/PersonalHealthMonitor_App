package personalhealthmonitor.DB;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EntityPains {

    @NonNull
    @PrimaryKey()
    @ColumnInfo(name = "Date")
    public String date;

    @ColumnInfo(name = "headPains")
    public Boolean head;

    @ColumnInfo(name = "eyesPains")
    public Boolean eyes;

    @ColumnInfo(name = "artsPains")
    public Boolean arts;

    @ColumnInfo(name = "bellyPains")
    public Boolean belly;

    @ColumnInfo(name = "neckPains")
    public Boolean neck;

    @ColumnInfo(name = "backPains")
    public Boolean back;

    @ColumnInfo(name = "otherPains")
    public String otherPains;

}
