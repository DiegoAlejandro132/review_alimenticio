package com.example.myapplication.util

import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Util {

    fun longToLocalDateTime(dataLong : Long) : LocalDateTime{
        val dataDate = Date(dataLong)
        return dataDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }

    fun getDataFromLong(dataLong: Long) : String{
        val dataDate = Date(dataLong)
        val dataLocalDate = dataDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        return "${dataLocalDate.dayOfMonth.toString().padStart(2,'0')}/${dataLocalDate.monthValue.toString().padStart(2,'0')}/${dataLocalDate.year}"
    }

    fun getHoraFromLong(dataLong : Long) : String{
        val dataDate = Date(dataLong)
        val dataLocalDateTime = dataDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        return "${dataLocalDateTime.hour.toString().padStart(2,'0')}:${dataLocalDateTime.minute.toString().padStart(2,'0')}"
    }

    fun getDataHoraFromLong(long: Long) : String{
        val data = getDataFromLong(long)
        val hora = getHoraFromLong(long)
        return "$data - $hora"
    }

    fun formatarDouble(value: Double): String {
        val decimalFormat = DecimalFormat("#.00")
        return decimalFormat.format(value)
    }
}