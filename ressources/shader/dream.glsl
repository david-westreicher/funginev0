void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment


// Scene buffer
uniform sampler2D tex;
uniform float time;

void main(void)
{
  vec2 uv = gl_TexCoord[0].xy;
  vec4 c = texture2D(tex, uv);

  c += texture2D(tex, uv+0.001);
  c += texture2D(tex, uv+0.003);
  c += texture2D(tex, uv+0.005);
  c += texture2D(tex, uv+0.007);
  c += texture2D(tex, uv+0.009);
  c += texture2D(tex, uv+0.011);

  c += texture2D(tex, uv-0.001);
  c += texture2D(tex, uv-0.003);
  c += texture2D(tex, uv-0.005);
  c += texture2D(tex, uv-0.007);
  c += texture2D(tex, uv-0.009);
  c += texture2D(tex, uv-0.011);

  c.rgb = vec3((c.r+c.g+c.b)/3.0);
  float time2 = mod(time,50.0)/50.0;
  float time3 = time2;
  if(time2>0.5){
  	time3=1.0-time2;}
  c = c / (9.5+time3*8);
  gl_FragColor = c;
}