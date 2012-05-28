var speed = 5;
var rand = true;
var randSpeed = 0.5;
var maxlife = 100;
if(rand){
	//update angles
	var angle=parseFloat(object.get("angle"));
	var angleSpeed=parseFloat(object.get("angleSpeed"));
	angle+=Math.sin(angleSpeed)*speed;
	object.set("angleSpeed",angleSpeed+Math.random()*randSpeed-0.5*randSpeed);
	object.set("angle",angle);

	//update object
	object.rotation =angle;
	var ang = angle*Math.PI/180;
	object.pos[0]+=Math.sin(ang)*speed;
	object.pos[1]-=Math.cos(ang)*speed;
}else{
	var ang = object.rotation*Math.PI/180;
	object.pos[0]+=Math.sin(ang)*speed;
	object.pos[1]-=Math.cos(ang)*speed;
}
var life = parseInt(object.get("life"));
object.alpha=life/maxlife;
if(life<0)
	game.world.remove(object);
else
	object.set("life",life-1);
