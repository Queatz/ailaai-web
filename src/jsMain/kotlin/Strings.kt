import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.neverEqualPolicy

data class Translation(
    val en: String,
    val vn: String,
    val ru: String = en
)

object Strings {
    val appName = Translation(
        "Ai Là Ai",
        "Ai là ai",
        "Ай Ла Ай"
    )
    val stories = Translation(
        "Stories",
        "Bản tin",
        "Истории"
    )
    val someone = Translation(
        "Someone",
        "Một người ai đó",
        "Кто-то"
    )
    val goBack = Translation(
        "Go back",
        "Quay lại",
        "Вернуться"
    )
    val inlineCard = Translation(
        "card",
        "thẻ",
        "карта"
    )
    val inlineCards = Translation(
        "cards",
        "thẻ",
        "карты"
    )
    val cards = Translation(
        "Cards",
        "Thẻ",
        "Карты"
    )
    val friends = Translation(
        "Friends",
        "Bạn",
        "Друзья"
    )
    val explore = Translation(
        "Explore",
        "Khám phá",
        "Исследовать"
    )
    val tapToOpen = Translation(
        "Tap to open",
        "Nhấn để mở",
        "Нажмите, чтобы открыть"
    )
    val profileNotFound = Translation(
        "Profile not found.",
        "Không tìm được trang cá nhân này.",
        "Профиль не найден."
    )
    val storyNotFound = Translation(
        "Story not found.",
        "Không tìm được bản tin này.",
        "История не найдена."
    )
    val cardNotFound = Translation(
        "Card not found.",
        "Không tìm được thẻ này.",
        "Карта не найдена."
    )
    val joined = Translation(
        "Joined",
        "Đã tham gia",
        "Присоединился"
    )
    val inlineBy = Translation(
        "by",
        "bởi",
        ""
    )
    val draft = Translation(
        "Draft",
        "Bản nháp",
        "Черновик"
    )
    val message = Translation(
        "Message",
        "Nhắn tin",
        "Cообщение"
    )
    val cancel = Translation(
        "Cancel",
        "Hủy",
        "Отмена"
    )
    val sendMessage = Translation(
        "Send message",
        "Gửi tin nhắn",
        "Отправить сообщение"
    )
    val viewProfile = Translation(
        "View profile",
        "Xem trang cá nhân",
        "Просмотреть профиль"
    )
    val includeContact = Translation(
        "Be sure you include a way to contact you!",
        "Bạn lại nhớ gồm một cách để liên lạc bạn!",
        "Обязательно укажите способ связи с вами!"
    )
    val didntWork = Translation(
        "That didn't work",
        "Điều đó đã không làm được",
        "Это не сработало"
    )
    val messageWasSent = Translation(
        "Your message was sent!",
        "Tin nhắn của bạn đã được gửi!",
        "Ваше сообщение отправлено!"
    )
    val homeTagline = Translation(
        "Expand and vivify your city",
        "Mở rộng và làm sinh động\nthành phố của bạn",
        "Расширяйте и оживляйте свой город"
    )
    val downloadApp = Translation(
        "Download Ai Là Ai Beta for Android",
        "Tải xuống Ai là ai beta cho Android",
        "Скачать Ай Ла Ай Бета для Андроид"
    )
    val homeAboutTitle = Translation(
        "What is Ai là ai?",
        "Ai là ai là gì?"
        ,"Что такое Ай Ла Ай?"
    )
    val homeAboutDescription = Translation(
        "Ai là ai is a platform that expands your city in meaningful ways.",
        "Ai là ai là một nền tảng giúp mở rộng thành phố của bạn theo những cách có tâm và đẹp."
        ,"Ай Ла Ай — это платформа, которая существенно расширяет ваш город."
    )
    val toJoinThePlatform = Translation(
        "To join the platform, ",
        "Để tham gia nền tảng này, hãy "
        ,"Чтобы присоединиться к платформе, "
    )
    val inlineSendMeAnEmail = Translation(
        "send me an email",
        "gửi email cho ta"
        ,"вышли мне электронное письмо"
    )
    val engageToday = Translation(
        "and start engaging your city today!",
        "và bắt đầu làm quen với thành phố của bạn ngay!"
        ,"и начните вовлекать свой город уже сегодня!"
    )
    val inviteEmailSubject = Translation(
        "Ai là ai invite to join",
        "Ai là ai lời mời tham gia",
        "Ай ла ай приглашаю присоединиться"
    )
    val peopleToKnow = Translation(
        "People to know",
        "Những người cần biết",
        "Люди, которых знать"
    )
    val placesToKnow = Translation(
        "Places to know",
        "Những chỗ cần biết",
        "Места, чтобы знать"
    )
    val thingsToKnow = Translation(
        "Things to know",
        "Những điều cần biết",
        "Что,чтобы знать"
    )
    val madeWith = Translation(
        "Made with",
        "Tạo với",
        "Сделано с"
    )
    val inHCMC = Translation(
        "in HCMC",
        "ở TP.HCM",
        "в Хошимине"
    )
}

@Composable
private fun appString(string: Translation) = when (LocalConfiguration.current.language) {
    "vi" -> string.vn
    "ru" -> string.ru
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
