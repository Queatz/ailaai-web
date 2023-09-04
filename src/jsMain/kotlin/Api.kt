import components.WildReplyBody
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.browser.localStorage
import kotlinx.serialization.Serializable
import org.w3c.dom.get
import org.w3c.dom.set

val api = Api()

@Serializable
data class TokenResponse(
    val token: String
)

@Serializable
data class SignUpRequest(
    val code: String?
)

@Serializable
data class SignInRequest(
    val code: String? = null,
    val link: String? = null
)

@Serializable
data class CreateGroupBody(val people: List<String>, val reuse: Boolean = false)

private val DefaultContentType = ContentType.Application.Json.withCharset(Charsets.UTF_8)

class Api {
    val device: String
        get() {
            val device = localStorage["device"]
            return if (device.isNullOrBlank()) {
                (0 until 128).token().also {
                    localStorage["device"] = it
                }
            } else {
                device
            }
        }

    suspend fun signIn(
        body: SignInRequest,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (TokenResponse) -> Unit = {}
    ) = post(
        url = "sign/in",
        body = body,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun signUp(
        body: SignUpRequest,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (TokenResponse) -> Unit = {}
    ) = post(
        url = "sign/up",
        body = body,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun device(
        device: Device,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "me/device",
        body = device,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun linkDevice(
        token: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (LinkDeviceToken) -> Unit = {}
    ) = get(
        url = "link-device/$token",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun linkDevice(
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (LinkDeviceToken) -> Unit = {}
    ) = post(
        url = "link-device",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun me(
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Person) -> Unit
    ) = get(
        url = "me",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun updateMe(
        person: Person,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Person) -> Unit
    ) = post(
        url = "me",
        body = person,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun createGroup(
        people: List<String>,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Group) -> Unit
    ) = post(
        url = "groups",
        body = CreateGroupBody(people = people),
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun explore(
        geo: List<Double>,
        search: String? = null,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<Card>) -> Unit
    ) = get(
        url = "cards",
        parameters = mapOf(
            "geo" to geo.joinToString(","),
            "search" to search
        ),
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun groups(
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<GroupExtended>) -> Unit
    ) = get(
        url = "groups",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun stories(
        geo: List<Double>,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<Story>) -> Unit
    ) = get(
        url = "stories",
        parameters = mapOf("geo" to geo.joinToString(",")),
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun myStories(
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<Story>) -> Unit
    ) = get(
        url = "me/stories",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun myCards(
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<Card>) -> Unit
    ) = get(
        url = "me/cards",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun cardsOfCard(
        cardId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<Card>) -> Unit
    ) = get(
        url = "cards/${cardId}/cards",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun profileCards(
        personId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<Card>) -> Unit
    ) = get(
        url = "people/$personId/profile/cards",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun updateProfile(
        profile: Profile,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Profile) -> Unit
    ) = post(
        url = "me/profile",
        body = profile,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun updateCard(
        cardId: String,
        card: Card,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Card) -> Unit
    ) = post(
        url = "cards/$cardId",
        body = card,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun deleteCard(
        cardId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "cards/$cardId/delete",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun updateStory(
        storyId: String,
        story: Story,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Story) -> Unit
    ) = post(
        url = "stories/$storyId",
        body = story,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun updateGroup(
        groupId: String,
        group: Group,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Group) -> Unit = {}
    ) = post(
        url = "groups/$groupId",
        body = group,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun updateMember(
        memberId: String,
        member: Member,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "members/$memberId",
        body = member,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun removeMember(
        memberId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "members/$memberId/delete",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun newCard(
        card: Card,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Card) -> Unit
    ) = post(
        url = "cards",
        body = card,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun newStory(
        story: Story,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Story) -> Unit
    ) = post(
        url = "stories",
        body = story,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun card(
        id: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Card) -> Unit
    ) = get(
        url = "cards/${id}",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun story(
        id: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Story) -> Unit
    ) = get(
        url = "stories/${id}",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun profile(
        personId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (PersonProfile) -> Unit
    ) = get(
        url = "people/$personId/profile",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun profileByUrl(
        url: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (PersonProfile) -> Unit
    ) = get(
        url = "profile/url/$url",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun storyByUrl(
        url: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Story) -> Unit
    ) = get(
        url = "urls/stories/$url",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun message(
        id: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (Message) -> Unit
    ) = get(
        url = "messages/${id}",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun groupMessages(
        groupId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<Message>) -> Unit
    ) = get(
        url = "groups/$groupId/messages",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun group(
        id: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (GroupExtended) -> Unit
    ) = get(
        url = "groups/${id}",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun stickerPacks(
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<StickerPack>) -> Unit
    ) = get(
        url = "sticker-packs",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun saved(
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<SaveAndCard>) -> Unit
    ) = get(
        url = "me/saved",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun save(
        cardId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<SaveAndCard>) -> Unit = {}
    ) = post(
        url = "cards/$cardId/save",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun unsave(
        cardId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (List<SaveAndCard>) -> Unit = {}
    ) = post(
        url = "cards/$cardId/unsave",
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun wildReply(
        reply: WildReplyBody,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "wild/reply",
        body = reply,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun sendMessage(
        groupId: String,
        message: Message,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "groups/$groupId/messages",
        body = message,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun sendPhotos(
        groupId: String,
        body: MultiPartFormDataContent,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "groups/$groupId/photos",
        body = body,
        contentType = null,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun updateCardPhoto(
        cardId: String,
        body: MultiPartFormDataContent,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "cards/$cardId/photo",
        body = body,
        contentType = null,
        onError = onError,
        onSuccess = onSuccess
    )

    suspend fun generateCardPhoto(
        cardId: String,
        onError: suspend (Throwable) -> Unit = {},
        onSuccess: suspend (HttpResponse) -> Unit = {}
    ) = post(
        url = "cards/$cardId/photo/generate",
        onError = onError,
        onSuccess = onSuccess
    )

    private suspend inline fun <reified T> get(
        url: String,
        contentType: ContentType? = DefaultContentType,
        parameters: Map<String, String?>? = null,
        onError: suspend (Throwable) -> Unit,
        onSuccess: suspend (T) -> Unit
    ) {
        try {
            onSuccess(
                http.get("$baseUrl/$url") {
                    if (contentType != null) {
                        contentType(contentType)
                    }
                    application.bearerToken.value?.let { bearer ->
                        bearerAuth(bearer)
                    }
                    parameters?.forEach {
                        parameter(it.key, it.value)
                    }
                }.body<T>()
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            onError(e)
        }
    }

    private suspend inline fun <reified T> post(
        url: String,
        contentType: ContentType? = DefaultContentType,
        parameters: Map<String, String>? = null,
        onError: suspend (Throwable) -> Unit,
        onSuccess: suspend (T) -> Unit
    ) {
        post(
            url,
            contentType,
            null as Unit?,
            parameters,
            onError,
            onSuccess
        )
    }

    private suspend inline fun <reified T, reified B : Any> post(
        url: String,
        contentType: ContentType? = DefaultContentType,
        body: B? = null,
        parameters: Map<String, String>? = null,
        onError: suspend (Throwable) -> Unit,
        onSuccess: suspend (T) -> Unit
    ) {
        try {
            onSuccess(
                http.post("$baseUrl/$url") {
                    if (contentType != null) {
                        contentType(contentType)
                    }
                    application.bearerToken.value?.let { bearer ->
                        bearerAuth(bearer)
                    }
                    if (body != null) {
                        setBody(body)
                    }
                    parameters?.forEach {
                        parameter(it.key, it.value)
                    }
                }.body<T>()
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            onError(e)
        }
    }
}
