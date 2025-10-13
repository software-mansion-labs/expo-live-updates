package expo.modules.liveupdates

import android.content.Context
import android.util.Log
import androidx.core.content.edit

const val PREFERENCES_NAME = "storeID"
const val VALUE_NAME = "lastID"

const val GENERATOR_TAG = "Generator"

class IDGenerator (private val context: Context){
    private fun saveID(number: Int) {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit { putInt(VALUE_NAME, number) }
    }

    private fun getID(): Int {
        val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(VALUE_NAME, -1)
    }

    fun generateNextID (): Int {
        val lastID = getID()
        Log.i(GENERATOR_TAG, "lastID: $lastID")

        val nextID = if (lastID == -1 || lastID == Int.MAX_VALUE){
            1
        } else {
            lastID + 1
        }
        saveID(nextID)

        Log.i("GENERATOR", "nestID: $nextID")
        return nextID
    }

}