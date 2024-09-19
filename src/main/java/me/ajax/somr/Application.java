package me.ajax.somr;

import me.ajaxdev.dackel.ApplicationArgs;
import me.ajaxdev.dackel.texture.SimpleTexture;

public class Application extends me.ajaxdev.dackel.Application {

    public static final Application MAIN_INSTANCE = new Application();

    public Application() {
        super(ApplicationArgs.builder()
                .setTitle("Sun of May RAMPAGE")
                .setWidth((int) (800 / 0.85))
                .setHeight((int) (500 / 0.85))
                .setScene(new GameScene())
                .setAntialiasing(4)
                .setVsync(true)
                .build());
    }

    @Override
    protected void preLoop() {
        this.textureManager.order("death", new SimpleTexture("/death.png"));
        this.textureManager.order("particle", new SimpleTexture("/particle.png"));
        this.textureManager.order("background", new SimpleTexture("/flag.png"));
        this.textureManager.order("player", new SimpleTexture("/player.png"));
        this.textureManager.order("enemy", new SimpleTexture("/sun_of_may.png"));
    }

    public static void main(String[] args) {
        if (MAIN_INSTANCE.run()) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

}