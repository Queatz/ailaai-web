package app

import Sticker
import StickerPack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import api
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

val allStickerPacks = MutableStateFlow<List<StickerPack>>(emptyList())

@Composable
fun StickersTray(onSticker: (Sticker) -> Unit) {
    val stickerPacks by allStickerPacks.collectAsState()

    LaunchedEffect(Unit) {
        api.stickerPacks {
            allStickerPacks.value = it
        }
    }

    if (stickerPacks.isNotEmpty()) {
        Div({
            style {
                padding(1.cssRem)
            }
        }) {
            stickerPacks.forEach { stickerPack ->
                Div({
                    style {
                        fontWeight("bold")
                        marginBottom(.5.cssRem)
                    }
                }) {
                    Text(stickerPack.name ?: "Stickers")
                }
                Div({
                    style {
                        display(DisplayStyle.Flex)
                        marginBottom(1.cssRem)
                        overflowX("auto")
                        property("scrollbar-width", "none")
                    }
                }) {
                    stickerPack.stickers?.forEach { sticker ->
                        StickerItem(sticker.photo!!, title = "Send sticker") {
                            onSticker(sticker)
                        }
                    }
                }
            }
        }
    } else {
        Div({
            style {
                height(100.percent)
                display(DisplayStyle.Flex)
                alignItems(AlignItems.Center)
                justifyContent(JustifyContent.Center)
                opacity(.5)
            }
        }) {
            Text("You haven't any stickers yet!")
        }
    }
}
