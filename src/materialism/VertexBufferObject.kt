package materialism

import org.lwjgl.opengl.GL15.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class VertexBufferObject {
    val id: Int

    init {
        id = glGenBuffers()
        assertNoGLError()
    }

    fun bind(target: Int) {
        glBindBuffer(target, id)
        assertNoGLError()
    }

    fun uploadData(target: Int, data: FloatBuffer, usage: Int) {
        glBufferData(target, data, usage)
        assertNoGLError()
    }

    fun uploadData(target: Int, size: Long, usage: Int) {
        glBufferData(target, size, usage)
        assertNoGLError()
    }

    fun uploadSubData(target: Int, offset: Long, data: FloatBuffer) {
        glBufferSubData(target, offset, data)
        assertNoGLError()
    }

    fun uploadData(target: Int, data: IntBuffer, usage: Int) {
        glBufferData(target, data, usage)
        assertNoGLError()
    }

    fun delete() {
        glDeleteBuffers(id)
        assertNoGLError()
    }
}
