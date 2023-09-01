import org.jetbrains.compose.web.attributes.AttrsScope
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement
import kotlin.random.Random

val String.notBlank get() = takeIf { it.isNotBlank() }

val <T> List<T>.notEmpty get() = takeIf { it.isNotEmpty() }

val Element.parents get() = let { element ->
    buildList<Element> {
        var parent: Element? = element
        while (parent != null) {
            add(parent)
            parent = parent.parentElement
        }
    }
}

fun IntRange.token() = joinToString("") { Random.nextInt(35).toString(36) }

fun AttrsScope<HTMLElement>.focusable() {
    tabIndex(1)

    onKeyDown {
        if (it.key == "Enter") {
            it.preventDefault()
            (it.target as HTMLElement).click()
        }
    }
}
