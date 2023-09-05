package personalhealthmonitor.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntityInfoDao {

    @Insert
    void insert(EntityInfo entityInfo);

    @Update
    void update(EntityInfo entityInfo);

    @Query("SELECT * FROM EntityInfo WHERE `date` LIKE :date")
    EntityInfo findEntityInfoByDate(String date);

    @Query("SELECT * FROM EntityInfo")
    List<EntityInfo> getAllEntityInfo();

    @Query("DELETE FROM EntityInfo WHERE `Date` LIKE :date")
    void deleteInfoByDate(String date);

}
