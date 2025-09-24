package expo.modules.liveupdates.service

data class NotificationData(
    var title: String? = null,
    var body: String? = null,
    var currentProgress: Int? = null,
    var currentProgressPointOne: Int? = null,
    var currentProgressPointTwo: Int? = null,
) {
    constructor(data: Map<String, String>) : this() {
        this.title = data.getValue("title")
        this.body = data.getValue("body")
        this.currentProgress = data.getValue("currentProgress").toInt()
        this.currentProgressPointOne = data.getValue("currentProgressPointOne").toInt()
        this.currentProgressPointTwo = data.getValue("currentProgressPointTwo").toInt()
    }
}
