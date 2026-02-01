package org.klibxlwy.io

import kotlinx.coroutines.*
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import com.googlecode.lanterna.TerminalSize
import com.googlecode.lanterna.input.KeyStroke

const val CTRL = "\u001B["

@Suppress("NOTHING_TO_INLINE")
object Cursor {
    val position
        get() =
            TerminalBuffer.terminalCennet.getCursorPosition()
    // 调用TerminalBuffer的Terminal对象get position
    inline fun up(length: Int) =
        print("$CTRL${length}A")

    inline fun down(length: Int) =
        print("$CTRL${length}B")

    inline fun left(length: Int) =
        print("$CTRL${length}D")
    inline fun right(length: Int) =
        print("$CTRL${length}C")
    // 常规移动光标
    inline fun next(length: Int) =
        print("$CTRL${length}E")
    inline fun previous(length: Int) =
        print("$CTRL${length}F")
    // 行类移动光标

    inline fun up() =
        print("${CTRL}A")
    inline fun down() =
        print("${CTRL}B")
    inline fun left() =
        print("${CTRL}D")
    inline fun right() =
        print("${CTRL}C")

    inline fun next() =
        print("${CTRL}E")
    inline fun previous() =
        print("${CTRL}F")
    // 重载无参数

    inline fun goto(X: Int, Y: Int) =
        print("$CTRL${X};${Y}H")
    // goto移动
    inline fun leftToEdge() =
        print('\r')
    inline fun newLine() =
        print('\n')
    // 特殊
    inline fun cleanLeft() =
        print("${CTRL}1K")
    inline fun cleanRight() =
        print("${CTRL}K")
    inline fun cleanLine() =
        print("${CTRL}2K")
    inline fun cleanAll() =
        print("${CTRL}2J")
    // 清除操作

    inline fun save() =
        print("${CTRL}s")
    inline fun restore() =
        print("${CTRL}u")
    // 保存与恢复

    inline fun hide() =
        print("${CTRL}?25l")
    inline fun show() =
        print("${CTRL}?25h")
    // 隐藏与显示
}

object TerminalBuffer {
    private val terminalFactory = DefaultTerminalFactory()
    // 内部维护的Terminal对象
    // 可为null
    private var terminalCennetTrue: Terminal? = null
    // 暴露的Terminal对象
    val terminalCennet
        get(): Terminal =
            terminalCennetTrue ?: synchronized(this) {
                terminalCennetTrue ?: terminalFactory.createTerminal().also {
                    terminalCennetTrue = it
                }
            }
    // 更安全

    /*
    if (terminalCennetTrue == null) {
        terminalCennetTrue = terminalFactory.createTerminal()
        terminalCennetTrue!!
    } else {
        terminalCennetTrue!!
    }
     */

    // 弃用

    // 如果为null(close) 则自动create一个

    @Suppress("NOTHING_TO_INLINE")
    inline fun getSize(): TerminalSize = terminalCennet.getTerminalSize()

    fun close() {
        terminalCennetTrue?.let {
            it.close()
            terminalCennetTrue = null
        } ?: Unit
    }
    // 关闭Terminal对象连接
    inline fun <T> runprivateMode(block: () -> T): T {
        try {
            terminalCennet.enterPrivateMode()
            return block()
        } finally {
            terminalCennet.exitPrivateMode()
        }
    }
    // 在private模式下运行
    // 自动关闭

    @Suppress("NOTHING_TO_INLINE")
    inline fun pollInput() = terminalCennet.pollInput()

    suspend fun readInput(delay: Long = 10L): KeyStroke {
        while (true) {
            val key = pollInput()
            if (key != null) {
                return key
            }
            delay(delay)
        }
    }
    // 为古老的readInput()阻塞版重写挂起版
}

