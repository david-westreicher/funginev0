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
vec2 resolution = vec2(800,600);

void main(void)
{
float time2 = time/10;
    vec2 p = -1.0 + 2.0 * gl_FragCoord.xy / resolution.xy;
    vec2 uv;
   
    float a = atan(p.y,p.x);
    float r = sqrt(dot(p,p));

    uv.x =          7.0*a/3.1416;
    uv.y = -time2+ sin(7.0*r+time2) + .7*cos(time2+7.0*a);

    float w = .5+.5*(sin(time2+7.0*r)+ .7*cos(time2+7.0*a));

    vec3 col =  texture2D(tex,uv*.5).xyz;

    gl_FragColor = vec4(col*w,1.0);
}