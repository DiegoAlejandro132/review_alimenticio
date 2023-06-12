package com.example.myapplication.util

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class Util {

    fun longToLocalDateTime(dataLong : Long) : LocalDateTime{
        val dataDate = Date(dataLong)
        return dataDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    }
}