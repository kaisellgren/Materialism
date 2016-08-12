package materialism

import org.lwjgl.glfw.GLFW.glfwGetTime

class Timer {
    var lastLoopTime: Double = 0.toDouble()
        private set

    private var timeCount: Float = 0.toFloat()
    private var fps: Int = 0
    private var fpsCount: Int = 0
    private var ups: Int = 0
    private var upsCount: Int = 0

    fun init() {
        lastLoopTime = time
    }

    val time: Double
        get() = glfwGetTime()

    val delta: Float
        get() {
            val time = time
            val delta = (time - lastLoopTime).toFloat()
            lastLoopTime = time
            timeCount += delta
            return delta
        }

    fun updateFPS() {
        fpsCount++
    }

    fun updateUPS() {
        upsCount++
    }

    fun update() {
        if (timeCount > 1f) {
            fps = fpsCount
            fpsCount = 0

            ups = upsCount
            upsCount = 0

            timeCount -= 1f
        }
    }

    fun getFPS(): Int {
        return if (fps > 0) fps else fpsCount
    }

    fun getUPS(): Int {
        return if (ups > 0) ups else upsCount
    }
}
