var coolDown = 0;
var centerMouse = false;
var movementSpeed = 1;
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

function init(game, factory) {
    game.cam.setPos(0, 0, 100);
    player1 = factory.createGameObject("Player");
    player1.setPos(-80, 0, 2.5);
   player1.setSize(5, 20, 5);
    player1.set("speed", 0);
    player1.set("num", 0);
    game.world.add(player1);
    player2 = factory.createGameObject("Player");
    player2.setPos(80, 0, 2.5);
    player2.setSize(5, 20, 5);
    player2.set("speed", Math.PI);
    player2.set("num", 1);
    game.world.add(player2);
    ball = factory.createGameObject("Ball");
    reset();
    ball.setSize(ballSize, ballSize, ballSize);
    game.world.add(ball);

    // var bg = factory.createGameObject("Background");
    // bg.size[0] = 200;
    // bg.size[1] = 120;
    // game.world.add(bg);

    var bg = factory.createGameObject("BackgroundBox");
    bg.size[0] = 200;
    bg.size[1] = 120;
    bg.size[2] = 1;
    game.world.add(bg);

    var light = factory.createGameObject("CamTest");
    light.setPos(100, 50, 100);
    light.rotation[0] = -Math.PI / 8;
    light.rotation[1] = Math.PI / 5;
    game.world.add(light);
}

function update(game) {
    camera();
    if (pauseGame) {
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
        var colorVal = 1 - linear(Math.min(coolDown, 20), 20);
        ball.setColor(colorVal, colorVal, colorVal);
        ball.size[0] += ballSpeed;
        ball.rotation[2] = Math.atan2(ballYSpeed, ballXSpeed);
        ballSpeed += 0.001;
    }

    if (keyB.isDown('p') && coolDown > 20) {
        coolDown = 0;
        centerMouse = !centerMouse;
        game.hideMouse(centerMouse);
        if (!centerMouse) {
            game.cam.setPos(0, 0, 100);
            game.cam.rotation[0] = 0;
            game.cam.rotation[1] = 0;
            game.cam.rotation[2] = 0;
        }
    }
    if (keyB.isDown('o') && coolDown > 20) {
        coolDown = 0;
        pauseGame = !pauseGame;
    }
    coolDown++;
}
function camera() {
    if (centerMouse) {
        rot[1] = (rot[1] - (game.input.mouse.pos[0] - game.getWidth() / 2) / 2000);
        rot[0] = rot[0] - (game.input.mouse.pos[1] - game.getHeight() / 2)
                / 2000;
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
        for ( var i = 0; i < 3; i++)
            game.cam.pos[i] += mov[i] * ySpeed * movementSpeed;
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