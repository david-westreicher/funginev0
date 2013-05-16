var coolDown = 0;
var centerMouse = false;
var movementSpeed = 100;
var ballSpeed = 1;
var ballSize = 5;
var ballXSpeed = 1;
var ballYSpeed = 1;
var ball;
var player1;
var player2;
var pauseGame;
var keyB = game.input.keyboard;
var rot = game.cam.rotation;
var light;
var terrain;
var size = 100;
var num = 3;
var shuttle;

function init(game, factory) {
    light = factory.createGameObject("CamTest");
    light.set("angle",Math.PI/4);
    light.set("angleSpeed",0.005);
    light.setPos(1000,800,1000);
    light.set("distance",2000);
    light.setColor(1,1,1);
    light.setRadius(5000);
    game.world.add(light);
    
//    
//    
//    
//    
//    game.cam.setPos(0, 0, 100);
//    player1 = factory.createGameObject("Player");
//    player1.setPos(-80, 0, 2.5);
//    player1.setSize(5, 20, 5);
//    player1.set("speed", 0);
//    player1.set("num", 0);
//    game.world.add(player1);
//    player2 = factory.createGameObject("Player");
//    player2.setPos(80, 0, 2.5);
//    player2.setSize(5, 20, 5);
//    player2.set("speed", Math.PI);
//    player2.set("num", 1);
//    game.world.add(player2);
//    ball = factory.createGameObject("Ball");
//    reset();
//    ball.setSize(ballSize, ballSize, ballSize);
//    game.world.add(ball);

    /*
     var bg = factory.createGameObject("Background");
     bg.size[0] = 2000;
     bg.size[1] = 2000;
     bg.size[0] = 2000;
     game.world.add(bg);*/

//    var bg = factory.createGameObject("BackgroundBox");
//    bg.size[0] = 200;
//    bg.size[1] = 120;
//    bg.size[2] = 1;
//    game.world.add(bg);

//    light = factory.createGameObject("CamTest");
//    light.setColor(1,1,1);
//    light.setPos(100, 50, 100);
//    light.rotation[0] = -Math.PI / 8;
//    light.rotation[1] = Math.PI / 5;
//    game.world.add(light);
    

    for(var i=0;i<1;i++){
        for(var j=0;j<1;j++){
            for(var k=0;k<1;k++){
                var rand = Math.random();
                var objName = (rand<0.33)?"Shuttle":((rand<0.66)?"Dog":"Cat");
    shuttle = factory.createGameObject(objName);
    shuttle.setColor(1,1,1);
    shuttle.setPos(500*i-500*3/2,500*j,500*k-500*3/2);
    shuttle.size[0] = 500;
    shuttle.size[1] = 500;
    shuttle.size[2] = 500;
    shuttle.rotation[1] = Math.random()*2;
    game.world.add(shuttle);
    }}}

    /*
    statue = factory.createGameObject("Sponza");
    statue.setColor(1,1,1);
    statue.setPos(0,1400,0);
    statue.size[0] = 10000;
    statue.size[1] = 10000;
    statue.size[2] = 10000;
    game.world.add(statue);
    temple = factory.createGameObject("Temple");
    temple.setColor(1,1,1);
    temple.setPos(4000,-100,4000);
    temple.size[0] = 5000;
    temple.size[1] = 5000;
    temple.size[2] = 5000;
    game.world.add(temple);*/
    
    
    terrain = factory.createGameObject("Terrain");
    terrain.size[0] = 15000;
    terrain.size[1] = 15000;
    terrain.size[2] = 15000;
   // terrain.rotation[0] =  Math.PI/2;
    terrain.setPos(0, -1300, 0);
    terrain.friction = 0;
    terrain.setFixed(true);
    terrain.setColor(1, 1, 1);
    game.world.add(terrain);

    skyboxl = factory.createGameObject("Skybox-l");
    skyboxl.size[0] = 1;
    skyboxl.size[1] = 1;
    skyboxl.size[2] = 1;
    skyboxl.rotation[1] = Math.PI;
    skyboxl.rotation[2] = Math.PI;
    skyboxl.setPos(0, 0, -0.5);
    game.world.add(skyboxl);

    skyboxr = factory.createGameObject("Skybox-r");
    skyboxr.size[0] = 1;
    skyboxr.size[1] = 1;
    skyboxr.size[2] = 1;
    skyboxr.rotation[2] = Math.PI;
    skyboxr.rotation[1] = Math.PI * 2;
    skyboxr.setPos(0, 0, 0.5);
    game.world.add(skyboxr);

    skyboxu = factory.createGameObject("Skybox-u");
    skyboxu.size[0] = 1;
    skyboxu.size[1] = 1;
    skyboxu.size[2] = 1;
    skyboxu.rotation[0] = Math.PI / 2;
    skyboxu.rotation[2] = Math.PI / 2;
    skyboxu.setPos(0, 0.5, 0);
    game.world.add(skyboxu);

    skyboxb = factory.createGameObject("Skybox-b");
    skyboxb.size[0] = 1;
    skyboxb.size[1] = 1;
    skyboxb.size[2] = 1;
    skyboxb.rotation[0] = Math.PI;
    skyboxb.rotation[1] = Math.PI * 1.5;
    skyboxb.setPos(0.5, 0, 0);
    game.world.add(skyboxb);

    skyboxd = factory.createGameObject("Skybox-d");
    skyboxd.size[0] = 1;
    skyboxd.size[1] = 1;
    skyboxd.size[2] = 1;
    skyboxd.rotation[0] = Math.PI / 2;
    skyboxd.rotation[1] = Math.PI;
    skyboxd.rotation[2] = Math.PI / 2;
    skyboxd.setPos(0, -0.5, 0);
    game.world.add(skyboxd);

    skyboxf = factory.createGameObject("Skybox-f");
    skyboxf.size[0] = 1;
    skyboxf.size[1] = 1;
    skyboxf.size[2] = 1;
    skyboxf.rotation[1] = Math.PI / 2;
    skyboxf.rotation[2] = Math.PI;
    skyboxf.setPos(-0.5, 0, 0);
    game.world.add(skyboxf);
}

function update(game) {
    camera();
    /*if (pauseGame) {
        ball.pos[0] += ballXSpeed;
        ball.pos[1] += ballYSpeed;
        if (ball.pos[0] < -100 || ball.pos[0] > 100) {
            reset();
        }
        if (ball.pos[1] < -50) {
            ball.pos[1] = -50;
            ballYSpeed *= -1;
            ballAnimate();
        }
        if (ball.pos[1] > 50) {
            ball.pos[1] = 50;
            ballYSpeed *= -1;
            ballAnimate();
        }
        player1.updateBbox();
        player2.updateBbox();
        ball.updateBbox();
        if (ball.pos[0] < -75 && ball.pos[0] > -80 && hit(player1, ball)) {
            ball.pos[0] = -75;
            ballXSpeed *= -1;
            ballAnimate();
        }
        if (ball.pos[0] > 75 && ball.pos[0] < 80 && hit(player2, ball)) {
            ball.pos[0] = 75;
            ballXSpeed *= -1;
            ballAnimate();
        }
        ball.size[0] = ballSize;
        ball.size[1] = ballSize;
        ball.size[2] = ballSize;
        var bounceVal = bounce(Math.min(coolDown, 50), 50);
        ballSize = 5 * bounceVal + (1 - bounceVal) * 10;
        if (!centerMouse) {
            game.cam.pos[0] = (1 - bounceVal)
                    * Math.abs(ballXSpeed * ballSpeed * 4);
            game.cam.pos[1] = (1 - bounceVal)
                    * Math.abs(ballYSpeed * ballSpeed * 4);
        }
        var colorVal = linear(Math.min(coolDown, 20), 20);
        ball.setColor(colorVal, colorVal, colorVal);
        ball.size[0] += ballSpeed;
        ball.rotation[2] = Math.atan2(ballYSpeed, ballXSpeed);
        ballSpeed += 0.001;
    } */

    if (keyB.isDown('p') && coolDown > 20) {
        coolDown = 0;
        centerMouse = !centerMouse;
        game.hideMouse(centerMouse);
        // if (!centerMouse) {
        // game.cam.setPos(0, 0, 100);
        // game.cam.rotation[0] = 0;
        // game.cam.rotation[1] = 0;
        // game.cam.rotation[2] = 0;
        // }
    }
    if (keyB.isDown('o') && coolDown > 20) {
        coolDown = 0;
        pauseGame = !pauseGame;
    }
    if (keyB.isDown('c') && coolDown > 20) {
        coolDown = 0;
//        if (light == null) {
            light = factory.createGameObject("CamTest");
            light.set("angle",0);
            light.set("angleSpeed",Math.random()*0.01);
//        }
        for ( var i = 0; i < 3; i++) {
            light.rotation[i] = game.cam.rotation[i];
            light.pos[i] = game.cam.pos[i];
            light.size[i] = 20;
            //light.color[i] = 1;
            // cam.pos[1] += 50;
        }
        light.radius = 5000;

        light.set("distance",Math.sqrt(light.pos[0]*light.pos[0]+light.pos[2]*light.pos[2]));
        game.world.add(light);
    }
    
    if (keyB.isDown('x') && coolDown > 20) {
        coolDown = 0;
        startX = Math.random() * 100 - 50;
        startY = Math.random() * 100 - 50;
        for ( var i = 0; i < num; i++) {
            for ( var j = 0; j < num; j++) {
                for ( var k = 0; k < num; k++) {
                    vid = factory.createGameObject("Box");
                    vid.size[0] = size
                    vid.size[1] = size;
                    vid.size[2] = size;
                    vid.friction = 0;
                    vid.setPos(startX + i * (size + 10), j
                            * (size + 10 + j * 20) + 3000, startY + k
                            * (size + 10));
                    game.world.add(vid);
                }
            }
        }

        firstTime = false;
    }
    
    
    coolDown++;
}
function camera() {
    if (centerMouse) {
        rot[1] = (rot[1] - (game.input.mouse.pos[0] - game.getWidth() / 2) / 2000);
        rot[0] = clamp(rot[0]
                - (game.input.mouse.pos[1] - game.getHeight() / 2) / 2000,
                -Math.PI / 2 + 0.01, Math.PI / 2 - 0.01);
        var xSpeed = -keyB.isDown('a') + keyB.isDown('d');
        var ySpeed = keyB.isDown('s') - keyB.isDown('w');
        c0 = Math.cos(rot[0]);
        s0 = Math.sin(rot[0]);
        c1 = Math.cos(rot[1]);
        s1 = Math.sin(rot[1]);
        mov = [ s1 * c0, -s0, c1 * c0 ];
        // turn y
        game.cam.pos[0] += Math.cos(game.cam.rotation[1]) * xSpeed
                * movementSpeed;
        game.cam.pos[2] -= Math.sin(game.cam.rotation[1]) * xSpeed
                * movementSpeed;
        for ( var i = 0; i < 3; i++){
            game.cam.pos[i] += mov[i] * ySpeed * movementSpeed;
//            shuttle.pos[i] = game.cam.pos[i];
        }
//        shuttle.rotation[2] = -game.cam.rotation[0];
//        shuttle.rotation[1] = game.cam.rotation[1]-Math.PI/2;
//        shuttle.pos[1]-=300;
//        shuttle.pos[0]-=Math.cos(shuttle.rotation[1])*300;
//        shuttle.pos[2]+=Math.sin(shuttle.rotation[1])*300;
//        shuttle.setPos(shuttle.pos);
        game.centerMouse();
    }
}
function ballAnimate() {
    coolDown = 0;
}
function hit(player, ball) {
    var bbox1 = player.bbox;
    var bbox2 = ball.bbox;
    if ((bbox1[1] >= bbox2[0]) && (bbox1[0] <= bbox2[1])
            && (bbox1[3] >= bbox2[2]) && (bbox1[2] <= bbox2[3])) {
        ballYSpeed -= (player.pos[1] - ball.pos[1]) / 10;
        return true;
    } else
        return false
}

function reset() {
    ballSpeed = 1;
    ballSize = 5;
    ball.setPos(0, 0, 2.5);
    var angle = Math.random() * Math.PI * 2;
    ballXSpeed = Math.sin(angle);
    ballYSpeed = Math.cos(angle);
    if (Math.abs(ballXSpeed) < 0.5) {
        ballXSpeed = ballYSpeed;
        ballYSpeed = Math.sin(angle);
    }
    ballXSpeed *= ballSpeed;
    ballYSpeed *= ballSpeed;
}

function bounce(t, d) {
    var ts = (t /= d) * t;
    var tc = ts * t;
    return (56 * tc * ts + -175 * ts * ts + 200 * tc + -100 * ts + 20 * t);
}
function linear(t, d) {
    t /= d;
    return (t);
}

function clamp(val, min, max) {
    return Math.min(Math.max(val, min), max);
}