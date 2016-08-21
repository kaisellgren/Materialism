package materialism

import org.joml.Vector3f
import java.lang.Math.abs
import java.lang.Math.max

/**
 * Based on minutes.
 */
class DayNightCycle {
    var currentTime = 0
    var intensity = 0f
    val currentPosition = Vector3f(20f, 50f, 50f)

    val maxTime = 1000
    val cycleSpeed = 100
    val currentColor = Vector3f()
    val dayColor = Vector3f(255/255f, 246/255f, 216/255f)
    val nightColor = Vector3f(0/255f, 60/255f, 108/255f)

    val dayColorVec = Vector3f()
    val nightColorVec = Vector3f()

    fun update(dt: Float) {
        currentTime = (currentTime + cycleSpeed * dt).toInt() % maxTime

        // 0 = end of dark, start of day
        // 500 = end of day, start of dark

        val dayScale = if (currentTime >= 0 && currentTime <= 500) {
            (250 - abs(currentTime - 250)) / 250f
        } else {
            0f
        }

        val nightScale = if (currentTime >= 500 && currentTime <= 1000) {
            (250 - abs(currentTime - 750)) / 250f
        } else {
            0f
        }

        currentColor.set(dayColorVec.set(dayColor).mul(dayScale).add(nightColorVec.set(nightColor).mul(nightScale)))
        intensity = max(dayScale, 0.2f)

        // TODO: POSITION!
    }
}
