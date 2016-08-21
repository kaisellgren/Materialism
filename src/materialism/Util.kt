package materialism

import org.lwjgl.opengl.GL11.GL_NO_ERROR
import org.lwjgl.opengl.GL11.glGetError

fun assertNoGLError() {
    val code = glGetError()
    if (code != GL_NO_ERROR) {
        throw RuntimeException("OpenGL error: $code")
    }
}

fun logGLError() {
    val code = glGetError()
    if (code != GL_NO_ERROR) {
        println("OpenGL error: $code")
    }
}

fun logError(error: String) {
    System.err.println(error)
}

fun clamp(value: Float, min: Float, max: Float): Float {
    return Math.max(min, Math.min(max, value))
}
