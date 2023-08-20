import kotlinx.serialization.Serializable

@Serializable
class LinkDeviceToken(
    var token: String? = null,
    var person: String? = null
) : Model()

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
    var seen: String? = null
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
