package personalhealthmonitor.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntityTemperatureDao {

    @Insert
    void insert(EntityTemperature entityTemperature);

    @Update
    void update(EntityTemperature entityTemperature);

    @Query("SELECT * FROM EntityTemperature WHERE `date` LIKE :date")
    EntityTemperature findEntityTemperatureByDate(String date);

    @Query("SELECT * FROM EntityTemperature")
    List<EntityTemperature> getAllEntityTemperature();

    @Query("DELETE FROM EntityTemperature WHERE `Date` LIKE :date")
    void deleteTemperatureByDate(String date);

}
