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
 
const float bias = 0.2; //aperture - bigger values for shallower depth of field
//uniform float focus;  // this value comes from ReadDepth script.
 
 
 float LinearizeDepth(vec2 uv)
{
  float z = texture2D(depthTex, uv).x;
  return (2.0 * zNear) / (zFar + zNear - z * (zFar - zNear));
}
 
 
void main()
{
 
        float aspectratio = 800.0/600.0;
        vec2 aspectcorrect = vec2(1.0,aspectratio);
       vec2 coord = gl_TexCoord[0].xy;
     float   factor = LinearizeDepth(coord)-focus;
         
        vec2 dofblur = vec2(mix(-0.01,0.18,factor/5)); 
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
        gl_FragColor.a = 1.0;
        
        
}


