package tj.unam.isstracker.utils

import android.util.Log

object CommonUtils {

    fun stringConvertDouble(s: String): Double {
        val s1 = 0.00
        try {
            return java.lang.Double.parseDouble(s)
        } catch (e: NumberFormatException) {
            // p did not contain a valid double
            Log.d("TAG_DOUBLE", "wrong")
        }

        return s1
    }
}