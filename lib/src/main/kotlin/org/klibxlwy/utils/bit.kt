@file:Suppress("NOTHING_TO_INLINE")

package org.klibxlwy.utils

inline fun Int.bitGetTrue(index: Int): Int =
    1 shl index
// 0000 0001 <- index
inline fun Int.bitGetFalse(index: Int): Int =
    (1 shl index).inv()
/*
0000 0001 <- index
1111 1110 inv()
 */
inline fun Int.bitSetTrue(index: Int): Int =
    this or bitGetTrue(index)
/*
0110 1101 this, index == 1
0000 0010 bitGetTrue()
0110 1111 or
 */
inline fun Int.bitSetFalse(index: Int): Int =
    this and bitGetFalse(index)
/*
0011 1010 this, index == 3
1111 0111 bitGetFalse()
0011 0010 amd
 */
inline fun Int.bitGet(index: Int): Boolean =
    this and bitGetTrue(index) != 0
/*
1110 0001 this, index == 0
0000 0001 bitGetTrue()
0000 0001 and
 */
fun Int.bitToString(): String {
    val strBuilder = StringBuilder(203)
    for (i in 0..31) {
        val bitGet = bitGet(i)
        if (bitGet == true) {
            strBuilder.append("true  ")
        } else {
            strBuilder.append("false ")
        }
        val iPlus1 = i + 1
        if (iPlus1 % 8 == 0) {
            strBuilder.append('\n')
        } else if (iPlus1 % 4 == 0) {
            strBuilder.append("  ")
        }
    }
    return strBuilder.toString()
}
fun Int.bitToStringBinary(): String {
    val strBuilder = StringBuilder(39)
    for (i in 0..31) {
        val bitGet = bitGet(i)
        if (bitGet == true) {
            strBuilder.append(1)
        } else {
            strBuilder.append(0)
        }
        val iPlus1 = i + 1
        if (iPlus1 % 8 == 0) {
            strBuilder.append('\n')
        } else if (iPlus1 % 4 == 0) {
            strBuilder.append(" ")
        }
    }
    return strBuilder.toString()
}


// Long
inline fun Long.bitGetTrue(index: Int): Long =
    1L shl index
// 0000 0001 <- index
inline fun Long.bitGetFalse(index: Int): Long =
    (1L shl index).inv()
/*
0000 0001 <- index
1111 1110 inv()
 */
inline fun Long.bitSetTrue(index: Int): Long =
    this or bitGetTrue(index)
/*
0110 1101 this, index == 1
0000 0010 bitGetTrue()
0110 1111 or
 */
inline fun Long.bitSetFalse(index: Int): Long =
    this and bitGetFalse(index)
/*
0011 1010 this, index == 3
1111 0111 bitGetFalse()
0011 0010 amd
 */
inline fun Long.bitGet(index: Int): Boolean =
    this and bitGetTrue(index) != 0L
/*
1110 0001 this, index == 0
0000 0001 bitGetTrue()
0000 0001 and
 */
fun Long.bitToString(): String {
    val strBuilder = StringBuilder(407)
    for (i in 0..63) {
        val bitGet = bitGet(i)
        if (bitGet == true) {
            strBuilder.append("true  ")
        } else {
            strBuilder.append("false ")
        }
        val iPlus1 = i + 1
        if (iPlus1 % 8 == 0) {
            strBuilder.append('\n')
        } else if (iPlus1 % 4 == 0) {
            strBuilder.append("  ")
        }
    }
    return strBuilder.toString()
}
fun Long.bitToStringBinary(): String {
    val strBuilder = StringBuilder(87)
    for (i in 0..63) {
        val bitGet = bitGet(i)
        if (bitGet == true) {
            strBuilder.append(1)
        } else {
            strBuilder.append(0)
        }
        val iPlus1 = i + 1
        if (iPlus1 % 8 == 0) {
            strBuilder.append('\n')
        } else if (iPlus1 % 4 == 0) {
            strBuilder.append(" ")
        }
    }
    return strBuilder.toString()
}
