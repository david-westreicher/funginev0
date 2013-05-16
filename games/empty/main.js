var centerMouse = false;
var rot = game.cam.rotation;
var keyB = game.input.keyboard;
var movementSpeed = 1;
var coolDown = 0;

function init(game, factory) {/*
    bla = factory.createGameObject("PointLight");
    bla.size[0] = 150;
    bla.size[1] = 150;
    bla.size[2] = 150;
    game.world.add(bla);*/
}

function update(game) {
    camera();

    if (keyB.isDown('p') && coolDown > 20) {
        coolDown = 0;
        centerMouse = !centerMouse;
        game.hideMouse(centerMouse);
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


function clamp(val, min, max) {
    return Math.min(Math.max(val, min), max);
}