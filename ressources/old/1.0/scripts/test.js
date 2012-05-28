//getting:
//	*object
//	*game
//	*log
//  *factory
var speed = 10;
var threeD = false;
if(threeD){
	object.rotation+=speed;
	object.zrotation+=speed;
}
object.pos[1]+=speed;
if(object.pos[1]>object.get("maxY")){
game.world.remove(object);
}
/*
var x = parseFloat(object.get("x"));
if(!threeD){
	object.size[0] = Math.abs(Math.sin(x))*50;
}
object.set("x",x+0.1);*/