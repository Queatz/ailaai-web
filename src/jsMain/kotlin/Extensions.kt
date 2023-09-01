import org.w3c.dom.Element

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
