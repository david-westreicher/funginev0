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
uniform vec2 center;


// GeeXLab built-in uniform, width of
// the current render target
float rt_w = 800.0;
// GeeXLab built-in uniform, height of
// the current render target
float rt_h = 600.0; 

// Swirl effect parameters
float radius = 100.0;
float angle = 2.0;
float speed = 70;

vec4 PostFX(sampler2D tex, vec2 uv,float time)
{
	//time = 0.1;
  vec2 texSize = vec2(rt_w, rt_h);
  vec2 tc = uv * texSize;
  tc -= center;
  float dist = length(tc);
  float time2 = time;
  
  if (dist < radius)
  {
    float percent = (radius - dist) / radius;
    if(time>0.5){
    time2=1.0-time;}
    time2-=0.25;
    float theta = percent * percent * angle * 8.0*time2;
    float s = sin(theta);
    float c = cos(theta);
    tc = vec2(dot(tc, vec2(c, -s)), dot(tc, vec2(s, c)));
  }
  tc += center;
  vec3 color = texture2D(tex, tc / texSize).rgb;
  return vec4(color, 1.0);
}

void main (void)
{
  vec2 uv = gl_TexCoord[0].st;
  gl_FragColor = PostFX(tex, uv,mod(time,speed)/speed);
}