uniform mat4 modelMatrix; // matrix for modeling transform (without view)
varying vec4 shadowCoord; // variable for shadow lookup coordinates

void main()
{
	shadowCoord = gl_TextureMatrix[0] *modelMatrix*  gl_Vertex;
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}
//fragment

uniform sampler2DShadow shadowTexture; // shadow map
varying vec4 shadowCoord; // interpolated shadow texture coordinates

void main()
{
	float depth = 0.0;
	if ( shadowCoord.w > 0.0 )
		depth = shadow2DProj( shadowTexture, shadowCoord ).r;
	// set shade factor
	float shadeFactor = depth != 1.0 ? 0.5 : 1.0;

	gl_FragColor = gl_Color * shadeFactor; // gl_Color * shadeFactor;
}