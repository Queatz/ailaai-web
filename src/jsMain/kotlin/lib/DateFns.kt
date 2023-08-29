@file:JsModule("date-fns")
@file:JsNonModule

package lib

import kotlin.js.Date

external fun formatDistanceToNow(date: Date, options: dynamic = definedExternally): String

external fun format(date: Date, format: String, options: dynamic = definedExternally): String
