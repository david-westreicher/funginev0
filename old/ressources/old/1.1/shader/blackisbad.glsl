void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment


// Scene buffer
uniform sampler2D tex; 


void main(void)
{ 
  gl_FragColor = texture2D(tex, gl_TexCoord[0].xy);
  if(length(gl_FragColor.rgb)<0.1){
  	discard;
 }
}