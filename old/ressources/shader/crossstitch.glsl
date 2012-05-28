void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment


// Scene buffer
uniform sampler2D tex; 

vec2 resolution = vec2(800,600);
float stitching_size = 10.0;
int invert = 1;

vec4 PostFX(sampler2D tex, vec2 uv)
{
  vec4 c = vec4(0.0);
  float size = stitching_size;
  vec2 cPos = uv * resolution;
  vec2 tlPos = floor(cPos / vec2(size, size));
  tlPos *= size;
  int remX = int(mod(cPos.x, size));
  int remY = int(mod(cPos.y, size));
  if (remX == 0 && remY == 0)
    tlPos = cPos;
  vec2 blPos = tlPos;
  blPos.y += (size - 1.0);
  if ((remX == remY) ||
     (((int(cPos.x) - int(blPos.x)) == (int(blPos.y) - int(cPos.y)))))
  {
    if (invert == 1)
      c = vec4(0.2, 0.15, 0.05, 1.0);
    else
      c = texture2D(tex, tlPos * vec2(1.0/resolution.x, 1.0/resolution.y)) * 1.4;
  }
  else
  {
    if (invert == 1)
      c = texture2D(tex, tlPos * vec2(1.0/resolution.x, 1.0/resolution.y)) * 1.4;
    else
      c = vec4(0.0, 0.0, 0.0, 1.0);
  }
  return c;
}

void main(void)
{ vec2 uv = gl_TexCoord[0].st;
    gl_FragColor = PostFX(tex, uv);
}