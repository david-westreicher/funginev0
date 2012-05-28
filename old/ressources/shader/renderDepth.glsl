void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment


// Scene buffer

//uniform sampler2D tex; 
uniform sampler2D depth; 

float LinearizeDepth(vec2 uv)
{
  float n = 20.0; // camera z near
  float f = 500.0; // camera z far
  float z = float(texture2D(depth, uv).x);
  return (2.0 * n) / (f + n - z * (f - n));
}

void main()
{
 
  gl_FragColor.rgb = vec3(LinearizeDepth(gl_TexCoord[0].xy));
   gl_FragColor.a = 1;
}