var speed = 0.3;
//object.pos[0]+=(game.input.mouse.pos[0]-object.pos[0])*speed;
//object.pos[1]+=(game.input.mouse.pos[1]-object.pos[1])*speed;
var cd =parseInt(object.get("coolDown"));
var mPos = game.input.mouse.pos;
object.rotation=Math.atan2(300-mPos[1],400-mPos[0])*180/Math.PI-90;
if(cd>0)
	object.set("coolDown",cd-1);

if(game.input.mouse.down&&cd==0){
	var b = factory.createGameObject("Bullet");
	b.setPos( object.pos[0], object.pos[1]);
	b.size[0] = 32;
	b.size[1] = 32;
	var angle = object.rotation;
	b.setRotation(angle);
	b.set("angle",angle);
	b.set("angleSpeed",Math.random()*10-5);
	b.set("life",100);
	game.world.add(b);
	object.set("coolDown",10);
}
if(game.input.keyboard.isDown('x')==1.0&&cd==0){
var b = factory.createGameObject("Light");
	b.setPos( object.pos[0], object.pos[1]);
	//b.color[3] = 0.5;
	
	var angle = object.rotation;
	b.setRotation(angle);
	b.set("angle",angle);
	b.set("angleSpeed",Math.random()*10-5);
	b.set("life",100);
	game.world.add(b);
	object.set("coolDown",10);
}