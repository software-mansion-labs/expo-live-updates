package expo.modules.liveupdates

import android.content.Context
import androidx.core.content.edit

const val PREFERENCES_NAME = "liveUpdatesPreferences"
const val VALUE_NAME = "lastNotificationId"

class IdGenerator(private val context: Context) {
  private fun saveId(number: Int) {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    sharedPreferences.edit { putInt(VALUE_NAME, number) }
  }

  private fun getId(): Int {
    val sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sharedPreferences.getInt(VALUE_NAME, -1)
  }

  fun generateNextId(): Int {
    val lastId = getId()
    val nextId =
      if (lastId == -1 || lastId == Int.MAX_VALUE) {
        1
      } else {
        lastId + 1
      }

    saveId(nextId)
    return nextId
  }
}
