@file:JsModule("date-fns")
@file:JsNonModule

package lib

import org.khronos.webgl.Uint8Array
import kotlin.js.Date

external fun formatDistanceToNow(date: Date, options: dynamic = definedExternally): String
