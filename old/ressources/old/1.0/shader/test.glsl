void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment
uniform sampler2D tex;
void main()
{
   gl_FragColor =  gl_FrontColor*texture2D(tex, vec2(gl_TexCoord[0]));
    gl_FragColor.r = (1-sqrt(length(gl_FragCoord.xy-vec2(400,300)))/19);
}
