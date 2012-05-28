
var m = game.input.mouse;
var size = 100;
var box = 800;
var num =4;
var width = 1024;
var height = 768;
var firstTime = true;
var coolDown = 0;
var cam;
var boxs = new Array();

function init(game,factory){
game.cam.setPos(0,100,500);
game.cam.rotation[0]=Math.PI;
game.hideMouse();
	
	cam = factory.createGameObject("CamTest");
	cam.size[1]=50;
	cam.setPos(-100,size*num/2,-100);
	//vid.rotation[2]+=Math.PI;
	game.world.add(cam);
}


function update(game){
cam.rotation[0]+=0.05;

var keyB = game.input.keyboard;
var rot = game.cam.rotation;
if(game.input.mouse.down){
	rot[0]=(rot[0]+(game.input.mouse.pos[0]-width/2)/2000);
	rot[1]=clamp(rot[1]-(game.input.mouse.pos[1]-height/2-6)/2000,-Math.PI/2+0.01,Math.PI/2-0.01);
	game.centerMouse();
}

//turn y
c0 = Math.cos(rot[1]);
s0 = Math.sin(rot[1]);
c1 = Math.cos(rot[0]);
s1 = Math.sin(rot[0]);
mov = [-s1*c0,s0,c1*c0];
	
var xSpeed = +keyB.isDown('a')-keyB.isDown('d');
var ySpeed = +keyB.isDown('w')-keyB.isDown('s');
game.cam.pos[0]+= Math.cos(game.cam.rotation[0])*xSpeed*10;
game.cam.pos[2]+= Math.sin(game.cam.rotation[0])*xSpeed*10;

for(var i=0;i<3;i++)
	game.cam.pos[i]+=mov[i]*ySpeed*10;
coolDown++;
if(coolDown>20&&keyB.isDown(' ')){
	coolDown=0;
		objRot = game.cam.rotation[0]+Math.PI/2;
		objSpeed = 1000
	vid = factory.createGameObject("Sphere");
		vid.size[0] =size
		vid.size[1] = size;
		vid.size[2] = size;
		vid.setPos(game.cam.pos);
		vid.setForce(Math.cos(objRot)*objSpeed,Math.sin(game.cam.rotation[1])*objSpeed,Math.sin(objRot)*objSpeed)
		//vid.rotation[2]+=Math.PI;
		game.world.add(vid);

}
//for(var i =0;i<boxs.length;i++){
//boxs[i].rotation[0]+=0.05}

if(firstTime&&keyB.isDown('x')){

	for(var i=0;i<num;i++){
		for(var j=0;j<num;j++){
			for(var k=0;k<num;k++){
		vid = factory.createGameObject("Box");
		vid.size[0] =size
		vid.size[1] = size;
		vid.size[2] = size;
		vid.setPos(i*(size+10),j*(size+10+j*20)+100,k*(size+10));
		boxs.push(vid);
		game.world.add(vid);
		}}}
		
	
	firstTime=false;
}
}


function clamp(val, min, max){
return  Math.min(Math.max(val,min),max);
}

function onCollision(){

}