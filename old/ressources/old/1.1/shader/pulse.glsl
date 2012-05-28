void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment


// Scene buffer
uniform sampler2D tex; 

uniform vec2 center;
uniform float time;
vec2 resolution = vec2(800,600);
float radius = 200;

void main(void)
{

  vec2 tc = gl_TexCoord[0].st * resolution;
  tc -= center;
  float dist = length(tc);
  
   vec3 col= texture2D(tex,gl_TexCoord[0].st).xyz;
  if (dist < radius)
  {
	float time2 = time/100;
    vec2 halfres = resolution.xy/2.0;
    vec2 cPos = gl_FragCoord.xy;



    cPos.x -= halfres.x;
    cPos.y -= halfres.y;
    float cLength = length(cPos);

    vec2 uv = gl_FragCoord.xy/resolution.xy+(cPos/cLength)*sin(cLength/30.0-time2*10.0)/25.0;
    col = texture2D(tex,uv).xyz;//*50.0/cLength;

  }
    gl_FragColor = vec4(col,1.0);
}