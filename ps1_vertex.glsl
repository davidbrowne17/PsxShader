uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform float u_snapSize;
uniform int u_hasBones;
uniform mat4 u_bones[15];
uniform vec3 u_cameraPos;

attribute vec4 a_position;
attribute vec2 a_texCoord0;
attribute vec4 a_boneWeights;
attribute vec4 a_boneIndices;

varying vec2 v_texCoord0;

void main() {
    vec4 finalPosition = a_position;

    if (u_hasBones == 1) {
        mat4 skinning = mat4(0.0); // initialize to zero matrix

        for (int i = 0; i < 4; i++) {
            int boneIndex = int(a_boneIndices[i]);
            skinning += a_boneWeights[i] * u_bones[boneIndex];
        }

        finalPosition = skinning * a_position;
    }

    vec4 worldPosition = u_worldTrans * finalPosition;

    // Calculate the snapping factor
    float snapFactor = float(u_hasBones == 0);
    worldPosition.x = floor(worldPosition.x / u_snapSize + 0.5 * snapFactor) * u_snapSize;
    worldPosition.y = floor(worldPosition.y / u_snapSize + 0.5 * snapFactor) * u_snapSize;
    worldPosition.z = floor(worldPosition.z / u_snapSize + 0.5 * snapFactor) * u_snapSize;

    gl_Position = u_projViewTrans * worldPosition;

    // Warp texture coordinates
    float warpFactor = 0.00005;
    vec2 warp = warpFactor * (worldPosition.xyz - u_cameraPos).xz;
    v_texCoord0 = a_texCoord0 - warp;
}
