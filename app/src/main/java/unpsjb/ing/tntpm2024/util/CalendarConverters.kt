package unpsjb.ing.tntpm2024.util

import androidx.room.TypeConverter
import java.util.Calendar

class CalendarConverters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Calendar? {
        return value?.let {
            Calendar.getInstance().apply { timeInMillis = it }
        }
    }

    @TypeConverter
    fun calendarToTimestamp(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }
}
