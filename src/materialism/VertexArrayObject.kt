package materialism

import org.lwjgl.opengl.GL30.*

class VertexArrayObject {
    val id: Int

    init {
        id = glGenVertexArrays()
        assertNoGLError()
    }

    fun bind() {
        glBindVertexArray(id)
        assertNoGLError()
    }

    fun delete() {
        glDeleteVertexArrays(id)
        assertNoGLError()
    }
}
