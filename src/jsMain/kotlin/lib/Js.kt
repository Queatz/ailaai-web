package lib

val systemTimezone get() = js("Intl.DateTimeFormat().resolvedOptions().timeZone") as String
