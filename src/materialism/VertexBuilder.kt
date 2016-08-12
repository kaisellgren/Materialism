package materialism

object VertexBuilder {
    fun cube(x: Float, y: Float, z: Float, width: Float, height: Float, length: Float): Array<Float> {
        return arrayOf(
            // Back.
            x + width, y + height, z, 1f, 1f, 1f, 1f, 1f,
            x + width, y, z, 1f, 1f, 1f, 1f, 0f,
            x, y, z, 1f, 1f, 1f, 0f, 0f,
            x, y, z, 1f, 1f, 1f, 0f, 0f,
            x, y + height, z, 1f, 1f, 1f, 0f, 1f,
            x + width, y + height, z, 1f, 1f, 1f, 1f, 1f,

            // Left.
            x, y, z, 1f, 1f, 1f, 0f, 0f,
            x, y, z + length, 1f, 1f, 1f, 0f, 1f,
            x, y + height, z + length, 1f, 1f, 1f, 1f, 1f,
            x, y + height, z + length, 1f, 1f, 1f, 1f, 1f,
            x, y + height, z, 1f, 1f, 1f, 1f, 0f,
            x, y, z, 1f, 1f, 1f, 0f, 0f,

            // Floor.
            x, y, z, 1f, 1f, 1f, 1f, 1f,
            x + width, y, z, 1f, 1f, 1f, 0f, 1f,
            x + width, y, z + length, 1f, 1f, 1f, 0f, 0f,
            x + width, y, z + length, 1f, 1f, 1f, 0f, 0f,
            x, y, z + length, 1f, 1f, 1f, 1f, 0f,
            x, y, z, 1f, 1f, 1f, 1f, 1f,

            // Right.
            x + width, y, z, 1f, 1f, 1f, 0f, 0f,
            x + width, y + height, z, 1f, 1f, 1f, 1f, 0f,
            x + width, y + height, z + length, 1f, 1f, 1f, 1f, 1f,
            x + width, y + height, z + length, 1f, 1f, 1f, 1f, 1f,
            x + width, y, z + length, 1f, 1f, 1f, 0f, 1f,
            x + width, y, z, 1f, 1f, 1f, 0f, 0f,

            // Top.
            x + width, y + height, z + length, 1f, 1f, 1f, 1f, 0f,
            x + width, y + height, z, 1f, 1f, 1f, 1f, 1f,
            x, y + height, z, 1f, 1f, 1f, 0f, 1f,
            x, y + height, z, 1f, 1f, 1f, 0f, 1f,
            x, y + height, z + length, 1f, 1f, 1f, 0f, 0f,
            x + width, y + height, z + length, 1f, 1f, 1f, 1f, 0f,

            // Front.
            x, y, z + length, 1f, 1f, 1f, 0f, 0f,
            x + width, y, z + length, 1f, 1f, 1f, 1f, 0f,
            x + width, y + height, z + length, 1f, 1f, 1f, 1f, 1f,
            x, y, z + length, 1f, 1f, 1f, 0f, 0f,
            x + width, y + height, z + length, 1f, 1f, 1f, 1f, 1f,
            x, y + height, z + length, 1f, 1f, 1f, 0f, 1f
        )
    }
}
