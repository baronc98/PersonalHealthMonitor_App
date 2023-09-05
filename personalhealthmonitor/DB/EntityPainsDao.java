package personalhealthmonitor.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntityPainsDao {

    @Insert
    void insert(EntityPains entityPains);

    @Update
    void update(EntityPains entityPains);

    @Query("SELECT * FROM EntityPains WHERE `date` LIKE :date")
    EntityPains findEntityPainsByDate(String date);

    @Query("SELECT * FROM EntityPains")
    List<EntityPains> getAllEntityPains();

    @Query("DELETE FROM EntityPains WHERE `Date` LIKE :date")
    void deletePainsByDate(String date);

}
