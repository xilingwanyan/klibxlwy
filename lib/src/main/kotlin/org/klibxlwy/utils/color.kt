package org.klibxlwy.utils

// 实现颜色String

fun String.withColor(colorCode: String) =
    "\u001B[0;${colorCode}m${this}\u001B[0m"
fun String.withColorBold(colorCode: String) =
    "\u001B[1;${colorCode}m${this}\u001B[0m"

