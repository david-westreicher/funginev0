void main(void)
{
	gl_Position = gl_ProjectionMatrix * gl_ModelViewMatrix *gl_Vertex;
	gl_TexCoord[0]= gl_MultiTexCoord0;
}

//fragment
uniform sampler2D diff;
uniform sampler2D normal;
uniform sampler2D position;
uniform vec3 camPos;
uniform vec3 lightPos;
uniform vec3 lightColor;
uniform float lightRadius;
void main()
{
	//gl_FragColor = texture2D(normal,gl_TexCoord[0].xy);
   vec3 Spos = lightPos;
   vec3 Scolor = lightColor;
   vec3 Mspec = vec3(1);
   float Mshi = 100;
   float Sradius = 100;
   vec3 p = texture2D(position,gl_TexCoord[0].xy).xyz;
   vec3 n = texture2D(normal,gl_TexCoord[0].xy).xyz;
   vec3 Mdiff = texture2D(diff,gl_TexCoord[0].xy).xyz;
   
   vec3 l = Spos - p; // light vector
   vec3 v = camPos-p; // view vector
   vec3 h = normalize(v + l); // half vector
    
   // attenuation (equation 2)
   float att = clamp(1.0 - length(l)/Sradius,0,1);
   l = normalize(l);
   vec3 Idiff = clamp((dot(l,n))*Mdiff*Scolor,0,1);
   vec3 Ispec = pow(clamp(dot(h,n),0,1),Mshi)*Mspec*Scolor;
   gl_FragColor = vec4(att*(Idiff + Ispec),1);
   gl_FragColor.a = 1;

}
