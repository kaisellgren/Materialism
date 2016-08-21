package materialism

import org.joml.Matrix4f
import org.lwjgl.BufferUtils.createIntBuffer
import org.lwjgl.glfw.GLFW.glfwGetFramebufferSize

class Transformation {
    private val viewMatrix: Matrix4f = Matrix4f()
    private val modelViewMatrix: Matrix4f = Matrix4f()
    private val projectionMatrix: Matrix4f = Matrix4f()
    private val viewCurr: Matrix4f = Matrix4f()

    fun getProjectionMatrix(window: Long): Matrix4f {
        val width = createIntBuffer(1)
        val height = createIntBuffer(1)
        glfwGetFramebufferSize(window, width, height)
        val ratio = width.get().toFloat() / height.get()

        return projectionMatrix
            .identity()
            .perspective(Math.toRadians(70.0).toFloat(), ratio, 0.001f, 1000f)
    }

    fun getModelViewMatrix(model: Model, viewMatrix: Matrix4f): Matrix4f {
        modelViewMatrix
            .identity()
            .translate(model.position)
            .rotateX(Math.toRadians(-model.rotation.x.toDouble()).toFloat())
            .rotateY(Math.toRadians(-model.rotation.y.toDouble()).toFloat())
            .rotateZ(Math.toRadians(-model.rotation.z.toDouble()).toFloat())
            .scale(model.scale)
        viewCurr.set(viewMatrix)
        return viewCurr.mul(modelViewMatrix)
    }

    fun getViewMatrix(player: Player): Matrix4f {
        return viewMatrix
            .identity()
            .rotate(Math.toRadians(player.rotation.x.toDouble()).toFloat(), 1f, 0f, 0f)
            .rotate(Math.toRadians(player.rotation.y.toDouble()).toFloat(), 0f, 1f, 0f)
            .translate(-player.position.x, -player.position.y - player.size.y, -player.position.z)
    }
}
