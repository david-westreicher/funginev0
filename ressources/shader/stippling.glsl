void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment


// Scene buffer
uniform sampler2D tex;
int size = 10;

void main(void)
{
  gl_FragColor = texture2D(tex, gl_TexCoord[0].xy);
  if(size>1){
if(int(gl_FragCoord.x)%size>=size/2&&int(gl_FragCoord.y)%size>=size/2){
    discard;
}}
}