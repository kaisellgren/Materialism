package materialism

import org.lwjgl.BufferUtils.createFloatBuffer
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW

class Mesh(data: Array<Float>) {
    val vao: VertexArrayObject
    val vbo: VertexBufferObject
    val vertexCount: Int

    init {
        vao = VertexArrayObject()
        vao.bind()

        vertexCount = data.size / 3 // TODO: ?

        val vertices = createFloatBuffer(data.size)
        vertices.put(data.toFloatArray())
        vertices.flip()

        vbo = VertexBufferObject()
        vbo.bind(GL_ARRAY_BUFFER)
        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)
    }

    fun render() {
        //vao.bind()
        //shaderProgram.enableVertexAttribute(shaderProgram.getAttributeLocation("position"))
        //shaderProgram.enableVertexAttribute(shaderProgram.getAttributeLocation("color"))
        glDrawArrays(GL_TRIANGLES, 0, vertexCount)
    }

    fun delete() {
        vbo.delete()
        vao.delete()
    }
}
