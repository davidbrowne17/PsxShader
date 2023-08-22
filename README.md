# PsxShader
![Demo Animation](demo.gif)

 A PSX style shader created for a libGDX horror game
## HOW TO USE

### Step 1
Create a folder called shaders in your libGDX assets folder. Copy the glsl files in the repo to this folder

### Step 2
Copy the PsxShader.kt file into your project add the correct package to the top of the file

### Step 3
Place the following as a variable where you are creating your ModelBatch
```kotlin
val psxShaderProvider = object : ShaderProvider {
        val shader = PsxShader()
        override fun dispose() {
            shader.dispose()
        }

        override fun getShader(renderable: Renderable): Shader {
            shader.init()
            return shader
        }
    }
```
### Step 4
Pass the shader into the ModelBatch during its creation
```kotlin
modelBatch = ModelBatch(psxShaderProvider)
```

You will now have a PSX shader working on your libGDX 3D game!!
