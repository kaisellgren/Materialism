package materialism

import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWCursorEnterCallbackI
import org.lwjgl.glfw.GLFWCursorPosCallbackI
import org.lwjgl.glfw.GLFWMouseButtonCallbackI

class Mouse {
    val previousPos = Vector2d(-1.0, -1.0)
    val currentPos = Vector2d(0.0, 0.0)
    val displayCoord = Vector2f()
    var inWindow = false
    var isLeftButtonPressed = false
    var isRightButtonPressed = false
    var isMiddleButtonPressed = false

    val glfwCursorPosCallbackI = GLFWCursorPosCallbackI { window, x, y ->
        currentPos.x = x
        currentPos.y = y
    }

    val glfwCursorEnterCallbackI = GLFWCursorEnterCallbackI { window, entered -> inWindow = entered }

    val glfwMouseButtonCallbackI = GLFWMouseButtonCallbackI { window, button, action, mods ->
        isLeftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
        isRightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
        isMiddleButtonPressed = button == GLFW_MOUSE_BUTTON_3 && action == GLFW_PRESS
    }

    fun initialize(window: Long) {
        glfwSetCursorPosCallback(window, glfwCursorPosCallbackI)
        glfwSetCursorEnterCallback(window, glfwCursorEnterCallbackI)
        glfwSetMouseButtonCallback(window, glfwMouseButtonCallbackI)
    }

    fun input() {
        displayCoord.zero()

        if (inWindow) {
            val deltaX = currentPos.x - previousPos.x
            val deltaY = currentPos.y - previousPos.y
            val rotateX = deltaX != 0.0
            val rotateY = deltaY != 0.0
            if (rotateX) {
                displayCoord.y = deltaX.toFloat()
            }
            if (rotateY) {
                displayCoord.x = deltaY.toFloat()
            }
        }

        previousPos.set(currentPos)
    }
}
