package materialism

import org.lwjgl.opengl.GL30.*

class VertexArrayObject {
    val id: Int

    init {
        id = glGenVertexArrays()
        assertNoError()
    }

    fun bind() {
        glBindVertexArray(id)
        assertNoError()
    }

    fun delete() {
        glDeleteVertexArrays(id)
        assertNoError()
    }
}
