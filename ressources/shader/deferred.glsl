#version 140
#define SIZE 30
uniform vec3 scaleArr[SIZE];
uniform vec3 translateArr[SIZE];
uniform vec3 translateOldArr[SIZE];
uniform mat3 rotationMatrices[SIZE];
uniform vec3 colorArr[SIZE];
uniform float interp;

varying vec3 normal;
varying vec4 pos,projectedPos;


mat4 getTransformMatrix(){
	vec3 scale = scaleArr[gl_InstanceID];
	vec3 translate  = translateOldArr[gl_InstanceID]*(1-interp)+translateArr[gl_InstanceID]*(interp);
	mat4 R = mat4(	vec4(rotationMatrices[gl_InstanceID][0],0),
					vec4(rotationMatrices[gl_InstanceID][1],0),
					vec4(rotationMatrices[gl_InstanceID][2],0),
					vec4(0,0,0,1));
	mat4 S = mat4(	vec4(scale.x,0,0,0),
              		vec4(0,scale.y,0,0),
              		vec4(0,0,scale.z,0),
              		vec4(0,0,0,1) );
	mat4 T = mat4(	vec4(1,0,0,0),
              		vec4(0,1,0,0),
              		vec4(0,0,1,0),
              		vec4(translate,1));
 	return T*R*S;
}

void main()
{
	mat4 transformMatrix = getTransformMatrix();
	normal =  normalize( ( transpose(inverse(transformMatrix))  * vec4(gl_Normal,0) ).xyz);
	pos = transformMatrix * gl_Vertex;
	gl_Position = gl_ProjectionMatrix *  gl_ModelViewMatrix *   pos;
	gl_FrontColor = gl_Color * vec4(colorArr[gl_InstanceID],1);
	gl_TexCoord[0]= gl_MultiTexCoord0;
}
//fragment 
#version 140

uniform sampler2D tex;
uniform float shininess; 
varying vec3 normal;
varying vec4 pos,projectedPos;


void main()
{
	gl_FragData[0] = gl_Color * texture2D(tex,gl_TexCoord[0].xy);
	gl_FragData[1] = vec4(normal,shininess);
	gl_FragData[2] = pos;
}
