var all = new Array();
var speeds = new Array();
var m = game.input.mouse;
var size = 100
var box = 700;
var num =60;

function init(game,factory){
game.cam.setPos(0,100,0);
game.hideMouse();
	for(var i=0;i<num;i++){
		var rand = Math.random();
		if(rand<0.3)
		vid = factory.createGameObject("bunny");
		else if(rand<0.6)
		vid = factory.createGameObject("minecraft");
		else
		vid = factory.createGameObject("overg");
		vid.size[0] =size;
		vid.size[1] = size;
		vid.size[2] = size;
		vid.setPos(Math.random()*box,Math.random()*box,Math.random()*box);
		//vid.setPos(0,0,0);
		//vid.rotation[2]+=Math.PI;
		game.world.add(vid);
		all.push(vid);
		speeds.push([Math.random()-0.5,Math.random()-0.5,Math.random()-0.5]);
	}		
}


function update(game){

var keyB = game.input.keyboard;
var rot = game.cam.rotation;
if(game.input.mouse.down){
	rot[0]=(rot[0]+(game.input.mouse.pos[0]-400)/2000);
	rot[1]=clamp(rot[1]-(game.input.mouse.pos[1]-306)/2000,-Math.PI/2+0.01,Math.PI/2-0.01);
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

for(var i=0;i<num;i++){
	all[i].rotation[1]+=speeds[i][0]/5;
	all[i].rotation[0]+=speeds[i][1]/5;
	var pos = all[i].pos;
	pos[0]+=speeds[i][0]*5;
	pos[1]+=speeds[i][1]*5;
	pos[2]+=speeds[i][2]*5;
	var bool = false;
	if(pos[0]>box){
		pos[0] = 0
		bool = true;
	}
	if(pos[1]>box){
		pos[1] = 0
		bool = true;
	}
	if(pos[2]>box/2){
		pos[2] = -box/2
		bool = true;
	}
		
	if(pos[0]<0){
		pos[0] = box
		bool = true;
	}
	if(pos[1]<0){
		pos[1] = box
		bool = true;
	}
	if(pos[2]<-box/2){
		pos[2] =box/2
		bool = true;
	}
	if(bool)
		all[i].oldPos=[pos[0],pos[1],pos[2]];
}}


function clamp(val, min, max){
return  Math.min(Math.max(val,min),max);
}

function onCollision(){

}