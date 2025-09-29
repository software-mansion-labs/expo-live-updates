package expo.modules.liveupdates.service

data class NotificationData(
  var title: String? = null,
  var subtitle: String? = null,
  var progress: Int? = null,
  var progressPoints: List<Int>? = null,
) {
  constructor(data: Map<String, String>) : this() {
    this.title = data["title"]
    this.subtitle = data["subtitle"]
    this.progress = data["progress"]?.toIntOrNull()
    this.progressPoints =
      listOf(data["progressPointOne"], data["progressPointTwo"]).mapNotNull { it?.toIntOrNull() }
  }
}
