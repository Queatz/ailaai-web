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
    val openGroup = Translation(
        "Open group",
        "Nhóm mở"
    )
    val accept = Translation(
        "Accept",
        "Chấp nhận"
    )
    val delete = Translation(
        "Delete",
        "Xóa bỏ"
    )
    val inlineMember = Translation(
        "member",
        "thành viên"
    )
    val inlineMembers = Translation(
        "members",
        "thành viên"
    )
    val close = Translation(
        "Close",
        "Đóng"
    )
    val manage = Translation(
        "Manage",
        "Quản lý"
    )
    val selectAGroup = Translation(
    "Select a group",
        "Chọn một nhóm"
    )
    val introduction = Translation(
        "About",
        "Giới thiệu"
    )
    val introductionCardId = Translation(
        "13575458",
        "13575494"
    )
    val appName = Translation(
        "Hi Town",
        "Chào Town",
        "Город приветик"
    )
    val stories = Translation(
        "Stories",
        "Bài viết",
        "Истории"
    )
    val createStory = Translation(
        "New story",
        "Tạo bài viết"
    )
    val newStory = Translation(
        "New story",
        "Bài viết mới"
    )
    val newCard = Translation(
        "New page",
        "Trang mới"
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
        "page",
        "trang",
//        "карта"
    )
    val inlineCards = Translation(
        "pages",
        "trang",
//        "карты"
    )
    val cards = Translation(
        "Pages",
        "Trang",
        "Карты"
    )
    val friends = Translation(
        "Friends",
        "Bạn bè",
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
        "Page not found.",
        "Không tìm được trang này.",
//        "Карта не найдена."
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
    val sending = Translation(
        "Sending...",
        "Đang gửi...",
        // todo
    )
    val cancel = Translation(
        "Cancel",
        "Hủy",
        "Отмена"
    )
    val okay = Translation(
        "Okay",
        "Đòng ý"
    )
    val sendMessage = Translation(
        "Send message",
        "Gửi tin nhắn",
    )
    val sendPhoto = Translation(
        "Send photo",
        "Gửi ảnh"
    )
    val viewProfile = Translation(
        "View profile",
        "Xem trang cá nhân",
        "Просмотреть профиль"
    )
    val profile = Translation(
        "Profile",
        "Trang cá nhân",
        "Профиль"
    )
    val includeContact = Translation(
        "Be sure to include a way to contact you!",
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
        "Download Hi Town Beta for Android",
        "Tải xuống Chào Town beta cho Android",
        "Скачать Ай Ла Ай Бета для Андроид"
    )
    val appTagline = Translation(
        "Local Messaging, Exploration, and Inspiration",
        "Nhắn tin, khám phá và đọc cảm hứng ở gần",
        "Локальный обмен сообщениями, торговая площадка и вдохновение"
    )
    val homeAboutTitle = Translation(
        "What is Hi Town?",
        "Chào Town là gì?"
        ,"Что такое Ай Ла Ай?"
    )
    val homeAboutDescription = Translation(
        "Hi Town is a collaboration platform that helps you discover and stay connected to your city, enabling you to do more, externalize all of your visions, and go farther than you ever imagined.",
        "Chào Town là một nền tảng hợp tác giúp bạn khám phá và giữ liên lạc với mọi người trong thành phố của bạn, cho phép bạn làm được nhiều hơn, hiện thực hóa tất cả tầm nhìn của bạn và tiến xa hơn những gì bạn tưởng tượng."
        ,""
    )
    val inviteEmailSubject = Translation(
        "Hi Town invite to join",
        "Chào Town lời mời tham gia",
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
        "Hi Town is being created by ",
        "Chào Town đang tạo ra bởi "
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
    val noCards = Translation(
        "No pages",
        "Không tìm được trang để cho bạn xem."
    )
    val noSavedCards = Translation(
        "No saved pages",
        "Bạn chưa lưu trang"
    )
    val noCardsNearby = Translation(
        "No pages",
        "Không tìm được trang gần đây."
    )
    val noStories = Translation(
        "No stories",
        "Không tìm được bài viết."
    )
    val noGroups = Translation(
        "No groups",
        "Không tìm được nhóm."
    )
    val searchCity = Translation(
        "Search Ho Chi Minh City",
        "Tìm kiếm trong TP. Hồ Chí Mình"
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
    val tos = Translation(
        "Terms of Use",
        "Điều khoản sử dụng"
    )
    val openSource = Translation(
        "Open Source",
        "Mã nguồn mở"
    )
    val signIn = Translation(
        "Sign in",
        "Đăng nhập"
    )
    val signUp = Translation(
        "Sign up",
        "Đăng ký"
    )
    val signOut = Translation(
        "Sign out",
        "Đăng xuất"
    )
    val signOutQuestion = Translation(
        "Sign out?",
        "Đăng xuất?"
    )
    val signOutQuestionLine1 = Translation(
        "You will permanently lose access to this account",
        "Bạn sẽ vĩnh viễn mất quyền truy cập vào tài khoản này"
    )
    val signOutQuestionLine2 = Translation(
        "if you are not currently signed in on another device.",
        "nếu bạn hiện chưa đăng nhập trên thiết bị khác."
    )
    val and = Translation(
        "and",
        "và"
    )
    val qrCode = Translation(
        "QR code",
        "Mã QR"
    )
    val yourName = Translation(
        "Your name",
        "Tên của bạn"
    )
    val update = Translation(
        "Update",
        "Cập nhật"
    )
    val introduceYourself = Translation(
        "Introduce yourself here",
        "Viết chút về bạn"
    )
    val filter = Translation(
        "Filter",
        "Cập nhật"
    )
    val published = Translation(
        "Published",
        "Xuất bản"
    )
    val notPublished = Translation(
        "Not published",
        "Không xuất bản"
    )
    val createCard = Translation(
        "New page",
        "Tạo trang"
    )
    val title = Translation(
        "Title",
        "Tiêu"
    )
    val create = Translation(
        "Create",
        "Tạo"
    )
    val createGroup = Translation(
        "Create group",
        "Tạo nhóm"
    )
    val local = Translation(
        "Local",
        "Gần đây"
    )
    val saved = Translation(
        "Saved",
        "Đã lưu"
    )
    val groups = Translation(
        "Groups",
        "Nhóm"
    )
    val groupName = Translation(
        "Group name",
        "Tên nhóm"
    )
    val reminders = Translation(
        "Reminders",
        "Lời nhắc"
    )
    val noReminders = Translation(
        "No reminders",
        "Không có lời nhắc"
    )
    val createReminder = Translation(
        "New reminder",
        "Tạo lời nhắc"
    )
    val daily = Translation(
        "Daily",
        "Theo ngày"
    )
    val weekly = Translation(
        "Weekly",
        "Theo tuần"
    )
    val monthly = Translation(
        "Monthly",
        "Theo tháng"
    )
    val yearly = Translation(
        "Yearly",
        "Theo năm"
    )
    val you = Translation(
        "You",
        "Bạn"
    )
    val newGroup = Translation(
        "New group",
        "Nhóm mới"
    )
    val created = Translation(
        "Created",
        "Đã tạo"
    )
    val inlinePersonWaiting = Translation(
        "person waiting",
        "người đang chờ"
    )
    val inlinePeopleWaiting = Translation(
        "people waiting",
        "người đang chờ"
    )
    val onProfile = Translation(
        "On profile",
        "Trên trang cá nhân"
    )
    val atALocation = Translation(
        "At a location",
        "Tại một vị trí nào đó"
    )
    val inAPage = Translation(
        "In a page",
        "Trong trang"
    )
    val none = Translation(
        "None",
        "Chưa có"
    )
    val choosePhoto = Translation(
        "Choose photo",
        "Chọn ảnh"
    )
    val generatePhoto = Translation(
        "Generate photo",
        "Tạo ảnh"
    )
    val regeneratePhoto = Translation(
        "Regenerate photo",
        "Tạo ảnh mới"
    )
    val openEnclosingCard = Translation(
        "Open enclosing page",
        "Mở trang có trang này"
    )
    val previousDay = Translation(
        "Previous day",
        "Ngày trước"
    )
    val previousWeek = Translation(
        "Previous week",
        "Tuần trước"
    )
    val previousMonth = Translation(
        "Previous month",
        "Tháng trước"
    )
    val previousYear = Translation(
        "Previous year",
        "Năm trước"
    )
    val nextDay = Translation(
        "Next day",
        "Ngày sau"
    )
    val nextWeek = Translation(
        "Next week",
        "Tuần sau"
    )
    val nextMonth = Translation(
        "Next month",
        "Tháng sau"
    )
    val nextYear = Translation(
        "Next year",
        "Năm sau"
    )
    val today = Translation(
        "Today",
        "Hôm nay"
    )
    val yesterday = Translation(
        "Yesterday",
        "Hôm qua"
    )
    val tomorrow = Translation(
        "Tomorrow",
        "Ngày mai"
    )
    val loading = Translation(
        "Loading",
        "Đang tải"
    )
    val rename = Translation(
        "Rename",
        "Đổi tên"
    )
    val openInNewTab = Translation(
        "Open in new tab",
        "Mở ra trong tab mới"
    )
    val options = Translation(
        "Options",
        "Tùy chọn"
    )
    val unsave = Translation(
        "Unsave",
        "Bỏ lưu"
    )
    val save = Translation(
        "Save",
        "Lưu"
    )
    val hint = Translation(
        "Hint",
        "Lời gợi ý"
    )
    val location = Translation(
        "Location",
        "Vị trí"
    )
    val details = Translation(
        "Details",
        "Chi tiết"
    )
    val members = Translation(
        "Members",
        "Thành viên"
    )
    val hide = Translation(
        "Hide",
        "Ẩn"
    )
    val leave = Translation(
        "Leave",
        "Rời khỏi"
    )
    val leaveGroup = Translation(
        "Leave group?",
        "Rời khỏi nhóm này không?"
    )
    val active = Translation(
        "Active",
        "Truy cập"
    )
    val discard = Translation(
        "Discard",
        "Bỏ"
    )
    val joinGroup = Translation(
        "Join group",
        "Tham gia nhóm"
    )
    val sendRequest = Translation(
        "Send request",
        "Gửi yêu cầu"
    )
    val cancelJoinRequest = Translation(
        "Cancel join request",
        "Hủy yêu cầu tham gia"
    )
    val stickers = Translation(
        "Stickers",
        "Hình dán"
    )
}

fun getString(string: Translation, language: String) = when (language) {
    "vi" -> string.vn
    "ru" -> string.ru
    else -> string.en
}

@Composable
private fun appString(string: Translation) = getString(string, LocalConfiguration.current.language)

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
    Configuration(application.language) {}
}
