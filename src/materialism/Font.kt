package materialism

import org.lwjgl.BufferUtils
import java.awt.Color
import java.awt.Font
import java.awt.RenderingHints.KEY_ANTIALIASING
import java.awt.RenderingHints.VALUE_ANTIALIAS_ON
import java.awt.geom.AffineTransform
import java.awt.image.AffineTransformOp
import java.awt.image.AffineTransformOp.TYPE_NEAREST_NEIGHBOR
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.lang.Math.max
import java.util.*

class Font(font: Font, useAntiAlias: Boolean) {
    val fontHeight: Int
    val texture: Texture
    val glyphs: HashMap<Char, Glyph> = hashMapOf()

    init {
        var imageWidth = 0
        var imageHeight = 0
        for (i in 32..256) {
            if (i != 127) {
                val c = i.toChar()
                val ch = createCharImage(font, c, true)
                if (ch != null) {
                    imageWidth += ch.width
                    imageHeight = max(imageHeight, ch.height)
                }
            }
        }
        fontHeight = imageHeight

        var image = BufferedImage(imageWidth, imageHeight, TYPE_INT_ARGB)
        val g = image.createGraphics()

        var x = 0
        for (i in 32..256) {
            if (i != 127) {
                val c = i.toChar()
                val charImage = createCharImage(font, c, useAntiAlias)
                if (charImage != null) {
                    val charWidth = charImage.width
                    val charHeight = charImage.height

                    val ch = Glyph(charWidth, charHeight, x, image.height - charHeight)
                    g.drawImage(charImage, x, 0, null)
                    x += ch.width
                    glyphs.put(c, ch)
                }
            }
        }

        val transform = AffineTransform.getScaleInstance(1.0, -1.0)
        transform.translate(0.0, -image.height.toDouble())
        val operation = AffineTransformOp(transform, TYPE_NEAREST_NEIGHBOR)
        image = operation.filter(image, null)

        val width = image.width
        val height = image.height
        val pixels = IntArray(width * height)
        image.getRGB(0, 0, width, height, pixels, 0, width)

        val buffer = BufferUtils.createByteBuffer(width * height * 4)
        for (i in 0..height) {
            for (j in 0..width) {
                val pixel = pixels[i * width + j]
                // RGBA:
                buffer.put(pixel.shr(16).and(0xff).toByte())
                buffer.put(pixel.shr(8).and(0xff).toByte())
                buffer.put(pixel.and(0xff).toByte())
                buffer.put(pixel.shr(24).and(0xff).toByte())
            }
        }
        buffer.flip()

        texture = Texture(width, height, buffer)
    }

    fun createCharImage(font: Font, c: Char, antiAlias: Boolean): BufferedImage? {
        var image = BufferedImage(1, 1, TYPE_INT_ARGB)
        var g = image.createGraphics()
        if (antiAlias) {
            g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
        }
        g.font = font
        val metrics = g.fontMetrics
        g.dispose()

        val charWidth = metrics.charWidth(c)
        val charHeight = metrics.height

        if (charWidth == 0) {
            return null
        }

        image = BufferedImage(charWidth, charHeight, TYPE_INT_ARGB)
        g = image.createGraphics()
        if (antiAlias) {
            g.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON)
        }
        g.font = font
        g.paint = Color.WHITE
        g.drawString(c.toString(), 0, metrics.ascent)
        g.dispose()
        return image
    }

    fun drawText(text: CharSequence, x: Float = 0f, y: Float = 0f, c: Color = Color.WHITE) {
        val textHeight = getHeight(text)

        var drawX = x
        var drawY = y
        if (textHeight > fontHeight) {
            drawY += textHeight - fontHeight
        }

        texture.bind()
        //renderer.begin()
        for (i in 0..text.length - 1) {
            val ch = text[i]
            if (ch == '\n') {
                drawY -= fontHeight
                drawX = x
                continue
            }
            if (ch == '\r') {
                continue
            }
            val g = glyphs[ch]
            if (g != null) {
                //renderer.drawTextureRegion(texture, drawX, drawY, g.x, g.y, g.width, g.height, c)
                drawX += g.width
            }
        }
        //renderer.end()
    }

    fun getWidth(text: CharSequence): Int {
        var width = 0
        var lineWidth = 0
        for (i in 0..text.length - 1) {
            val c = text[i]
            if (c == '\n') {
                width = Math.max(width, lineWidth)
                lineWidth = 0
                continue
            }
            if (c == '\r') {
                continue
            }
            val g = glyphs[c]
            if (g != null) {
                lineWidth += g.width
            }
        }
        return Math.max(width, lineWidth)
    }

    fun getHeight(text: CharSequence): Int {
        var height = 0
        var lineHeight = 0
        for (i in 0..text.length - 1) {
            val c = text[i]
            if (c == '\n') {
                height += lineHeight
                lineHeight = 0
                continue
            }
            if (c == '\r') {
                continue
            }
            val g = glyphs[c]
            if (g != null) {
                lineHeight = Math.max(lineHeight, g.height)
            }
        }
        height += lineHeight
        return height
    }
}
