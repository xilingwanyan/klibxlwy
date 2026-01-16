package org.lib.unit

fun String.color(colorCode: String) =
    "\u001B[0;${colorCode}m${this}\u001B[0m"
fun String.colorBold(colorCode: String) =
    "\u001B[1;${colorCode}m${this}\u001B[0m" // 实现颜色String
