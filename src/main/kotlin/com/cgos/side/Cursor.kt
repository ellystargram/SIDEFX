package com.cgos.side

class Cursor {
    var curY: Long = 0
    var curX: Long = 0
    var isVisible: Boolean = true
    var isBlinking: Boolean = true
    var blinkTimer:Long = 0
    val blinkThread: Thread = Thread {
        try {
            while (true) {
                if (blinkTimer >= 500) {
                    isVisible = !isVisible
                    blinkTimer = 0
                }
                Thread.sleep(10)
                if (isBlinking) {
                    blinkTimer += 10
                }
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    init {
        curY = 0
        curX = 0
        isVisible = false
        isBlinking = false
        blinkTimer = 0
        blinkThread.start()
    }

    fun setBlinkingStatus(target: Boolean) {
        isBlinking = target
        blinkTimer=0
        isVisible = true
    }
}