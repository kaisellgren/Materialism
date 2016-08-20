#version 450 core

in vec3 position;
in vec2 texcoord;
in vec3 normal;

out vec2 textureCoord;
out vec3 vertexNormal;
out vec3 vertexPosition;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main() {
    mat4 mvp = projectionMatrix * modelViewMatrix;
    gl_Position = mvp * vec4(position, 1.0);
    textureCoord = texcoord;
    vec4 mvPos = modelViewMatrix * vec4(position, 1.0);
    vertexNormal = normalize(modelViewMatrix * vec4(normal, 0.0)).xyz;
    vertexPosition = mvPos.xyz;
}
