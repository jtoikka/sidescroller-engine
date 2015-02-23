varying vec2 UV;

uniform sampler2D diffuseTex;

void main() {
	vec4 diffuseColour = texture2D(diffuseTex, UV);
	gl_FragColor = diffuseColour;
}