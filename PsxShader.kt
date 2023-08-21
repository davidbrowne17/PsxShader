import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g3d.Renderable
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.utils.RenderContext
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.utils.GdxRuntimeException
const val MAX_BONES = 16

class PsxShader : Shader {
    private lateinit var program: ShaderProgram
    private var camera: Camera? = null
    private var context: RenderContext? = null
    private var u_texture = 0
    private var u_snapSize = 0
    private var u_cameraPos = 0

    override fun init() {
        val vert = Gdx.files.internal("shaders/ps1_vertex.glsl").readString()
        val frag = Gdx.files.internal("shaders/ps1_fragment.glsl").readString()
        program = ShaderProgram(vert, frag)
        if (!program.isCompiled) {
            throw GdxRuntimeException("Shader compilation failed: ${program.log}")
        }
        u_texture = program.getUniformLocation("u_texture")
        u_snapSize = program.getUniformLocation("u_snapSize")
        u_cameraPos = program.getUniformLocation("u_cameraPos")
    }

    override fun dispose() {
        program.dispose()
    }
    override fun begin(camera: Camera, context: RenderContext) {
        this.camera = camera
        this.context = context
        program.bind()
        program.setUniformMatrix("u_projViewTrans", camera.combined)
        program.setUniformf("u_snapSize", 0.1f)
        program.setUniformf("u_cameraPos", camera.position)
        context.setDepthTest(GL20.GL_LEQUAL)
        context.setCullFace(1)
    }

    override fun render(renderable: Renderable) {
        val textureAttribute = renderable.material.get(TextureAttribute.Diffuse) as TextureAttribute
        val textureUnit = context!!.textureBinder.bind(textureAttribute.textureDescription)
        program.setUniformMatrix("u_worldTrans", renderable.worldTransform)
        program.setUniformi("u_texture", textureUnit)
        if (renderable.bones != null && renderable.bones.isNotEmpty()) {
            program.setUniformi("u_hasBones", 1)
            val bonesArray = FloatArray(MAX_BONES * 16)
            for (i in renderable.bones.indices) {
                val boneMat = renderable.bones[i].values
                System.arraycopy(boneMat, 0, bonesArray, i * 16, 16)
            }
            program.setUniformMatrix4fv("u_bones", bonesArray, 0, bonesArray.size)
        } else {
            program.setUniformi("u_hasBones", 0)
        }
        renderable.meshPart.render(program)
    }
    override fun end() {}
    override fun compareTo(other: Shader?): Int {
        return 0
    }
    override fun canRender(instance: Renderable?): Boolean {
        return true
    }
}