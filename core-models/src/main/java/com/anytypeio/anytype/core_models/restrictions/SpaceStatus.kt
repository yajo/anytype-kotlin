package com.anytypeio.anytype.core_models.restrictions

enum class SpaceStatus(val code: Number) {
    UNKNOWN(0),
    LOADING(1),
    OK(2),
    MISSING(3),
    ERROR(4),
    REMOTE_WAITING_DELETION(5),
    REMOTE_DELETED(6),
    SPACE_DELETED(7),
}