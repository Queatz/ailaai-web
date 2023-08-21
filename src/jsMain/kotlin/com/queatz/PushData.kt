package com.queatz

import Card
import Group
import Message
import Person
import kotlinx.serialization.Serializable

enum class PushAction {
    Message,
    Collaboration
}

@Serializable
data class PushData(
    val action: PushAction? = null,
    val data: PushDataData? = null,
)

@Serializable
sealed class PushDataData

@Serializable
data class MessagePushData(
    val group: Group,
    val person: Person,
    val message: Message,
) : PushDataData()

@Serializable
data class CollaborationPushData(
    val person: Person,
    val card: Card,
    val event: CollaborationEvent,
    val data: CollaborationEventData,
) : PushDataData()

@Serializable
data class CollaborationEventData (
    val card: Card? = null,
    val person: Person? = null,
    val details: CollaborationEventDataDetails? = null
)

enum class CollaborationEvent {
    AddedPerson,
    RemovedPerson,
    AddedCard,
    RemovedCard,
    UpdatedCard,
}

enum class CollaborationEventDataDetails {
    Photo,
    Video,
    Conversation,
    Name,
    Location,
}
