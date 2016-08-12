package materialism

import materialism.BlockSize.BASE_SIZE
import materialism.Shader.Companion.loadShader
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL13.GL_TEXTURE0
import org.lwjgl.opengl.GL13.glActiveTexture
import org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER
import org.lwjgl.opengl.GL20.GL_VERTEX_SHADER
import java.util.*

class Materialism {
    val window: Long
    val timer = Timer()
    val mouse = Mouse()
    val transformation = Transformation()
    val terrain: ArrayList<Model> = arrayListOf()
    val noise = OpenSimplexNoise()
    val player = Player()

    init {
        try {
            window = initializeOpenGL()
            initializeGame()

            glfwFreeCallbacks(window)
            glfwDestroyWindow(window)
        } finally {
            glfwTerminate()
            glfwSetErrorCallback(null).free()
        }
    }

    fun initializeGame() {
        mouse.initialize(window)

        glActiveTexture(GL_TEXTURE0)
        val dirt = Texture("assets/dirt.png")

        val mesh = Mesh(VertexBuilder.cube(0f, 0f, 0f, BASE_SIZE, BASE_SIZE, BASE_SIZE))

        for (x in -10..10) {
            //for (y in 0..10) {
                for (z in -10..10) {
                    /*val value = noise.eval(x.toDouble(), y.toDouble() / 10, z.toDouble())
                    if (value > 0) {*/
                    val y = 0
                    val model = Model(mesh)
                    model.position.set(x.toFloat() * BASE_SIZE, y.toFloat() * BASE_SIZE, z.toFloat() * BASE_SIZE)
                    terrain.add(model)
                    //}
                }
            //}
        }

        val voxelVS = loadShader(GL_VERTEX_SHADER, "shaders/voxel.vs")
        val voxelFS = loadShader(GL_FRAGMENT_SHADER, "shaders/voxel.fs")

        val voxelShaderProgram = ShaderProgram()
        voxelShaderProgram.attachShader(voxelVS)
        voxelShaderProgram.attachShader(voxelFS)
        voxelShaderProgram.bindFragmentDataLocation(0, "fragColor")
        voxelShaderProgram.link()
        voxelShaderProgram.use()

        val pos = voxelShaderProgram.getAttributeLocation("position")
        voxelShaderProgram.enableVertexAttribute(pos)
        voxelShaderProgram.pointVertexAttribute(pos, 3, 8 * java.lang.Float.BYTES, 0)

        val color = voxelShaderProgram.getAttributeLocation("color")
        voxelShaderProgram.enableVertexAttribute(color)
        voxelShaderProgram.pointVertexAttribute(color, 3, 8 * java.lang.Float.BYTES, 3 * java.lang.Float.BYTES.toLong())

        val texcoord = voxelShaderProgram.getAttributeLocation("texcoord")
        voxelShaderProgram.enableVertexAttribute(texcoord)
        voxelShaderProgram.pointVertexAttribute(texcoord, 2, 8 * java.lang.Float.BYTES, 6 * java.lang.Float.BYTES.toLong())

        voxelShaderProgram.createUniform("modelViewMatrix")
        voxelShaderProgram.createUniform("projectionMatrix")
        voxelShaderProgram.createUniform("texImage")
        voxelShaderProgram.setUniform("texImage", 0)

        loop(voxelShaderProgram)

        mesh.delete()
        voxelVS.delete()
        voxelFS.delete()
        voxelShaderProgram.delete()
    }

    fun loop(voxelShaderProgram: ShaderProgram) {
        var delta: Float
        var accumulator = 0f
        var alpha: Float
        val targetUPS = 60
        val interval = 1f / targetUPS

        voxelShaderProgram.setUniform("projectionMatrix", transformation.getProjectionMatrix(window))

        while (!glfwWindowShouldClose(window)) {
            delta = timer.delta
            accumulator += delta


            input()

            while (accumulator >= interval) {
                update(1f / targetUPS)
                accumulator -= interval
            }

            alpha = accumulator / interval
            render(alpha, voxelShaderProgram)

            glfwPollEvents()
        }
    }

    fun input() {
        mouse.input()
    }

    fun update(dt: Float) {
        player.update(dt, mouse, window, terrain)
    }

    fun render(dt: Float, shaderProgram: ShaderProgram) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        shaderProgram.use()

        val viewMatrix = transformation.getViewMatrix(player)

        for (model in terrain) {
            shaderProgram.setUniform("modelViewMatrix", transformation.getModelViewMatrix(model, viewMatrix))
            model.mesh.render()
        }

        glfwSwapBuffers(window)

        logError()
    }
}

fun main(args: Array<String>) {
    Materialism()
}
