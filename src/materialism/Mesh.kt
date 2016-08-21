package materialism

import org.lwjgl.BufferUtils.createFloatBuffer
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.glDrawArrays
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL15.GL_STATIC_DRAW

class Mesh(data: Array<Array<Float>>) {
    val vao: VertexArrayObject
    val vbo: VertexBufferObject
    val vertexCount: Int

    init {
        vao = VertexArrayObject()
        vao.bind()

        var dataSize = 0
        for (floatData in data) {
            dataSize += floatData.size
        }

        vertexCount = dataSize / 3 // TODO: ?

        val vertices = createFloatBuffer(dataSize)
        for (floats in data) {
            vertices.put(floats.toFloatArray())
        }
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
