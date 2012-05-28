uniform vec3 lightPos;
void main(void)
{
	vec4 lightSrc =gl_ModelViewMatrix*vec4(gl_NormalMatrix*lightPos,1.0);
	vec4 mvv = gl_ModelViewMatrix *gl_Vertex;
	vec3 normal =normalize(gl_NormalMatrix * gl_Normal);
	vec3 toLight = lightPos-mvv.xyz;
	mvv.xy+=normal.xy*10;
	if(dot(toLight,normal)<0){
		//mvv.xy+=(mvv.xy)*200;
		//mvv.xy-=toLight.xy;
		mvv.xy-=(lightPos.xy-mvv.xy)*200;
	}
	gl_Position = gl_ProjectionMatrix*mvv;
	gl_Color = gl_BackColor;
}

//fragment
uniform sampler2D tex;
void main()
{
   gl_FragColor = vec4(0.2,0.2,0.2,1.0);
}
