package materialism

import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.*
import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

class Shader(type: Int, source: CharSequence) {
    val id: Int

    init {
        id = glCreateShader(type)
        assertNoGLError()
        glShaderSource(id, source)
        assertNoGLError()
        glCompileShader(id)
        assertNoGLError()

        checkStatus()
    }

    private fun checkStatus() {
        val status = glGetShaderi(id, GL_COMPILE_STATUS)
        if (status != GL_TRUE) {
            throw RuntimeException(glGetShaderInfoLog(id))
        }
    }

    fun delete() {
        glDeleteShader(id)
    }

    companion object {
        fun loadShader(type: Int, path: String): Shader {
            val builder = StringBuilder()

            try {
                FileInputStream(path).use({ `in` ->
                    BufferedReader(InputStreamReader(`in`)).use({ reader ->
                        var line = reader.readLine()
                        while (line != null) {
                            builder.append(line).append("\n")
                            line = reader.readLine()
                        }
                    })
                })
            } catch (ex: IOException) {
                throw RuntimeException("Failed to load a shader file!" + System.lineSeparator() + ex.message)
            }

            return Shader(type, builder.toString())
        }
    }
}
