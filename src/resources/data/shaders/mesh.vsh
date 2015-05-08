#version 120
attribute vec3 position;
attribute vec3 normal;
attribute vec2 uv;

uniform mat4 cameraToClipMatrix;
uniform mat4 modelToCameraMatrix;

varying vec2 UV;

void main() {
	vec4 posCam = modelToCameraMatrix * vec4(position, 1.0);
	vec4 posClip = cameraToClipMatrix * posCam;
	gl_Position = posClip;
	UV = uv;
}