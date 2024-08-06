package com.jskaleel.fte.data.source.local.converters

import androidx.room.TypeConverter
import com.jskaleel.fte.core.model.ImageType
import com.jskaleel.fte.core.model.toImage
import com.jskaleel.fte.core.model.toTypeString

class ImageTypeConverter {
    @TypeConverter
    fun imageTypeToString(imageType: ImageType): String {
        return imageType.toTypeString()
    }

    @TypeConverter
    fun stringToImageType(value: String): ImageType {
        return value.toImage()
    }
}