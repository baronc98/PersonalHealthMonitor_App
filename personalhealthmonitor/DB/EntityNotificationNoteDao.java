package personalhealthmonitor.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntityNotificationNoteDao {

    @Insert
    void insert(EntityNotificationNote entityNotificationNote);

    @Update
    void update(EntityNotificationNote entityNotificationNote);

    @Query("SELECT * FROM EntityNotificationNote")
    List<EntityNotificationNote> getAllNotiticationNote();

    @Query("DELETE FROM EntityNotificationNote WHERE `id` LIKE :id")
    void deleteNotificationNoteById(int id);

}
