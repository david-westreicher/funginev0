var speed=parseFloat(object.get("speed"));
var num = parseInt(object.get("num"));
speed+=(game.input.getKey(num,"up")-game.input.getKey(num,"down"))*0.2;
object.pos[1] += speed;
object.pos[1]=Math.max(Math.min(50-object.size[1]/2,object.pos[1]),-50+object.size[1]/2);
object.rotation[1]+=0.005
object.size[0]=5+Math.abs(speed)*2;
object.size[1]=20-Math.abs(speed)*3;
speed*=0.91;
object.set("speed",speed);