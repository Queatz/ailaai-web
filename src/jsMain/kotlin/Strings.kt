import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.neverEqualPolicy

data class Translation(
    val en: String,
    val vn: String
)

object Strings {
    val stories = Translation("Stories", "Bản tin")
    val someone = Translation("Someone", "Một người ai đó")
    val goBack = Translation("Go back", "Quay lại")
    val inlineCard = Translation("card", "thẻ")
    val inlineCards = Translation("cards", "thẻ")
    val cards = Translation("Cards", "Thẻ")
    val friends = Translation("Friends", "Bạn")
    val explore = Translation("Explore", "Khám phá")
    val tapToOpen = Translation("Tap to open", "Nhấn để mở")
    val profileNotFound = Translation("Profile not found.", "Không tìm được trang cá nhân này")
    val storyNotFound = Translation("Story not found.", "Không tìm được bản tin này")
    val cardNotFound = Translation("Card not found.", "Không tìm được thẻ này")
    val joined = Translation("Joined", "Đã tham gia")
    val inlineBy = Translation("by", "bởi")
    val draft = Translation("Draft", "Bản nháp")
    val message = Translation("Message", "Nhắn tin")
    val cancel = Translation("Cancel", "Hủy")
    val sendMessage = Translation("Send message", "Gửi tin nhắn")
    val viewProfile = Translation("View profile", "Xem trang cá nhân")
    val includeContact = Translation(
        "Be sure you include a way to contact you!",
        "Bạn lại nhớ gồm một cách để liên lạc bạn!"
    )
    val didntWork = Translation(
        "That didn't work",
        "Điều đó đã không làm được"
    )
    val messageWasSent = Translation(
        "Your message was sent!",
        "Tin nhắn của bạn đã được gửi!"
    )
}


@Composable
private fun appString(string: Translation) = when (LocalConfiguration.current.language) {
    "vi" -> string.vn
    else -> string.en
}

@Composable
fun appString(block: Strings.() -> Translation) = appString(block(Strings))

class Configuration(
    var language: String = "en",
    private val onLanguage: (String) -> Unit,
) {
    fun set(language: String) {
        onLanguage(language)
    }
}

val LocalConfiguration = compositionLocalOf<Configuration>(
    neverEqualPolicy()
) {
    error("No language provided")
}
