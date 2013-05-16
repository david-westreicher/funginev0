var m = game.input.mouse;
var size = 100;
var box = 800;
var num = 3;
var firstTime = true;
var coolDown = 0;
var cam = null;
var boxs = new Array();
var player;
var cameraMode = true;
var centerMouse = false;
var movementSpeed = 5;
var terrain;
var playerLight = null;
var playerSize = 100;
var CAM_SPEED = 10;
var cam;

function init(game, factory) {
    game.cam.setPos(0, 100, 500);
    game.cam.rotation[0] = Math.PI;
    // game.hideMouse();
    player = factory.createGameObject("Player");
    player.size[0] = playerSize;
    player.size[1] = playerSize;
    player.size[2] = playerSize;
    player.setPos(0, 0, 0);
    player.friction = 0
    game.world.add(player);
    cam = factory.createGameObject("CamTest");
    cam.setPos(0, 4000, 0);
    cam.rotation[1] = -Math.PI / 2;
    // game.world.add(cam);

    playerLight = factory.createGameObject("CamTest");
    playerLight.setPos(0, 500, 0);
    playerLight.rotation[1] = -Math.PI / 2;
    game.world.add(playerLight);

    /*
     * wall = factory.createGameObject("Wall"); wall.size[0] = 1 wall.size[1] =
     * 50000; wall.size[2] = 50000; wall.setPos(2000, 25000, 0);
     * wall.setFixed(true); game.world.add(wall);
     */
    vid = factory.createGameObject("StaticBox");
    vid.size[0] = 50000
    vid.size[1] = 1;
    vid.size[2] = 50000;
    vid.setPos(0, 0, 0);
    // game.world.add(vid);

    terrain = factory.createGameObject("Terrain");
    terrain.size[0] = 25000;
    terrain.size[1] = 25000;
    terrain.size[2] = 25000;
    terrain.setPos(0, 0, 0);
    terrain.friction = 0;
    terrain.setFixed(true);
    terrain.setColor(1, 1, 1);
    game.world.add(terrain);

    // terrain = factory.createGameObject("VoxelTerrain");
    // terrain.size[0] = 25000;
    // terrain.size[1] = 25000;
    // terrain.size[2] = 25000;
    // terrain.setPos(0, 0, 0);
    // terrain.setColor(1, 1, 1);
    // game.world.add(terrain);

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
    var keyB = game.input.keyboard;
    var rot = game.cam.rotation;
    if (centerMouse) {
        rot[0] = (rot[0] + (game.input.mouse.pos[0] - game.getWidth() / 2) / 2000);
        rot[1] = clamp(rot[1]
                - (game.input.mouse.pos[1] - game.getHeight() / 2) / 2000,
                -Math.PI / 2 + 0.01, Math.PI / 2 - 0.01);
        game.centerMouse();
    }

    var xSpeed = +keyB.isDown('a') - keyB.isDown('d');
    var ySpeed = +keyB.isDown('w') - keyB.isDown('s');
    c0 = Math.cos(rot[1]);
    s0 = Math.sin(rot[1]);
    c1 = Math.cos(rot[0]);
    s1 = Math.sin(rot[0]);
    mov = [ -s1 * c0, s0, c1 * c0 ];
    // turn y
    if (cameraMode) {

        game.cam.pos[0] += Math.cos(game.cam.rotation[0]) * xSpeed
                * movementSpeed * CAM_SPEED;
        game.cam.pos[2] += Math.sin(game.cam.rotation[0]) * xSpeed
                * movementSpeed * CAM_SPEED;

        for ( var i = 0; i < 3; i++)
            game.cam.pos[i] += mov[i] * ySpeed * movementSpeed * CAM_SPEED;
    } else {
        player.setLinearVelocity(Math.cos(game.cam.rotation[0]) * xSpeed
                * movementSpeed + mov[0] * ySpeed * movementSpeed, Math
                .sin(game.cam.rotation[0])
                * xSpeed * movementSpeed + mov[2] * ySpeed * movementSpeed);
        player.setForce(0, (keyB.isDown(' ') ? 1 : player.force[1])
                * movementSpeed * 5, 0);
        for ( var i = 0; i < 3; i++)
            game.cam.pos[i] = player.pos[i];
        game.cam.pos[1] = player.pos[1] + playerSize;
    }

    coolDown++;
    if (coolDown > 20 && game.input.mouse.down && !cameraMode) {
        coolDown = 0;
        objRot = game.cam.rotation[0] + Math.PI / 2;
        objSpeed = 1000
        vid = factory.createGameObject("Sphere");
        vid.size[0] = size
        vid.size[1] = size;
        vid.size[2] = size;
        vid.setPos(game.cam.pos);
        vid.setForce(Math.cos(objRot) * objSpeed, Math
                .sin(game.cam.rotation[1])
                * objSpeed, Math.sin(objRot) * objSpeed)
        // vid.rotation[2]+=Math.PI;
        game.world.add(vid);

    }

    if (keyB.isDown('p') && coolDown > 20) {
        coolDown = 0;
        centerMouse = !centerMouse;
    }
    if (keyB.isDown('m') && coolDown > 20) {
        coolDown = 0;
        cameraMode = !cameraMode;

    }

    if (keyB.isDown('c')) {
        // if (cam == null) {
        cam = factory.createGameObject("CamTest");
        game.world.add(cam);
        // }
        for ( var i = 0; i < 3; i++) {
            cam.rotation[i] = game.cam.rotation[i];
            cam.pos[i] = game.cam.pos[i];
        }
    }
    // for(var i =0;i<boxs.length;i++){
    // boxs[i].rotation[0]+=0.05}

    if (keyB.isDown('x') && coolDown > 20) {
        coolDown = 0;
        startX = Math.random() * 10000 - 5000;
        startY = Math.random() * 10000 - 5000;
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
                    boxs.push(vid);
                    game.world.add(vid);
                }
            }
        }

        firstTime = false;
    }
}

function clamp(val, min, max) {
    return Math.min(Math.max(val, min), max);
}

function onCollision() {

}