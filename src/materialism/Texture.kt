package materialism

import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12.GL_TEXTURE_MAX_LEVEL
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load
import java.nio.ByteBuffer

class Texture(width: Int, height: Int, buffer: ByteBuffer) {
    val id: Int

    init {
        id = glGenTextures()
        assertNoGLError()
        bind()
        assertNoGLError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        assertNoGLError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        assertNoGLError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        assertNoGLError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        assertNoGLError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAX_LEVEL, 8)
        assertNoGLError()

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        assertNoGLError()
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
        assertNoGLError()
    }

    companion object {
        fun fromPath(path: String): Texture {
            val w = createIntBuffer(1)
            val h = createIntBuffer(1)
            val comp = createIntBuffer(1)

            val image = stbi_load(path, w, h, comp, 4) ?: throw RuntimeException("Failed to load a texture file: $path (${stbi_failure_reason()})")
            return Texture(w.get(), h.get(), image)
        }
    }
}
