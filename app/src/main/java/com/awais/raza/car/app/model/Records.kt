package com.awais.raza.car.app.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cam_table")
data class Records(
    @PrimaryKey
    @ColumnInfo(name = "rRegNO")
    val rRegNO: String,
    @ColumnInfo(name = "rName")
    val rName: String,
    @ColumnInfo(name = "rMillage")
    val rMillage: String,
    @ColumnInfo(name = "rStatus")
    var rStatus: String,
    @ColumnInfo(name = "rCategory")
    var rCategory: String,
    @ColumnInfo(name = "rStartDate")
    val rStartDate: String,
    @ColumnInfo(name = "rEndDate")
    val rEndDate: String,
    @ColumnInfo(name = "rNote")
    val rNote: String,
)