package materialism.shader

import org.joml.Vector3f

data class PointLight(val color: Vector3f, var position: Vector3f, var intensity: Float, val att: Attenuation)
