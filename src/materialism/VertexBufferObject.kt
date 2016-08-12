package materialism

import org.lwjgl.opengl.GL15.*
import java.nio.FloatBuffer
import java.nio.IntBuffer

class VertexBufferObject {
    val id: Int

    init {
        id = glGenBuffers()
        assertNoError()
    }

    fun bind(target: Int) {
        glBindBuffer(target, id)
        assertNoError()
    }

    fun uploadData(target: Int, data: FloatBuffer, usage: Int) {
        glBufferData(target, data, usage)
        assertNoError()
    }

    fun uploadData(target: Int, size: Long, usage: Int) {
        glBufferData(target, size, usage)
        assertNoError()
    }

    fun uploadSubData(target: Int, offset: Long, data: FloatBuffer) {
        glBufferSubData(target, offset, data)
        assertNoError()
    }

    fun uploadData(target: Int, data: IntBuffer, usage: Int) {
        glBufferData(target, data, usage)
        assertNoError()
    }

    fun delete() {
        glDeleteBuffers(id)
        assertNoError()
    }
}
