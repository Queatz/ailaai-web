@file:JsModule("date-fns")
@file:JsNonModule

package lib

import kotlin.js.Date

external fun formatDistanceToNow(date: Date, options: dynamic = definedExternally): String

external fun format(date: Date, format: String, options: dynamic = definedExternally): String

external fun addHours(date: Date, amount: Double): Date
external fun addDays(date: Date, amount: Double): Date
external fun addWeeks(date: Date, amount: Double): Date
external fun addMonths(date: Date, amount: Double): Date
external fun addYears(date: Date, amount: Double): Date
external fun previousSunday(date: Date): Date
external fun isToday(date: Date): Boolean
external fun isTomorrow(date: Date): Boolean
