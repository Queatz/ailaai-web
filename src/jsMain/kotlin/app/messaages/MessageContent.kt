package app.messaages

import CornerDefault
import MemberAndPerson
import Message
import PaddingDefault
import Story
import androidx.compose.runtime.*
import app.AppStyles
import app.StickerItem
import app.softwork.routingcompose.Router
import baseUrl
import components.CardItem
import components.LinkifyText
import components.textContent
import http
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.browser.window
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import stories.StoryStyles

@Composable
fun MessageContent(message: Message, myMember: MemberAndPerson?) {
    val isMe = message.member == myMember?.member?.id

    val attachment = remember(message) {
        message.getAttachment()
    }

    when (attachment) {
        is PhotosAttachment -> {
            Div({
                classes(StoryStyles.contentPhotos)
            }) {
                attachment.photos?.forEach { photo ->
                    Div({
                        classes(StoryStyles.contentPhotosPhoto)
                        style {
                            display(DisplayStyle.InlineBlock)
                            backgroundColor(Styles.colors.background)
                            backgroundImage("url($baseUrl$photo)")
                            backgroundSize("cover")
                            backgroundPosition("center")
                            height(320.px)
                            width(320.px)
                            maxHeight(100.vw)
                            borderRadius(CornerDefault)
                            border(3.px, LineStyle.Solid, Color.white)
                        }
                    })
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
            // todo
        }
        is StoryAttachment -> {
            var story by remember {
                mutableStateOf<Story?>(null)
            }

            LaunchedEffect(Unit) {
                story = try {
                    http.get("$baseUrl/stories/${attachment.story!!}").body()
                } catch (e: Exception) {
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
