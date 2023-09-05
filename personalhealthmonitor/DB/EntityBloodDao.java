package personalhealthmonitor.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntityBloodDao {

    @Insert
    void insert(EntityBlood entityBlood);

    @Update
    void update(EntityBlood entityBlood);

    @Query("SELECT * FROM EntityBlood WHERE `date` LIKE :date")
    EntityBlood findEntityBloodByDate(String date);

    @Query("SELECT * FROM EntityBlood")
    List<EntityBlood> getAllEntityBlood();

    @Query("DELETE FROM EntityBlood WHERE `Date` LIKE :date")
    void deleteBloodByDate(String date);

}
