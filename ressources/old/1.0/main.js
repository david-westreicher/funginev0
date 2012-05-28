var player;
var size = 5;
var size2=100;
var sprite2;
function init(game,factory){
  game.loop.renderer.hideCursor(false);
  player = factory.createGameObject("Player");
  player.setPos(400,300);
  player.size[0] = 50;
  player.size[1] = 50;
  /*for(var i=0;i<1;i++){
  var sprite = factory.createGameObject("Sprite");
	sprite.setPos(400*size,300*size);
	sprite.size[0] = 800*size;
	sprite.size[1] = 600*size;
	//sprite.setRotation(Math.random()*360);
	game.world.add(sprite);
  }*/
  for(var i=0;i<size*size*2;i++){
  var sprite = factory.createGameObject("Box");
	sprite.size[0] = size2*Math.random()+size2;
	sprite.size[1] = size2*Math.random()+size2;
	sprite.size[2] = 100*Math.random()+100;
	sprite.setPos(Math.random()*800*size,Math.random()*600*size,sprite.size[2]/2);
	sprite.setRotation(Math.random()*360);
	game.world.add(sprite);
  }
  game.world.add(player);
}
var x=0;
var zoomSpeed = 1.05;
function update(game){
	var cam = game.cam;
	var keyB =game.input.keyboard;
	var xSpeed = +keyB.isDown('a')-keyB.isDown('d');
	var ySpeed = +keyB.isDown('w')-keyB.isDown('s');
	cam.pos[0] -= xSpeed*10;
	cam.pos[1] -= ySpeed*10;
	player.pos[0] = cam.pos[0];
	player.pos[1] = cam.pos[1];
	//sprite2.pos[0] = cam.pos[0];
	//sprite2.pos[1] = cam.pos[1];
	
	var wheel = game.input.mouse.wheel;
	if(wheel>0){
		cam.zoom*=zoomSpeed;
	}
	if(wheel<0){
	cam.zoom/=zoomSpeed;}
	x++;
	if(x>30){
		var e = factory.createGameObject("Enemy");
		e.size[0]=200;
		e.size[1]=200;
		e.size[2]=200;
		e.set("maxY",600*size);
		//e.rotation=40;
		e.set("x",0);
		e.setPos(Math.random()*800*size,0);
		game.world.add(e);
		x=0;
	}
}

function onCollision(){

}