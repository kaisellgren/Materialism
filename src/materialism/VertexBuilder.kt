package materialism

object VertexBuilder {
    fun cube(x: Float, y: Float, z: Float, width: Float, height: Float, length: Float): Array<Float> {
        val uvTopLeft = 2/32f
        val uvBottomRight = 30/32f

        // Position, texCoord, normal.
        return arrayOf(
            // Back.
            x + width, y + height, z,
            uvBottomRight, uvBottomRight,
            0f, 0f, -1f,

            x + width, y, z,
            uvBottomRight, uvTopLeft,
            0f, 0f, -1f,

            x, y, z,
            uvTopLeft, uvTopLeft,
            0f, 0f, -1f,

            x, y, z,
            uvTopLeft, uvTopLeft,
            0f, 0f, -1f,

            x, y + height, z,
            uvTopLeft, uvBottomRight,
            0f, 0f, -1f,

            x + width, y + height, z,
            uvBottomRight, uvBottomRight,
            0f, 0f, -1f,

            // Left.
            x, y, z,
            uvTopLeft, uvTopLeft,
            -1f, 0f, 0f,

            x, y, z + length,
            uvTopLeft, uvBottomRight,
            -1f, 0f, 0f,

            x, y + height, z + length,
            uvBottomRight, uvBottomRight,
            -1f, 0f, 0f,

            x, y + height, z + length,
            uvBottomRight, uvBottomRight,
            -1f, 0f, 0f,

            x, y + height, z,
            uvBottomRight, uvTopLeft,
            -1f, 0f, 0f,

            x, y, z,
            uvTopLeft, uvTopLeft,
            -1f, 0f, 0f,

            // Floor.
            x, y, z,
            uvBottomRight, uvBottomRight,
            0f, -1f, 0f,

            x + width, y, z,
            uvTopLeft, uvBottomRight,
            0f, -1f, 0f,

            x + width, y, z + length,
            uvTopLeft, uvTopLeft,
            0f, -1f, 0f,

            x + width, y, z + length,
            uvTopLeft, uvTopLeft,
            0f, -1f, 0f,

            x, y, z + length,
            uvBottomRight, uvTopLeft,
            0f, -1f, 0f,

            x, y, z,
            uvBottomRight, uvBottomRight,
            0f, -1f, 0f,

            // Right.
            x + width, y, z,
            uvTopLeft, uvTopLeft,
            1f, 0f, 0f,

            x + width, y + height, z,
            uvBottomRight, uvTopLeft,
            1f, 0f, 0f,

            x + width, y + height, z + length,
            uvBottomRight, uvBottomRight,
            1f, 0f, 0f,

            x + width, y + height, z + length,
            uvBottomRight, uvBottomRight,
            1f, 0f, 0f,

            x + width, y, z + length,
            uvTopLeft, uvBottomRight,
            1f, 0f, 0f,

            x + width, y, z,
            uvTopLeft, uvTopLeft,
            1f, 0f, 0f,

            // Top.
            x + width, y + height, z + length,
            uvBottomRight, uvTopLeft,
            0f, 1f, 0f,

            x + width, y + height, z,
            uvBottomRight, uvBottomRight,
            0f, 1f, 0f,

            x, y + height, z,
            uvTopLeft, uvBottomRight,
            0f, 1f, 0f,

            x, y + height, z,
            uvTopLeft, uvBottomRight,
            0f, 1f, 0f,

            x, y + height, z + length,
            uvTopLeft, uvTopLeft,
            0f, 1f, 0f,

            x + width, y + height, z + length,
            uvBottomRight, uvTopLeft,
            0f, 1f, 0f,

            // Front.
            x, y, z + length,
            uvTopLeft, uvTopLeft,
            0f, 0f, 1f,

            x + width, y, z + length,
            uvBottomRight, uvTopLeft,
            0f, 0f, 1f,

            x + width, y + height, z + length,
            uvBottomRight, uvBottomRight,
            0f, 0f, 1f,

            x, y, z + length,
            uvTopLeft, uvTopLeft,
            0f, 0f, 1f,

            x + width, y + height, z + length,
            uvBottomRight, uvBottomRight,
            0f, 0f, 1f,

            x, y + height, z + length,
            uvTopLeft, uvBottomRight,
            0f, 0f, 1f
        )
    }
}
