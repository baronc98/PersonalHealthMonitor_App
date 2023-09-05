package personalhealthmonitor.DB;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EntityReportsDao {

    @Insert
    void insert(EntityReports entityReports);

    @Update
    void update(EntityReports entityReports);

    @Query("SELECT * FROM EntityReports WHERE `Report Date` LIKE :date")
    EntityReports findReportByDate(String date);

    @Query("SELECT * FROM EntityReports")
    List<EntityReports> getReportsList();

    @Query("DELETE FROM EntityReports WHERE `Report Date` LIKE :date")
    void deleteReportByDate(String date);

}
