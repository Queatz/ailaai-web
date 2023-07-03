import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.neverEqualPolicy
import org.jetbrains.compose.web.dom.Text

data class Translation(
    val en: String,
    val vn: String,
    val ru: String = en
)

object Strings {
    val introduction = Translation(
        "About",
        "Giới thiệu"
    )
    val introductionCardId = Translation(
        "13575458",
        "13575494"
    )
    val appName = Translation(
        "Ai Là Ai",
        "Ai là ai",
        "Ай Ла Ай"
    )
    val stories = Translation(
        "Stories",
        "Bài viết",
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
        "Không tìm được bài viết này.",
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
        "Be the life of the city!",
        "Hãy cùng làm thành phố\n" +
            "năng động và đầy màu sắc!",
        "Расширяйте и оживляйте свой город"
    )
    val downloadApp = Translation(
        "Download Ai Là Ai Beta for Android",
        "Tải xuống Ai là ai beta cho Android",
        "Скачать Ай Ла Ай Бета для Андроид"
    )
    val appTagline = Translation(
        "Local Messaging, Exploration, and Inspiration",
        "Nhắn tin, khám phá và đọc cảm hứng ở gần",
        "Локальный обмен сообщениями, торговая площадка и вдохновение"
    )
    val homeAboutTitle = Translation(
        "What is Ai Là Ai?",
        "Ai là ai là gì?"
        ,"Что такое Ай Ла Ай?"
    )
    val homeAboutDescription = Translation(
        "Ai Là Ai is a platform that helps you stay connected to your city in ways meaningful to you.",
        "Nền tảng Ai là ai giúp bạn kết nối gần gũi với thành phố của bạn theo ý thích."
        ,""
    )
    val toJoinThePlatform = Translation(
        "To join the Ai Là Ai platform, ",
        "Để tham gia nền tảng Ai là ai, hãy "
        ,"Чтобы присоединиться к платформе, "
    )
    val inlineSendMeAnEmail = Translation(
        "send me an email",
        "gửi email cho tôi"
        ,"вышли мне электронное письмо"
    )
    val engageToday = Translation(
        "and start engaging with your city today!",
        "và bắt đầu làm quen với thành phố của bạn nha!"
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
    val contact = Translation(
        "Contact",
        "Liên hệ"
    )
    val isCreatedBy = Translation(
        "Ai Là Ai is being created by ",
        "Ai là ai đang tạo ra bởi "
    )
    val sendMeAnEmail = Translation(
        "Send me an email",
        "gửi email cho tôi",
        "Вышли мне электронное письмо"
    )
    val forAllInquiries = Translation(
        " for all inquiries.",
        " cho tất cả các yêu cầu nha."
    )
    val please = Translation(
        "",
        "Hãy "
    )
    val searching = Translation(
        "Searching…",
        "Đang tìm kiếm…"
    )
    val searchResults = Translation(
        "Search results",
        "Kết quả tìm kiếm"
    )
    val noCardsFound = Translation(
        "No cards found.",
        "Không tìm được thẻ để cho bạn xem."
    )
    val whomDoYouSeek = Translation(
        "Who are you looking for today?",
        "Hôm nay bạn đang tìm kiếm ai vậy?"
    )
    val chooseYourCity = Translation(
        "Choose your city",
        "Chọn thành phố"
    )
    val search = Translation(
        "Search",
        "Tìm kiếm"
    )
    val privacyPolicy = Translation(
        "Privacy Policy",
        "Chính sách bảo mật"
    )
    val privacyPolicyText = Translation(
        "Data that you have not designated as publicly available on the Platform is not accessed or provided to any other entity to the extent possible by law.",
        "Dữ liệu mà bạn không chỉ định là có sẵn công khai trên Nền tảng sẽ không được truy cập hoặc cung cấp cho bất kỳ tổ chức nào khác trong phạm vi có thể của pháp luật."
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

@Composable
fun appText(block: Strings.() -> Translation) = Text(appString(block))

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
