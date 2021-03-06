package materialism

import materialism.BlockSize.BASE_SIZE
import materialism.BlockSize.LARGE_SIZE
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import java.lang.Math.*

class Player {
    val position = Vector3f(20f, 50f, 20f)
    val rotation = Vector3f(0f, 0f, 0f) // In degrees. Pitch, yaw, roll. Roll isn't used.

    val size = Vector3f(BASE_SIZE, LARGE_SIZE, BASE_SIZE)
    val velocity = Vector3f()
    val movementSpeed = 10f
    val mouseSpeed = 0.5f
    val weight = 0.5f

    fun update(dt: Float, mouse: Mouse, window: Long, terrain: List<Chunk>) {
        updateRotation(mouse.displayCoord.x * mouseSpeed, mouse.displayCoord.y * mouseSpeed)
        updateMovement(dt, window, terrain)
        updateGravity(dt, terrain)
    }

    fun updateRotation(x: Float, y: Float) {
        rotation.x = clamp(rotation.x + x, -90f, 90f)
        rotation.y = (rotation.y + y) //% 180
    }

    fun updateMovement(dt: Float, window: Long, terrain: List<Chunk>) {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            position.x += sin(toRadians(rotation.y.toDouble())).toFloat() * movementSpeed * dt
            position.z -= cos(toRadians(rotation.y.toDouble())).toFloat() * movementSpeed * dt
        }

        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            position.x -= sin(toRadians(rotation.y.toDouble())).toFloat() * movementSpeed * dt * 0.5f
            position.z += cos(toRadians(rotation.y.toDouble())).toFloat() * movementSpeed * dt * 0.5f
        }

        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            position.x += sin(toRadians(rotation.y.toDouble() - 90)).toFloat() * movementSpeed * dt
            position.z -= cos(toRadians(rotation.y.toDouble() - 90)).toFloat() * movementSpeed * dt
        }

        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            position.x += sin(toRadians(rotation.y.toDouble() + 90)).toFloat() * movementSpeed * dt
            position.z -= cos(toRadians(rotation.y.toDouble() + 90)).toFloat() * movementSpeed * dt
        }

        if (glfwGetKey(window, GLFW_KEY_LEFT_ALT) == GLFW_PRESS) {
            val terrainBelow = getBlockBelow(terrain)
            if (terrainBelow != null || true) {
                velocity.y = 20f * dt
            }
        }
    }

    fun updateGravity(dt: Float, terrain: List<Chunk>) {
        val terrainBelow = getBlockBelow(terrain)

        if (terrainBelow != null) {
            velocity.y = 0f
            position.y = terrainBelow
        } else {
            velocity.y -= pow(weight.toDouble(), velocity.y.toDouble()).toFloat() * dt
            if (velocity.y < -2f) {
                velocity.y = -2f
            }
        }

        position.y += velocity.y
    }

    fun getBlockBelow(terrain: List<Chunk>): Float? {
        for (chunk in terrain) {
            for (model in chunk.models) {
                for (collisionModel in model.collisionModels) {
                    if (collisionModel is CubeCollision) {
                        fun isWithin(size: Float, size2: Float, position: Float, position2: Float): Boolean {
                            if (size < size2) {
                                val isLeftWithin = position >= position2 && position <= position2 + size2
                                val isRightWithin = position + size >= position2 && position + size <= position2 + size2
                                return isLeftWithin || isRightWithin
                            } else {
                                val isLeftWithin = position2 >= position && position2 <= position + size
                                val isRightWithin = position2 + size2 >= position && position2 + size2 <= position + size
                                return isLeftWithin || isRightWithin
                            }
                        }

                        if (isWithin(collisionModel.width, size.x, collisionModel.x + model.position.x, position.x)) {
                            if (isWithin(collisionModel.length, size.z, collisionModel.z + model.position.z, position.z)) {
                                if (position.y + velocity.y <= collisionModel.y + model.position.y + collisionModel.height && position.y + velocity.y >= collisionModel.y + model.position.y) {
                                    return collisionModel.y + model.position.y + collisionModel.height
                                }
                            }
                        }
                    } else {
                        logError("Unsupported collision model: $collisionModel")
                    }
                }
            }
        }

        return null
    }
}
