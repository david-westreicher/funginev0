void main(void)
{
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment


// Scene buffer

//uniform sampler2D tex; 
uniform sampler2D depth; 
uniform float zNear;
uniform float zFar;

float LinearizeDepth(vec2 uv)
{
  float z = float(texture2D(depth, uv).x);
  return (2.0 * zNear) / (zFar + zNear - z * (zFar - zNear));
}

void main()
{
 
  gl_FragColor.rgb = vec3(LinearizeDepth(gl_TexCoord[0].xy));
  if( gl_FragColor.r==1)
  	discard;
   //gl_FragColor = vec4(texture2D(depth, gl_TexCoord[0].xy)/);
   //gl_FragColor = vec4(0.5);
}