package org.klibxlwy.io

import kotlinx.coroutines.*

suspend fun next() {
    println()
    while (true) {
        for (i in charArrayOf('⠋', '⠙', '⠹', '⠼', '⠴', '⠦', '⠧', '⠏')) {
            TerminalBuffer.run() {
                up()
                hide()
            }
            print(i)
            TerminalBuffer.leftToEdge()
            TerminalBuffer.run() {
                down()
                show()
            }
            delay(100L)
        }
    }
}
