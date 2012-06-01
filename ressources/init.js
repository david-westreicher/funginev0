//add components:
//game.addComponent("renderer")
//game.addComponent("world")
//game.addComponent("input")
//game.addComponent(...)

function init(game, factory) {
    game.addComponent("gamemechanics");
    game.addComponent("physics");
    game.addComponent("renderer");
    // game.exit();
}