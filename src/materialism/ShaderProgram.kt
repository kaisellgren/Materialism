package materialism

import materialism.shader.Material
import materialism.shader.PointLight
import org.joml.Matrix4f
import org.joml.Vector3f
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
        if (id < 0) {
            throw RuntimeException("Could not create shader program")
        }
    }

    fun attachShader(shader: Shader) {
        glAttachShader(id, shader.id)
        assertNoGLError()
    }

    fun bindFragmentDataLocation(number: Int, name: CharSequence) {
        glBindFragDataLocation(id, number, name)
        assertNoGLError()
    }

    fun link() {
        glLinkProgram(id)
        checkStatus()
    }

    fun getAttributeLocation(name: CharSequence): Int {
        val location = glGetAttribLocation(id, name)
        if (location < 0) {
            System.err.println("Could not find attribute location: $name")
        }
        return location
    }

    fun enableVertexAttribute(location: Int) {
        glEnableVertexAttribArray(location)
        assertNoGLError()
    }

    fun disableVertexAttribute(location: Int) {
        glDisableVertexAttribArray(location)
        assertNoGLError()
    }

    fun pointVertexAttribute(location: Int, size: Int, stride: Int, offset: Long) {
        glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset)
        assertNoGLError()
    }

    fun setUniform(name: String, value: Vector3f) {
        glUniform3f(uniforms.get(name)!!, value.x, value.y, value.z)
        assertNoGLError()
    }

    fun setUniform(name: String, value: Float) {
        glUniform1f(uniforms.get(name)!!, value)
        assertNoGLError()
    }

    fun setUniform(name: String, value: Matrix4f) {
        glUniformMatrix4fv(uniforms.get(name)!!, false, value.get(createFloatBuffer(16)))
        assertNoGLError()
    }

    fun setUniform(name: String, value: Int) {
        glUniform1i(uniforms.get(name)!!, value)
        assertNoGLError()
    }

    fun createUniform(name: String) {
        val location = glGetUniformLocation(id, name)
        if (location < 0) {
            System.err.println("Could not find uniform: $name")
        }
        uniforms.put(name, location)
    }

    fun createPointLightUniform(name: String) {
        createUniform("$name.color")
        createUniform("$name.position")
        createUniform("$name.intensity")
        createUniform("$name.att.constant")
        createUniform("$name.att.linear")
        createUniform("$name.att.exponent")
    }

    fun createMaterialUniform(name: String) {
        createUniform("$name.reflectance")
    }

    fun setUniform(name: String, pointLight: PointLight) {
        setUniform(name + ".color", pointLight.color)
        setUniform(name + ".position", pointLight.position)
        setUniform(name + ".intensity", pointLight.intensity)
        setUniform(name + ".att.constant", pointLight.att.constant)
        setUniform(name + ".att.linear", pointLight.att.linear)
        setUniform(name + ".att.exponent", pointLight.att.exponent)
        assertNoGLError()
    }

    fun setUniform(name: String, material: Material) {
        setUniform(name + ".reflectance", material.reflectance)
        assertNoGLError()
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
