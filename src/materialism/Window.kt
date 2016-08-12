package materialism

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil

fun initializeOpenGL(): Long {
    GLFWErrorCallback.createPrint(System.err).set()

    if (!glfwInit()) {
        throw IllegalStateException("Unable to initialize GLFW")
    }

    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)

    val WIDTH = 1920
    val HEIGHT = 1080

    // Create the window
    val window = glfwCreateWindow(WIDTH, HEIGHT, "Materialism", MemoryUtil.NULL, MemoryUtil.NULL)
    if (window == MemoryUtil.NULL) {
        throw RuntimeException("Your system does not support OpenGL 4.5.")
    }

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window) { window, key, scancode, action, mods ->
        if (key === GLFW_KEY_ESCAPE && action === GLFW_RELEASE) {
            glfwSetWindowShouldClose(window, true) // We will detect this in our rendering loop
        }
    }

    // Get the resolution of the primary monitor
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

    // Center our window
    glfwSetWindowPos(window, (vidmode.width() - WIDTH) / 2, (vidmode.height() - HEIGHT) / 2)

    glfwMakeContextCurrent(window)
    glfwSwapInterval(1) // Enable v-sync
    glfwShowWindow(window)

    GL.createCapabilities()

    glEnable(GL_CULL_FACE)
    glEnable(GL_DEPTH_TEST)
    glCullFace(GL_BACK)
    glClearColor(0.8f, 0.5f, 0.5f, 0.0f)
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED)

    return window
}
