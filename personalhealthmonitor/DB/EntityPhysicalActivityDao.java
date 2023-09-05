package personalhealthmonitor.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntityPhysicalActivityDao {

    @Insert
    void insert(EntityPhysicalActivity entityPhysicalActivity);

    @Update
    void update(EntityPhysicalActivity entityPhysicalActivity);

    @Query("SELECT * FROM EntityPhysicalActivity WHERE `date` LIKE :date")
    EntityPhysicalActivity findEntityPhysicalActivityByDate(String date);

    @Query("SELECT * FROM EntityPhysicalActivity")
    List<EntityPhysicalActivity> getAllEntityPhysicalActivity();

    @Query("DELETE FROM EntityPhysicalActivity WHERE `Date` LIKE :date")
    void deletePhysicalActivityByDate(String date);

}
