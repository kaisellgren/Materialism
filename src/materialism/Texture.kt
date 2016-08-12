package materialism

import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.opengl.GL11.*
import org.lwjgl.stb.STBImage.stbi_failure_reason
import org.lwjgl.stb.STBImage.stbi_load

class Texture(val path: String) {
    val id: Int
    val width: Int
    val height: Int

    init {
        id = glGenTextures()
        assertNoError()
        bind()
        assertNoError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        assertNoError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        assertNoError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        assertNoError()
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)
        assertNoError()

        val w = createIntBuffer(1)
        val h = createIntBuffer(1)
        val comp = createIntBuffer(1)

        val image = stbi_load(path, w, h, comp, 4) ?: throw RuntimeException("Failed to load a texture file: $path (${stbi_failure_reason()})")
        width = w.get()
        height = h.get()

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image)
        assertNoError()
    }

    fun bind() {
        glBindTexture(GL_TEXTURE_2D, id)
        assertNoError()
    }
}
