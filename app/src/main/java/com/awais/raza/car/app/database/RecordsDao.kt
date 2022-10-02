package com.awais.raza.car.app.database

import androidx.room.*
import com.awais.raza.car.app.model.Records

@Dao
interface RecordsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addRecord(cameraData: Records)

    //    @Query("SELECT * FROM cv_table ORDER BY id ASC")
    @Query("SELECT * FROM cam_table Where rRegNO=:id")
    fun readForUpdateData(id: String): List<Records>

    @Query("SELECT * FROM cam_table Where ((SELECT max(rRegNO) FROM cam_table))")
    fun readParticularData(): List<Records>

//    @Query("SELECT * FROM cam_table ORDER BY rRegNO ASC")
//    fun readAllData(): List<Records>
//
    @Query("SELECT * FROM cam_table")
    fun readAllData(): List<Records>

    @Query("DELETE FROM cam_table")
    fun dropTable(): Int

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateRecord(cameraData: Records)

    @Delete
    fun deleteRecord(cameraData: Records):Int

}