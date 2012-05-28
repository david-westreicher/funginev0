var num = 20;
var num2 = 0;
var sprites = new Array();
var lag = false;
var turnoff = false;
var vid;
function init(game,factory){
	vid = factory.createGameObject("Sprite");
		vid.size[0] =100;
		vid.size[1] = 100;
		vid.size[2] = 200;
		vid.setPos(0,0);
		game.world.add(vid);
	for(var i =0;i<num2;i++){
		var anim = factory.createGameObject("Sprite3");
		anim.size[0] = 100;
		anim.size[1] = 100;
		anim.size[2] = 200;
		anim.setPos((i%40)*30,Math.floor(i/100)*80);
		anim.animSprite = length(anim.pos[0]-400,anim.pos[1]-300)/40;
		game.world.add(anim);
	}
	if(turnoff)
		num=1;
	for(var i =0;i<num;i++){
	 	sprite = factory.createGameObject("Sprite3");
		sprite.size[0] = 600;
		sprite.size[1] = 400;
		sprite.size[2] = 500;
		sprite.setPos(250+i,250);
		if(lag)
			sprite.pos[2] = -i;
		game.world.add(sprite);
		sprites[i] = sprite;
	}
}

function length(x,y){
	return Math.sqrt(x*x+y*y);
}
function update(game){
	sprites[0].pos[0] = game.input.mouse.pos[0];
	sprites[0].pos[1] = game.input.mouse.pos[1];
	if(game.input.mouse.down)
		sprites[0].rotation += 6;
	if(!turnoff){
		var rotDiff =sprites[0].rotation- sprites[0].oldRotation;
		var pos = sprites[0].pos.slice(0,2);
		var oldPos = sprites[0].oldPos.slice(0,2);
		var diff = [pos[0]-oldPos[0],pos[1]-oldPos[1]];
		for(var i =0;i<num-1;i++){
			var sprite  = sprites[i+1];
			var other  = sprites[i];
			if(lag){
				sprite.pos[0] = other.oldPos[0];
				sprite.pos[1] = other.oldPos[1];
				sprite.rotation = other.oldRotation;
			}
			else{
				sprite.pos[0] = (i/num)*diff[0]+oldPos[0];
				sprite.pos[1] = (i/num)*diff[1]+oldPos[1];
				sprite.rotation = rotDiff*(i/num)+sprites[0].oldRotation;
			}
		}
		if(lag)diff = [sprites[0].pos[0]-sprites[num-1].pos[0],sprites[0].pos[1]-sprites[num-1].pos[1]];
		game.setBoolean("shader",(Math.abs(diff[0])+Math.abs(diff[1])+Math.abs(rotDiff))/60+1);
	}
}

function onCollision(){

}