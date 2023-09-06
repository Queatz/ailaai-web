import kotlinx.serialization.Serializable

enum class DeviceType {
    Hms,
    Gms,
    Web
}

@Serializable
class LinkDeviceToken(
    var token: String? = null,
    var person: String? = null
) : Model()

@Serializable
class Device(
    val type: DeviceType,
    val token: String,
)

@Serializable
class Group(
    var name: String? = null,
    var seen: String? = null,
    var description: String? = null,
) : Model()


@Serializable
class MemberAndPerson(
    var person: Person? = null,
    var member: Member? = null,
)

@Serializable
class Save(
    var person: String? = null,
    var card: String? = null
) : Model()

@Serializable
class SaveAndCard(
    var save: Save? = null,
    var card: Card? = null
)

@Serializable
class Message(
    var group: String? = null,
    var member: String? = null,
    var text: String? = null,
    var attachment: String? = null,
    var attachments: List<String>? = null
) : Model()

@Serializable
class Member(
    var seen: String? = null,
    var hide: Boolean? = null,
    var gone: Boolean? = null,
    var host: Boolean? = null
) : Edge()

@Serializable
open class Edge : Model() {
    var from: String? = null
    var to: String? = null
}
@Serializable
class GroupExtended(
    var group: Group? = null,
    var members: List<MemberAndPerson>? = null,
    var latestMessage: Message? = null,
)

@Serializable
class Card(
    var person: String? = null,
    var parent: String? = null,
    var name: String? = null,
    var photo: String? = null,
    var video: String? = null,
    var location: String? = null,
    var collaborators: List<String>? = null,
    var categories: List<String>? = null,
    var equipped: Boolean? = null,
    var geo: List<Double>? = null,
    var conversation: String? = null,
    var options: String? = null,
    var active: Boolean? = null,
    var offline: Boolean? = null,
    var cardCount: Int? = null
) : Model()

@Serializable
class Story(
    var person: String? = null,
    var title: String? = null,
    var url: String? = null,
    var geo: List<Double>? = null,
    var publishDate: String? = null,
    var published: Boolean? = null,
    var content: String? = null,
    var authors: List<Person>? = null
) : Model()

@Serializable
class Person(
    var name: String? = null,
    var photo: String? = null,
    var seen: String? = null,
    var geo: List<Double>? = null
) : Model()

@Serializable
class Sticker(
    var photo: String? = null,
    var pack: String? = null,
    var name: String? = null,
    var message: String? = null,
) : Model()

@Serializable
class StickerPack(
    var name: String? = null,
    var description: String? = null,
    var person: String? = null,
    var active: Boolean? = null,
    var stickers: List<Sticker>? = null
) : Model()

@Serializable
class Reminder(
    var person: String? = null,
    var groups: List<String>? = null,
    var attachment: String? = null,
    var title: String? = null,
    var note: String? = null,
    var start: String? = null,
    var end: String? = null,
    var schedule: ReminderSchedule? = null
) : Model()

@Serializable
class ReminderOccurrence(
    var reminder: String? = null,
    var occurrence: String? = null,
    var date: String? = null,
    var note: String? = null,
    var done: String? = null,
    var gone: Boolean? = null,
) : Model()

@Serializable
class ReminderSchedule(
    /**
     * 0 - 23 = hour of day
     */
    val hours: List<Int>? = null,
    /**
     * 1 - 31 = day of month
     * -1 - -31 = last days of month
     */
    val days: List<Int>? = null,
    /**
     * 1 - 7 = day of week
     */
    val weekdays: List<Int>? = null,
    /**
     * 1 - 5 = week of month
     */
    val weeks: List<Int>? = null,
    /**
     * 1 - 12 = month of year
     */
    val months: List<Int>? = null,
    /**
     * year
     */
    val years: List<Int>? = null,
)

@Serializable
open class Model {
    var id: String? = null
    var createdAt: String? = null
}

@Serializable
data class ProfileStats(
    val friendsCount: Int,
    val cardCount: Int
)

@Serializable
data class PersonProfile(
    val person: Person,
    val profile: Profile,
    val stats: ProfileStats
)

@Serializable
class Profile(
    var person: String? = null,
    var photo: String? = null,
    var video: String? = null,
    var about: String? = null,
    var url: String? = null
) : Model()
