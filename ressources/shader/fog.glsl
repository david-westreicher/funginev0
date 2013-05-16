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
 
       vec2 coord = gl_TexCoord[0].xy;
     float   factor = LinearizeDepth(coord);
         
 
                       
        //gl_FragColor = texture2D(ambTex, coord.xy );
         //gl_FragColor =vec4(factor);
        float z =1- LinearizeDepth(coord)*1.5;
gl_FragColor = texture2D(ambTex, coord.xy )*z;
      //  gl_FragColor.a = 1-factor;
}

