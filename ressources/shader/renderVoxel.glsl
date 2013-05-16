#version 140
#define SIZE 30
uniform vec3 scaleArr[SIZE];
uniform vec3 translateArr[SIZE];
uniform mat3 rotationMatrices[SIZE];
uniform vec3 colorArr[SIZE];



mat4 getTransformMatrix(){
	vec3 scale = scaleArr[gl_InstanceID];
	vec3 translate = translateArr[gl_InstanceID];
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



uniform float time;
varying float distFromCam;
void main()
{
	mat4 transformMatrix = getTransformMatrix();
	mat4 modelView = gl_ModelViewMatrix * transformMatrix;
	vec4 pos = modelView * gl_Vertex;
	gl_Position = gl_ProjectionMatrix * pos;
	distFromCam = length(pos)/10000;
	gl_PointSize = clamp(sqrt(500/(distFromCam))+10/distFromCam,0,200);
}
//fragment
#version 140


varying float distFromCam;
void main()
{
	gl_FragColor =vec4(0);
}
