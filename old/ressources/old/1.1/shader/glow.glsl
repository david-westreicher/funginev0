void main(void)
{
	gl_Position = gl_ProjectionMatrix*gl_ModelViewMatrix *gl_Vertex; 
	gl_FrontColor = gl_Color;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment

uniform sampler2D tex;

//
//void main() {
 //   gl_FragColor = gl_Color*texture2D(tex, gl_TexCoord[0].xy);
//}

void main()
{
   vec4 col = gl_Color;
   vec4 sum = vec4(0);
   vec2 texcoord = vec2(gl_TexCoord[0]);
   int j;
   int i;

   for( i= -4 ;i < 4; i++)
   {
        for (j = -3; j < 3; j++)
        {
            sum += texture2D(tex, texcoord + vec2(j, i)*0.004) * 0.25;
        }
   }
       if (texture2D(tex, texcoord).r < 0.3)
    {
       col = sum*sum*0.012 + texture2D(tex, texcoord);
    }
    else
    {
        if (texture2D(tex, texcoord).r < 0.5)
        {
            col = sum*sum*0.009 + texture2D(tex, texcoord);
        }
        else
        {
            col = sum*sum*0.0075 + texture2D(tex, texcoord);
        }
    }
	//gamma correction
	//gl_FragColor.rgb = pow(col, 1.0 / 2.2);
	//gl_FragColor.a = col.a;
	gl_FragColor = col;
}