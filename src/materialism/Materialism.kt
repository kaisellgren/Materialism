package materialism

import materialism.BlockSize.BASE_SIZE
import materialism.Shader.Companion.loadShader
import materialism.shader.Attenuation
import materialism.shader.Material
import materialism.shader.PointLight
import org.joml.Vector3f
import org.joml.Vector4f
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

        val featureSize = 0.01
        for (x in 0..20) {
            for (y in 0..10) {
                for (z in 0..20) {
                    val value = noise.eval(x.toDouble() / 4, y.toDouble() / 1, z.toDouble() / 4)
                    if (value > 0) {
                        val model = Model(mesh)
                        model.position.set(x.toFloat() * BASE_SIZE, y.toFloat() * BASE_SIZE, z.toFloat() * BASE_SIZE)
                        terrain.add(model)
                    }
                }
            }
        }

        val voxelVS = loadShader(GL_VERTEX_SHADER, "shaders/voxel.vs")
        val voxelFS = loadShader(GL_FRAGMENT_SHADER, "shaders/voxel.fs")

        val shaderProgram = ShaderProgram()
        shaderProgram.attachShader(voxelVS)
        shaderProgram.attachShader(voxelFS)
        shaderProgram.bindFragmentDataLocation(0, "fragColor")
        shaderProgram.link()
        shaderProgram.use()

        val position = shaderProgram.getAttributeLocation("position")
        shaderProgram.enableVertexAttribute(position)
        shaderProgram.pointVertexAttribute(position, 3, 8 * java.lang.Float.BYTES, 0)

        val texcoord = shaderProgram.getAttributeLocation("texcoord")
        shaderProgram.enableVertexAttribute(texcoord)
        shaderProgram.pointVertexAttribute(texcoord, 2, 8 * java.lang.Float.BYTES, 3 * java.lang.Float.BYTES.toLong())

        val normal = shaderProgram.getAttributeLocation("normal")
        if (normal >= 0) {
            shaderProgram.enableVertexAttribute(normal)
            shaderProgram.pointVertexAttribute(normal, 3, 8 * java.lang.Float.BYTES, 5 * java.lang.Float.BYTES.toLong())
        }

        shaderProgram.createUniform("modelViewMatrix")
        shaderProgram.createUniform("projectionMatrix")
        shaderProgram.createUniform("texImage")
        shaderProgram.createUniform("ambientLight")
        shaderProgram.createUniform("specularPower")
        shaderProgram.createPointLightUniform("pointLight")
        shaderProgram.createMaterialUniform("material")
        shaderProgram.setUniform("specularPower", 1f)
        shaderProgram.setUniform("texImage", 0)
        shaderProgram.setUniform("ambientLight", Vector3f(0.2f, 0.2f, 0.2f))
        shaderProgram.setUniform("material", Material(reflectance = 1f))

        loop(shaderProgram)

        mesh.delete()
        voxelVS.delete()
        voxelFS.delete()
        shaderProgram.delete()
    }

    fun loop(shaderProgram: ShaderProgram) {
        var delta: Float
        var accumulator = 0f
        var alpha: Float
        val targetUPS = 60
        val interval = 1f / targetUPS

        val projectionMatrix = transformation.getProjectionMatrix(window)
        shaderProgram.setUniform("projectionMatrix", projectionMatrix)

        while (!glfwWindowShouldClose(window)) {
            delta = timer.delta
            accumulator += delta

            input()

            while (accumulator >= interval) {
                update(1f / targetUPS)
                accumulator -= interval
            }

            alpha = accumulator / interval
            render(alpha, shaderProgram)

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
            val modelViewMatrix = transformation.getModelViewMatrix(model, viewMatrix)

            val p = Vector3f(0f, 100f, 0f)
            val pos = Vector4f(p, 1f)
            pos.mul(modelViewMatrix)
            p.x = pos.x
            p.y = pos.y
            p.z = pos.z
            shaderProgram.setUniform("pointLight", PointLight(
                color = Vector3f(1f, 1f, 1f),
                position = p,
                att = Attenuation(constant = 1f, exponent = 0f, linear = 0f),
                intensity = 1f
            ))

            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix)

            model.mesh.render()
        }

        glfwSwapBuffers(window)

        logError()
    }
}

fun main(args: Array<String>) {
    Materialism()
}
