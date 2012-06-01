void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment

uniform sampler2D ambTex;
uniform sampler2D depthTex;
uniform float zNear;
uniform float zFar;
uniform float focus;
uniform float time;
uniform float aspectRatio;

 
 
 float LinearizeDepth(vec2 uv)
{
  float z = texture2D(depthTex, uv).x;
  return (2.0 * zNear) / (zFar + zNear - z * (zFar - zNear));
}
 
 
void main()
{
 		
        vec2 aspectcorrect = vec2(1.0,aspectRatio);
       	vec2 coord = gl_TexCoord[0].xy;
     	float factor = LinearizeDepth(coord)-focus;
         
        vec2 dofblur = vec2(mix(0,0.05,factor/2)); 
        vec4 col = vec4(0.0);
       
        col += texture2D(ambTex, coord);
        col += texture2D(ambTex,coord + (vec2( 0.0,0.4 )*aspectcorrect) * dofblur);
        col += texture2D(ambTex, coord.xy + (vec2( 0.15,0.37 )*aspectcorrect) * dofblur);
        col += texture2D(ambTex, coord.xy + (vec2( 0.29,0.29 )*aspectcorrect) * dofblur);
        col += texture2D(ambTex, coord.xy + (vec2( -0.37,0.15 )*aspectcorrect) * dofblur);       
        col += texture2D(ambTex, coord.xy + (vec2( 0.4,0.0 )*aspectcorrect) * dofblur);   
        col += texture2D(ambTex, coord.xy + (vec2( 0.37,-0.15 )*aspectcorrect) * dofblur);       
        col += texture2D(ambTex, coord.xy + (vec2( 0.29,-0.29 )*aspectcorrect) * dofblur);       
        col += texture2D(ambTex, coord.xy + (vec2( -0.15,-0.37 )*aspectcorrect) * dofblur);
        col += texture2D(ambTex, coord.xy + (vec2( 0.0,-0.4 )*aspectcorrect) * dofblur); 
        col += texture2D(ambTex, coord.xy + (vec2( -0.15,0.37 )*aspectcorrect) * dofblur);
        col += texture2D(ambTex, coord.xy + (vec2( -0.29,0.29 )*aspectcorrect) * dofblur);
        col += texture2D(ambTex, coord.xy + (vec2( 0.37,0.15 )*aspectcorrect) * dofblur); 
        col += texture2D(ambTex, coord.xy + (vec2( -0.4,0.0 )*aspectcorrect) * dofblur); 
        col += texture2D(ambTex, coord.xy + (vec2( -0.37,-0.15 )*aspectcorrect) * dofblur);       
        col += texture2D(ambTex, coord.xy + (vec2( -0.29,-0.29 )*aspectcorrect) * dofblur);       
        col += texture2D(ambTex, coord.xy + (vec2( 0.15,-0.37 )*aspectcorrect) * dofblur);
       
        col += texture2D(ambTex, coord.xy + (vec2( 0.15,0.37 )*aspectcorrect) * dofblur*0.9);
        col += texture2D(ambTex, coord.xy + (vec2( -0.37,0.15 )*aspectcorrect) * dofblur*0.9);           
        col += texture2D(ambTex, coord.xy + (vec2( 0.37,-0.15 )*aspectcorrect) * dofblur*0.9);           
        col += texture2D(ambTex, coord.xy + (vec2( -0.15,-0.37 )*aspectcorrect) * dofblur*0.9);
        col += texture2D(ambTex, coord.xy + (vec2( -0.15,0.37 )*aspectcorrect) * dofblur*0.9);
        col += texture2D(ambTex, coord.xy + (vec2( 0.37,0.15 )*aspectcorrect) * dofblur*0.9);            
        col += texture2D(ambTex, coord.xy + (vec2( -0.37,-0.15 )*aspectcorrect) * dofblur*0.9);   
        col += texture2D(ambTex, coord.xy + (vec2( 0.15,-0.37 )*aspectcorrect) * dofblur*0.9);   
       
        col += texture2D(ambTex, coord.xy + (vec2( 0.29,0.29 )*aspectcorrect) * dofblur*0.7);
        col += texture2D(ambTex, coord.xy + (vec2( 0.4,0.0 )*aspectcorrect) * dofblur*0.7);       
        col += texture2D(ambTex, coord.xy + (vec2( 0.29,-0.29 )*aspectcorrect) * dofblur*0.7);   
        col += texture2D(ambTex, coord.xy + (vec2( 0.0,-0.4 )*aspectcorrect) * dofblur*0.7);     
        col += texture2D(ambTex, coord.xy + (vec2( -0.29,0.29 )*aspectcorrect) * dofblur*0.7);
        col += texture2D(ambTex, coord.xy + (vec2( -0.4,0.0 )*aspectcorrect) * dofblur*0.7);     
        col += texture2D(ambTex, coord.xy + (vec2( -0.29,-0.29 )*aspectcorrect) * dofblur*0.7);   
        col += texture2D(ambTex, coord.xy + (vec2( 0.0,0.4 )*aspectcorrect) * dofblur*0.7);
                         
        col += texture2D(ambTex, coord.xy + (vec2( 0.29,0.29 )*aspectcorrect) * dofblur*0.4);
        col += texture2D(ambTex, coord.xy + (vec2( 0.4,0.0 )*aspectcorrect) * dofblur*0.4);       
        col += texture2D(ambTex, coord.xy + (vec2( 0.29,-0.29 )*aspectcorrect) * dofblur*0.4);   
        col += texture2D(ambTex, coord.xy + (vec2( 0.0,-0.4 )*aspectcorrect) * dofblur*0.4);     
        col += texture2D(ambTex, coord.xy + (vec2( -0.29,0.29 )*aspectcorrect) * dofblur*0.4);
        col += texture2D(ambTex, coord.xy + (vec2( -0.4,0.0 )*aspectcorrect) * dofblur*0.4);     
        col += texture2D(ambTex, coord.xy + (vec2( -0.29,-0.29 )*aspectcorrect) * dofblur*0.4);   
        col += texture2D(ambTex, coord.xy + (vec2( 0.0,0.4 )*aspectcorrect) * dofblur*0.4);       
                       
        gl_FragColor = (col/41.0);
         //gl_FragColor =vec4(factor);
        gl_FragColor.a =1.0;
        
 const vec4  kRGBToYPrime = vec4 (0.299, 0.587, 0.114, 0.0);
    const vec4  kRGBToI     = vec4 (0.596, -0.275, -0.321, 0.0);
    const vec4  kRGBToQ     = vec4 (0.212, -0.523, 0.311, 0.0);

    const vec4  kYIQToR   = vec4 (1.0, 0.956, 0.621, 0.0);
    const vec4  kYIQToG   = vec4 (1.0, -0.272, -0.647, 0.0);
    const vec4  kYIQToB   = vec4 (1.0, -1.107, 1.704, 0.0);

    // Sample the input pixel
    vec4    color   = gl_FragColor;

    // Convert to YIQ
    float   YPrime  = dot (color, kRGBToYPrime);
    float   I      = dot (color, kRGBToI);
    float   Q      = dot (color, kRGBToQ);

    // Calculate the hue and chroma
    float   hue     = atan (Q, I);
    float   chroma  = sqrt (I * I + Q * Q);

    // Make the user's adjustments
    hue += time*2;

    // Convert back to YIQ
    Q = chroma * sin (hue);
    I = chroma * cos (hue);

    // Convert back to RGB
    vec4    yIQ   = vec4 (YPrime, I, Q, 0.0);
    color.r = dot (yIQ, kYIQToR);
    color.g = dot (yIQ, kYIQToG);
    color.b = dot (yIQ, kYIQToB);

    // Save the result
    gl_FragColor    = color;
}

