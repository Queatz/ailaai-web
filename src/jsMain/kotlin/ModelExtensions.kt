import com.queatz.db.Card

fun Card.isMine(me: String?) = person == me || collaborators?.any { it == me } == true
