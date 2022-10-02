package com.awais.raza.car.app.listener

import com.awais.raza.car.app.model.Records

interface OnRecordClickListener {
    fun onRecordClick(records: Records)
    fun onRecordDelete(records: Records)
}