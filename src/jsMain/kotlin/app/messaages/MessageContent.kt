package app.messaages

import CornerDefault
import MemberAndPerson
import Message
import PaddingDefault
import Story
import androidx.compose.runtime.*
import app.AppStyles
import app.StickerItem
import application
import baseUrl
import components.CardItem
import components.LinkifyText
import components.textContent
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import stories.StoryStyles
import kotlin.js.Date

@Composable
fun MessageContent(message: Message, myMember: MemberAndPerson?, isReply: Boolean = false) {
    val isMe = message.member == myMember?.member?.id

    val attachment = remember(message) {
        message.getAttachment()
    }

    var reply by remember {
        mutableStateOf<Message?>(null)
    }

    LaunchedEffect(attachment) {
        reply = null

        val replyAttachment = message.getAllAttachments().firstNotNullOfOrNull { it as? ReplyAttachment }

        if (isReply || replyAttachment == null) {
            return@LaunchedEffect
        }

        try {
            reply = http.get("$baseUrl/messages/${replyAttachment.message!!}") {
                contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                bearerAuth(application.bearer)
            }.body()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    Div({
        style {
            display(DisplayStyle.Flex)
            flexDirection(FlexDirection.Column)
            if (isMe) {
                alignItems(AlignItems.FlexEnd)
            } else {
                alignItems(AlignItems.FlexStart)
            }
        }
        title(message.createdAt?.let { Date(it) }.toString())
    }) {
        reply?.let { reply ->
            Div({
                classes(
                    listOf(AppStyles.messageReply) + if (isMe) {
                        listOf(AppStyles.myMessageReply)
                    } else {
                        emptyList()
                    }
                )
            }) {
                Div({
                    classes("material-symbols-outlined")
                    style {
                        position(Position.Absolute)
                        if (isMe) {
                            top(PaddingDefault / 4)
                            right(PaddingDefault / 4)
                        } else {
                            top(PaddingDefault / 4)
                            left(PaddingDefault / 4)
                        }
                        opacity(.75)
                        fontSize(12.px)
                    }
                }) {
                    Text("reply")
                }
                MessageContent(reply, myMember, isReply = true)
            }
        }

        when (attachment) {
            is PhotosAttachment -> {
                Div({
                    classes(StoryStyles.contentPhotos)
                }) {
                    attachment.photos?.forEach { photo ->
                        Img(src = "$baseUrl$photo") {
                            classes(StoryStyles.contentPhotosPhoto)
                            style {
                                backgroundColor(Styles.colors.background)
                                height(320.px)
//                                width(320.px)
                                maxHeight(100.vw)
                                borderRadius(CornerDefault)
                                border(3.px, LineStyle.Solid, Color.white)
                                cursor("pointer")
                            }
                            onClick {
                                window.open("$baseUrl$photo", target = "_blank")
                            }
                        }
                    }
                }
            }

            is AudioAttachment -> {
                Audio({
                    attr("controls", "")
                    style {
                        borderRadius(1.cssRem)
                    }
                }) {
                    Source({
                        attr("src", "$baseUrl${attachment.audio}")
                        attr("type", "audio/mp4")
                    })
                }
            }

            is VideoAttachment -> {
                // todo
            }

            is CardAttachment -> {
                CardItem(attachment.card!!, openInNewWindow = true) {
                    maxWidth(320.px)
                }
            }

            is ReplyAttachment -> {

            }

            is StoryAttachment -> {
                var story by remember {
                    mutableStateOf<Story?>(null)
                }

                LaunchedEffect(Unit) {
                    story = try {
                        http.get("$baseUrl/stories/${attachment.story!!}") {
                            contentType(ContentType.Application.Json.withCharset(Charsets.UTF_8))
                            bearerAuth(application.bearer)
                        }.body()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        null
                    }
                }

                story?.let { story ->
                    Div({
                        style {
                            borderRadius(CornerDefault)
                            backgroundColor(Color.white)
                            property("box-shadow", "rgba(0, 0, 0, 0.125) 1px 1px 4px")
                            padding(PaddingDefault)
                            cursor("pointer")
                            overflow("hidden")
                            maxWidth(36.cssRem)
                        }

                        onClick {
                            window.open("/story/${story.url ?: story.id}", target = "_blank")
                        }
                    }) {
                        Div({
                            style {
                                marginBottom(.5.cssRem)
                                fontSize(24.px)
                            }
                        }) {
                            Text(story.title ?: "New story")
                        }
                        Div({
                            style {
                                marginBottom(.5.cssRem)
                                color(Styles.colors.secondary)
                                fontSize(16.px)
                            }
                        }) {
                            Text("${if (story.publishDate != null) "Published" else "Draft"} by ${story.authors?.joinToString { it.name ?: "Someone" }}")
                        }
                        Div({
                            style {
                                marginBottom(.5.cssRem)
                                overflow("hidden")
                                property("text-overflow", "ellipsis")
                                whiteSpace("nowrap")
                            }
                        }) {
                            Text(story.textContent())
                        }
                    }
                }
            }

            is StickerAttachment -> {
                StickerItem(attachment.photo!!, 96.px) {}
            }
        }

        message.text?.let { text ->
            Div({
                classes(
                    listOf(AppStyles.messageItem) + if (isMe) {
                        listOf(AppStyles.myMessage)
                    } else {
                        emptyList()
                    }
                )
            }) {
                LinkifyText(text)
            }
        }
    }
}
