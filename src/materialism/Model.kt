package materialism

import materialism.BlockSize.BASE_SIZE
import org.joml.Vector3f

class Model(val mesh: Mesh) {
    val position: Vector3f = Vector3f()
    val scale: Float = 1f
    val rotation: Vector3f = Vector3f()
    val size: Vector3f = Vector3f(BASE_SIZE, BASE_SIZE, BASE_SIZE)
}
