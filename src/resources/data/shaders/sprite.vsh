#version 120
attribute vec2 position;
attribute vec2 uv;

uniform mat3 cameraToClipMatrix;

varying vec2 UV;

void main() {
	vec3 posClip = cameraToClipMatrix * vec3(position, 1.0);
	gl_Position = vec4(posClip.xy, -1.0, 1.0);
	// gl_Position = vec4(position, 0.0, 1.0);
	UV = uv;
}