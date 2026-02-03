package org.klibxlwy.io

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import sun.misc.*
import org.jline.terminal.*
import org.jline.utils.NonBlockingReader

const val CTRL = "\u001B["

@Suppress("NOTHING_TO_INLINE")
object Cursor {
    inline fun getPosition() =
        Term.terminal.getCursorPositionRe()
    // 调用Term的Terminal对象get position

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

    inline fun goto(x: Int, y: Int) =
        print("$CTRL${x};${y}H")
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

object Term {
    @Volatile
    private var terminalTrue: Terminal? = null

    @Suppress("NOTHING_TO_INLINE")
    private inline fun initTerminal(): Terminal =
        TerminalBuilder
            .builder()
            .system(true)
            .build()
            .also {
                terminalTrue = it
            }

    val terminal
        get(): Terminal =
            terminalTrue ?: initTerminal()

    suspend fun getTerminalAsync(): Terminal {
        val mutex = Mutex()
        return autoNullWithAsync(terminalTrue, mutex) {
            initTerminal()
        }
    }
    // 缓存 获取 异步的terminal对象

    @Suppress("NOTHING_TO_INLINE")
    private inline fun initSize(): Size =
        fetchSize().also {
            sizeTrue = it
            listenWinch()
        }

    private var sizeTrue: Size? = null

    val size
        get(): Size =
            sizeTrue ?: initSize()
    suspend fun getSizeAsync(): Size {
        val mutex = Mutex()
        return autoNullWithAsync(sizeTrue, mutex) {
            initSize()
        }
    }
    // 缓存 获取 异步的size对象
    // -val position = terminal.getCursorPosition(IntConsumer {n -> })
    fun updateSize() {
        sizeTrue = fetchSize()
    }
    fun updateSizeAndStart() {
        sizeTrue = fetchSize().also {
            updateStart(it)
        }
    }
    fun fetchSize(): Size =
        terminal.getSize()

    private var startTrue: Position? = null
    val start
        get(): Position =
            startTrue ?: Position(1, size.getRows()).also {
                startTrue = it
            }
    suspend fun getStartAsync() {
        val mutex = Mutex()
        autoNullWithAsync(startTrue, mutex) {
            fetchStart().also {
                startTrue = it
            }
        }
    }
    // 缓存 获取 异步startPosition(左下角)

    fun updateStart() {
        startTrue = fetchStart()
    }
    fun updateStart(size: Size) {
        startTrue = fetchStart(size)
    }
    fun fetchStart(): Position =
        Position(1, size.getRows())
    fun fetchStart(size: Size): Position =
        Position(1, size.getRows())


    fun listenWinch() {
        Signal.handle(Signal("WINCH")) {
            updateSizeAndStart()
        }
    }
    fun unlistenWinch() {
        Signal.handle(
            Signal("WINCH"),
            SignalHandler.SIG_DFL
        )
    }

    // 监听WINCH信号以获得终端大小变化
    fun close() {
        terminalTrue?.let {
            it.close()
            terminalTrue = null
            sizeTrue = null
            unlistenWinch()
        }
    }
    // 关闭terminal对象连接

    private val closeHookThread: Thread =
        Thread {
            Term.close()
        }
    private var closeHook: Boolean = false
    fun addCloseHook() {
        if (closeHook == true) {
            return
        }
        Runtime.getRuntime().addShutdownHook(
            closeHookThread
        )
        closeHook = true
    }
    fun removeCloseHook() {
        if (closeHook == false) {
            return
        }
        Runtime.getRuntime().removeShutdownHook(
            closeHookThread
        )
        closeHook = false
    }

    private var restoreModeHook: Boolean = false
    private val restoreModeHookThread: Thread =
        Thread {
            terminal.attributes = startMode
        }
    private var startMode: Attributes? = null
    fun addRestoreModeHook() {
        if (startMode == null) {
            startMode = terminal.attributes
            addRestoreModeHook()
        }
        if (restoreModeHook == true) (
            return
        )
        Runtime.getRuntime().addShutdownHook(
            restoreModeHookThread
        )
        restoreModeHook = true
    }

    fun removeRestoreModeHook() {
        if (restoreModeHook == false) {
            return
        }
        Runtime.getRuntime().removeShutdownHook(
            restoreModeHookThread
        )
        restoreModeHook = false
    }
    inline fun <T> withRawMode(block: () -> T): T {
        val attributes = terminal.enterRawMode()
        try {
            return block()
        } finally {
            terminal.setAttributes(attributes)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun reader() = terminal.reader()

    @Suppress("NOTHING_TO_INLINE")
    inline fun writer() = terminal.writer()
}

suspend fun NonBlockingReader.readDelay(delay: Long = 10L): Int {
    while (true) {
        val out = this.read(0)
        if (out != -2) {
            return out
        }
        delay(delay)
    }
}

// reader.read的非阻塞挂起实现

fun Terminal.getCursorPositionRe(retry: Int = 0): Position {
    if (retry >= 5) {
        throw KotlinNullPointerException("can' getCursorPosition")
    }
    var retryPrivate = retry
    val reader = this.reader()
    val writer = this.writer()
    val strBuilder = StringBuilder()
    Term.withRawMode {
        writer.print("${CTRL}6n")
        writer.flush()

        while (true) {
            val i = reader.read().toChar()
            if (i == 'R') {
                break
            }
            strBuilder.append(i)
        }
    }
    val str = strBuilder.toString()

    val splitIndex1: Int  = str.indexOf(CTRL)
    val splitIndex2: Int  = str.indexOf(';')

    if (splitIndex1 == -1 ||
        splitIndex2 == -1
    ) {
        retryPrivate++
        return this.getCursorPositionRe(retryPrivate)
    }

    val p1: String = str.substring(splitIndex1 + 2, splitIndex2)
    val p2: String = str.substring(splitIndex2 + 1)

    val y = p1.toIntOrNull()
    val x = p2.toIntOrNull()
    return if (x == null || y == null) {
        retryPrivate++
        this.getCursorPositionRe(retryPrivate)
    } else {
        Position(x, y)
    }
}

fun Terminal.getSizeRe(retry: Int = 0): Position {
    // TODO: ("build this")
    TODO("build this")
}

// TODO: rebuild class
data class Position(val x: Int, val y: Int) {
    override fun toString() = "[${x};${y}]"
    fun goto() = Cursor.goto(x, y)
}

data class MutablePosition(var x: Int, var y: Int) {
    override fun toString() = "[${x};${y}]"
    fun goto() = Cursor.goto(x, y)
}

suspend inline fun <T : Any> autoNullWithAsync(
    value: T?,
    mutex: Mutex,
    block: () -> T
): T =
    value ?: mutex.withLock(mutex) {
        value ?: block()
    }
// 自动处理null的安全异步实现

suspend inline fun <T : Any> withMutex(
    mutex: Mutex,
    block: () -> T
): T {
    return mutex.withLock() {
        block()
    }
}

inline fun runTimeTest(
    block: () -> Unit
): Long {
    val start = System.nanoTime()
    block()
    return (System.nanoTime() - start) / 1_000_000

}
