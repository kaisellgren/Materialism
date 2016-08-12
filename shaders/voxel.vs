#version 450 core

in vec3 position;
in vec3 color;
in vec2 texcoord;

out vec3 vertexColor;
out vec2 textureCoord;

uniform mat4 modelViewMatrix;
uniform mat4 projectionMatrix;

void main() {
    vertexColor = color;
    textureCoord = texcoord;
    mat4 mvp = projectionMatrix * modelViewMatrix;
    gl_Position = mvp * vec4(position, 1.0);
}
