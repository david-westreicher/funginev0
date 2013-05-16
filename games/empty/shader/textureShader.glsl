void main(void)
{
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix *gl_Vertex;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment
uniform sampler2D tex;
void main()
{
   gl_FragColor = texture2D(tex,gl_TexCoord[0].xy);
   gl_FragColor.a = 1;
}
