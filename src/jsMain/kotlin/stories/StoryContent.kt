package stories

import Person
import Story
import json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
sealed class StoryContent {
    object Divider : StoryContent()
    class Title(var title: String, val id: String) : StoryContent()
    class Authors(var publishDate: String?, var authors: List<Person>) : StoryContent()
    @Serializable
    class Section(var section: String) : StoryContent()
    @Serializable
    class Text(var text: String) : StoryContent()
    @Serializable
    class Cards(var cards: List<String>) : StoryContent()
    @Serializable
    class Photos(var photos: List<String>, var aspect: Float = 0.75f) : StoryContent()
    @Serializable
    class Audio(var audio: String) : StoryContent()
}

fun JsonObject.toStoryContent(): StoryContent? = get("content")?.jsonObject?.let { content ->
    when (get("type")?.jsonPrimitive?.content) {
        "section" -> json.decodeFromJsonElement<StoryContent.Section>(content)
        "text" -> json.decodeFromJsonElement<StoryContent.Text>(content)
        "cards" -> json.decodeFromJsonElement<StoryContent.Cards>(content)
        "photos" -> json.decodeFromJsonElement<StoryContent.Photos>(content)
        "audio" -> json.decodeFromJsonElement<StoryContent.Audio>(content)
        else -> null
    }
}

fun Story.contents() = json.parseToJsonElement(content ?: "[]").jsonArray.toList().mapNotNull { part ->
    part.jsonObject.toStoryContent()
}

fun Story.textContent(): String = contents().mapNotNull {
    when (it) {
        is StoryContent.Title -> it.title.takeIf { it.isNotBlank() }
        is StoryContent.Section -> it.section.takeIf { it.isNotBlank() }
        is StoryContent.Text -> it.text.takeIf { it.isNotBlank() }
        else -> null
    }
}.joinToString("\n")

fun Story.full(): List<StoryContent> = contents().let { parts ->
    listOf(
        StoryContent.Title(title ?: "", id!!),
        StoryContent.Authors(publishDate, authors ?: emptyList()),
    ) + parts
}
