import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.neverEqualPolicy

data class Translation(
    val en: String,
    val vn: String
)

object Strings {
    val appName = Translation("Ai Là Ai", "Ai là ai")
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
    val homeTagline = Translation(
        "Expand and vivify your city",
        "Mở rộng và làm sinh động\nthành phố của bạn"
    )
    val downloadApp = Translation(
        "Download Ai Là Ai Beta for Android",
        "Tải xuống Ai là ai beta cho Android"
    )
    val homeAboutTitle = Translation(
        "What is Ai là ai?",
        "Ứng dụng Ai là ai là gì?"
    )
    val homeAboutDescription = Translation(
        "Ai là ai is a platform that expands your city in meaningful ways.",
        "Ai là ai là một nền tảng giúp mở rộng thành phố của bạn theo những cách có tâm và đẹp."
    )
    val toJoinThePlatform = Translation(
        "To join the platform, ",
        "Để tham gia nền tảng này, hãy "
    )
    val inlineSendMeAnEmail = Translation(
        "send me an email",
        "gửi email cho ta"
    )
    val engageToday = Translation(
        "and start engaging your city today!",
        "và bắt đầu làm quen với thành phố của bạn ngay!"
    )
    val inviteEmailSubject = Translation(
        "Ai là ai invite to join",
        "Ai là ai lời mời tham gia"
    )
    val peopleToKnow = Translation(
        "People to know",
        "Những người cần biết"
    )
    val placesToKnow = Translation(
        "Places to know",
        "Những chỗ cần biết"
    )
    val thingsToKnow = Translation(
        "Things to know",
        "Những điều cần biết"
    )
    val madeWith = Translation(
        "Made with",
        "Tạo với"
    )
    val inHCMC = Translation(
        "in HCMC",
        "ở TP.HCM"
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
