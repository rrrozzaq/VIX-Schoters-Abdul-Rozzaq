package com.vixchotersrozzaq.newsapp.db

import androidx.room.TypeConverter
import com.vixchotersrozzaq.newsapp.models.Source

class Converters {

    //Convert Source ke String
    @TypeConverter
    fun fromSource(source: Source): String{
    return source.name
    }


    //Convert string ke Source
    @TypeConverter
    fun toSource(name: String): Source{
    return Source(name, name)
    }
}