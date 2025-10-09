package expo.modules.liveupdates.service

enum class FirebaseNotificationEvent {
  START,
  UPDATE,
  STOP,
}

// TODO: this state should be same as LiveUpdateState
data class FirebaseNotificationData(
  val event: FirebaseNotificationEvent,
  val notificationId: Int,
  val title: String,
  var subtitle: String?,
  var progress: Int?,
  var progressPoints: List<Int>?,
) {
  companion object {
    private fun <T> validateNotNull(value: T?, propertyName: String): T {
      return value
        ?: throw IllegalArgumentException("Property $propertyName is missing or invalid.")
    }
  }

  constructor(
    data: Map<String, String>
  ) : this(
    event =
      validateNotNull(
        FirebaseNotificationEvent.entries.find { it.name == data["event"]?.uppercase() },
        "event",
      ),
    notificationId = validateNotNull(data["notificationId"]?.toIntOrNull(), "notificationId"),
    title = validateNotNull(data["title"], "title"),
    subtitle = data["subtitle"],
    progress = data["progress"]?.toIntOrNull(),
    progressPoints =
      listOf(data["progressPointOne"], data["progressPointTwo"]).mapNotNull { it?.toIntOrNull() },
  )
}
