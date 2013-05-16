void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment

uniform sampler2D ambTex;
uniform sampler2D depthTex;
uniform float zNear;
uniform float zFar;


 float LinearizeDepth(vec2 uv)
{
  float z = texture2D(depthTex, uv).x;
  return (2.0 * zNear) / (zFar + zNear - z * (zFar - zNear));
}

void main()
{
 
      vec2 pos=gl_TexCoord[0].xy;
        vec4 tex   = texture2D(ambTex,pos);
        float d =   LinearizeDepth(pos);
        gl_FragColor =tex;
        gl_FragColor.a =1-d;
}

