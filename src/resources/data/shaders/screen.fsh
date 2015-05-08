varying vec2 UV;

uniform sampler2D depthTex;
uniform sampler2D screenTex;
uniform sampler2D debugTex;

const float texelH = 1.0/480.0;
const float texelW = 1.0/640.0;

void main() {
	vec4 diffuseColour = texture2D(screenTex, UV);
	vec4 debugColour = texture2D(debugTex, UV);
	vec4 depth = texture2D(depthTex, UV);

	gl_FragColor = diffuseColour;
}