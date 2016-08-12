package materialism

import org.joml.Matrix4f
import org.lwjgl.BufferUtils.createFloatBuffer
import org.lwjgl.opengl.GL11.GL_FLOAT
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.GL30.glBindFragDataLocation
import java.util.*

class ShaderProgram {
    private val id: Int
    private val uniforms: HashMap<String, Int> = hashMapOf()

    init {
        id = glCreateProgram()
    }

    fun attachShader(shader: Shader) {
        glAttachShader(id, shader.id)
    }

    fun bindFragmentDataLocation(number: Int, name: CharSequence) {
        glBindFragDataLocation(id, number, name)
    }

    fun link() {
        glLinkProgram(id)
        checkStatus()
    }

    fun getAttributeLocation(name: CharSequence): Int {
        return glGetAttribLocation(id, name)
    }

    fun enableVertexAttribute(location: Int) {
        glEnableVertexAttribArray(location)
    }

    fun disableVertexAttribute(location: Int) {
        glDisableVertexAttribArray(location)
    }

    fun pointVertexAttribute(location: Int, size: Int, stride: Int, offset: Long) {
        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset)
    }

    fun setUniform(name: String, value: Matrix4f) {
        glUniformMatrix4fv(uniforms.get(name)!!, false, value.get(createFloatBuffer(16)))
    }

    fun setUniform(name: String, value: Int) {
        glUniform1i(uniforms.get(name)!!, value)
    }

    fun createUniform(name: String) {
        val location = glGetUniformLocation(id, name)
        if (location < 0) {
            throw RuntimeException("Could not find uniform: $name")
        }
        uniforms.put(name, location)
    }

    fun use() {
        glUseProgram(id)
    }

    fun checkStatus() {
        val status = glGetProgrami(id, GL_LINK_STATUS)
        if (status != GL_TRUE) {
            throw RuntimeException(glGetProgramInfoLog(id))
        }
    }

    fun delete() {
        glDeleteProgram(id)
    }
}
