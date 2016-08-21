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
    val terrain: ArrayList<Chunk> = arrayListOf()
    val noise = OpenSimplexNoise()
    val player = Player()
    val dayNightCycle = DayNightCycle()
    //var font: Font? = null
    var dirt: Texture? = null

    val att = Attenuation(constant = 1f, exponent = 0f, linear = 0f)
    val pointLightColor = Vector3f(255 / 255f, 246 / 255f, 216 / 255f)
    val tmpPointLightPosM = Vector4f()
    val pointLightPos = Vector3f(20f, 50f, 20f)
    val tmpPointLightPos = Vector3f()
    val pointLight = PointLight(
        color = pointLightColor,
        position = pointLightPos,
        att = att,
        intensity = dayNightCycle.intensity
    )

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
        //font = Font(java.awt.Font(java.awt.Font.MONOSPACED, java.awt.Font.PLAIN, 16), true)
        dirt = Texture.fromPath("assets/dirt.png")

        // 9000 = 50fps, 14000 = 30fps, 20000 = 25fps / 1 115 000
        var totalBlocks = 0
        for (chunkX in 0..4) {
            for (chunkZ in 0..4) {
                val chunk = Chunk()
                for (modelIndex in 0..15) {
                    val meshData: ArrayList<Array<Float>> = arrayListOf()
                    val collisionModels: ArrayList<CollisionModel> = arrayListOf()
                    for (x in 0..15) {
                        for (z in 0..15) {
                            for (y in 0..15) {
                                val value = noise.eval(x.toDouble() / 8, y.toDouble() / 4, z.toDouble() / 8)
                                if (value > 0) {
                                    val x1 = x.toFloat() * BASE_SIZE
                                    val y1 = y.toFloat() * BASE_SIZE
                                    val z1 = z.toFloat() * BASE_SIZE
                                    meshData.add(VertexBuilder.cube(x1, y1, z1, BASE_SIZE, BASE_SIZE, BASE_SIZE))
                                    collisionModels.add(CubeCollision(x1, y1, z1, BASE_SIZE, BASE_SIZE, BASE_SIZE))
                                    totalBlocks++
                                }
                            }
                        }
                    }
                    val meshSize = 16f * BASE_SIZE
                    val mesh = Mesh(meshData.toTypedArray())
                    val model = Model(mesh, collisionModels.toTypedArray())
                    model.position.set(chunkX.toFloat() * meshSize, modelIndex.toFloat() * meshSize, chunkZ.toFloat() * meshSize)
                    model.size.set(meshSize, meshSize, meshSize)
                    chunk.models.add(model)
                }
                terrain.add(chunk)
            }
        }
        println("Total blocks: $totalBlocks")

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
        shaderProgram.setUniform("material", Material(reflectance = 0f))

        loop(shaderProgram)

        for (c in terrain) {
            c.delete()
        }
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

            timer.update()
            glfwPollEvents()
        }
    }

    fun input() {
        mouse.input()
    }

    fun update(dt: Float) {
        player.update(dt, mouse, window, terrain)
        dayNightCycle.update(dt)
        timer.updateUPS()
    }

    var fpsTimeout = 0
    fun render(dt: Float, shaderProgram: ShaderProgram) {
        fpsTimeout++
        if (fpsTimeout > 25) {
            fpsTimeout = 0
            println("FPS: ${timer.getFPS()}, UPS: ${timer.getUPS()}")
        }
        timer.updateFPS()

        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        dirt?.bind()

        shaderProgram.use()

        shaderProgram.setUniform("ambientLight", dayNightCycle.currentColor)

        val viewMatrix = transformation.getViewMatrix(player)

        for (chunk in terrain) {
            for (model in chunk.models) {
                val modelViewMatrix = transformation.getModelViewMatrix(model, viewMatrix)

                val pos = tmpPointLightPosM.set(pointLightPos.x, pointLightPos.y, pointLightPos.z, 1f)
                pos.mul(modelViewMatrix)
                tmpPointLightPos.x = pos.x
                tmpPointLightPos.y = pos.y
                tmpPointLightPos.z = pos.z

                pointLight.intensity = dayNightCycle.intensity
                pointLight.position = tmpPointLightPos

                shaderProgram.setUniform("pointLight", pointLight)
                shaderProgram.setUniform("modelViewMatrix", modelViewMatrix)

                model.mesh.render()
            }
        }

        //font?.drawText(a, "testing this thing")

        glfwSwapBuffers(window)

        logGLError()
    }
}

fun main(args: Array<String>) {
    Materialism()
}
