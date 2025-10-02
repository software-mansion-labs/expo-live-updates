package expo.modules.liveupdates.service

enum class NotificationEvent {
  START,
  UPDATE,
  STOP,
}

data class NotificationData(
  var event: NotificationEvent? = null,
  var notificationId: Int? = null,
  var title: String? = null,
  var subtitle: String? = null,
  var progress: Int? = null,
  var progressPoints: List<Int>? = null,
) {
  constructor(data: Map<String, String>) : this() {
    this.event = NotificationEvent.entries.find { it.name == data["event"]?.uppercase() }
    this.notificationId = data["notificationId"]?.toIntOrNull()
    this.title = data["title"]
    this.subtitle = data["subtitle"]
    this.progress = data["progress"]?.toIntOrNull()
    this.progressPoints =
      listOf(data["progressPointOne"], data["progressPointTwo"]).mapNotNull { it?.toIntOrNull() }
  }
}
