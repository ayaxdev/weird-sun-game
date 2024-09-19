package me.ajax.somr;

import me.ajaxdev.dackel.scene.ImageScene;

public class GameOverScene extends ImageScene {

    public GameOverScene() {
        super("death");
    }

    public void mouseClicked(double x, double y, int button) {
        System.exit(0);
    }

    public void keyPressed(int keyCode, int scancode) {
        System.exit(0);
    }

}
