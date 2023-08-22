varying vec2 v_texCoord0;
uniform sampler2D u_texture;

const float PALETTE_SIZE = 8.0; // Limited color depth
const mat2 ditherMatrix = mat2(0.0, 2.0, 3.0, 1.0) / 4.0;

vec3 manualRound(vec3 number) {
    return vec3(floor(number.x + 0.5), floor(number.y + 0.5), floor(number.z + 0.5));
}

void main() {
    // Dithering effect
    vec4 color = texture2D(u_texture, v_texCoord0);
    vec2 ditherPos = mod(gl_FragCoord.xy, 2.0);
    float ditherValue = ditherMatrix[int(ditherPos.x)][int(ditherPos.y)];
    color.rgb = color.rgb + ditherValue / 255.0;

    // Quantize to a limited color palette for both animated and non-animated models
    color.rgb = manualRound(color.rgb * PALETTE_SIZE) / PALETTE_SIZE;

    gl_FragColor = color;
}
