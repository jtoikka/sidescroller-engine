#version 120
attribute vec3 position;
attribute vec3 normal;
attribute vec2 uv;

uniform mat4 cameraToClipMatrix;
uniform mat4 modelToCameraMatrix;

varying vec2 UV;
varying vec3 norm;
varying float depth;

void main() {
	vec4 posCam = modelToCameraMatrix * vec4(position, 1.0);
	vec4 posClip = cameraToClipMatrix * posCam;
	// posClip = vec4(position.xy * 1000.0, -10.0, 1.0);
	gl_Position = posClip;
	// UV = uv;
	UV = uv;
	norm = (modelToCameraMatrix * vec4(normal, 0.0)).xyz;

	depth = -posClip.z;
}