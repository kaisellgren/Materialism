package materialism.shader

import org.joml.Vector3f

data class PointLight(val color: Vector3f, val position: Vector3f, val intensity: Float, val att: Attenuation)
