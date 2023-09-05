package personalhealthmonitor.DB;

import personalhealthmonitor.DB.DB;
import personalhealthmonitor.DB.EntityReports;

public class UtilsDB {

    //controlla che non esista un report con la stessa data nel DB
    public static boolean checkIfExistReport(String date, DB DBinstance){
        if (DBinstance.entityReportsDao().findReportByDate(date) == null){
            return false;
        }else {
            return true;
        }
    }


    //creo un report che non esiste ancora
    public static void createReport(String date, DB DBinstance){
        EntityReports entityReports = new EntityReports(date);
        DBinstance.entityReportsDao().insert(entityReports);
    }

    public static EntityReports getReportByDate(String date, DB DBinstance){
        return DBinstance.entityReportsDao().findReportByDate(date);
    }

    public static void deleteReportFromDB(String date, DB DBinstance){
        DBinstance.entityReportsDao().deleteReportByDate(date);
        DBinstance.entityTemperatureDao().deleteTemperatureByDate(date);
        DBinstance.entityPainsDao().deletePainsByDate(date);
        DBinstance.entityInfoDao().deleteInfoByDate(date);
        DBinstance.entityPhysicalActivityDao().deletePhysicalActivityByDate(date);
        DBinstance.entityBloodDao().deleteBloodByDate(date);
    }

}
