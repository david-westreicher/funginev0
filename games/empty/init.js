//add components:
//game.addComponent("renderer")
//game.addComponent("world")
//game.addComponent("input")
//game.addComponent(...)

function init(game, factory) {
    game.addComponent("gamemechanics");
    game.addComponent("deferredrenderer");
    game.loop.renderer.ZFAR_DISTANCE = 5000;
    // game.exit();
}