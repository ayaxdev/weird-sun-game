package me.ajax.somr;

import me.ajaxdev.dackel.components.AutomaticallyDisposeOfOutOfBoundsObjectsComponent;
import me.ajaxdev.dackel.components.IObjectComponent;
import me.ajaxdev.dackel.components.KeyboardObjectMovementSceneComponent;
import me.ajaxdev.dackel.components.SingleAngleRotationComponent;
import me.ajaxdev.dackel.object.GameObject;
import me.ajaxdev.dackel.scene.ImageScene;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class GameScene extends ImageScene {

    public static final float NORMAL_ENEMY_SIZE = 220, NORMAL_PLAYER_SIZE = 50;
    public static final double ENEMY_SPEED = 0.11, PLAYER_SPEED = 0.22, PARTICLE_SPEED = 0.2;

    private StartingEnlargementComponent enlargementComponent;

    public final List<GameObject> updateParticles = new ArrayList<>();
    public final List<GameObject> movingParticles = new ArrayList<>();

    private GameObject player, enemy;

    public GameScene() {
        super("background");

        this.components.add(new AutomaticallyDisposeOfOutOfBoundsObjectsComponent(this));
    }

    @Override
    public void postShow() {
        final GameObject playerObject = player = new GameObject(getLastApplication().textureManager.get("player"), 20, 20, NORMAL_PLAYER_SIZE, NORMAL_PLAYER_SIZE);

        this.objects.add(playerObject);

        this.components.add(new KeyboardObjectMovementSceneComponent(playerObject, PLAYER_SPEED, GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_D));

        final double width = getLastApplication().display.getWindowWidth(), height = getLastApplication().display.getWindowHeight();
        final double defaultSize = 200;

        final GameObject enemyObject = enemy = new GameObject(getLastApplication().textureManager.get("enemy"), width / 2 - defaultSize / 2, height / 2 - defaultSize / 2, defaultSize, defaultSize);

        this.enlargementComponent = new StartingEnlargementComponent(enemyObject, NORMAL_ENEMY_SIZE, 0.08);

        enemyObject.components.add(enlargementComponent);
        enemyObject.components.add(new EnemyAttackComponent(enemyObject, this, getLastApplication()));
        enemyObject.components.add(new SingleAngleRotationComponent(enemyObject));
        enemyObject.components.add(new EnemyFollowPlayerComponent(enemyObject, playerObject));

        this.objects.add(enemyObject);
    }

    @Override
    public void drawGame(double windowWidth, double windowHeight, double delta) {
        this.movingParticles.removeIf(particle -> !this.objects.contains(particle));

        boolean finished = false;

        enemyComponents: for (final IObjectComponent objectComponent : enemy.components) {
            if (objectComponent instanceof StartingEnlargementComponent component) {
                finished = component.isFinished();

                break enemyComponents;
            }
        }

        enemyComponents: for (final IObjectComponent objectComponent : enemy.components) {
            if (objectComponent instanceof SingleAngleRotationComponent component) {
                if (finished)
                    component.forward(ENEMY_SPEED * delta);

                break enemyComponents;
            }
        }

        this.movingParticles.forEach(particle -> {
            for (final IObjectComponent objectComponent : particle.components) {
                if (objectComponent instanceof SingleAngleRotationComponent rotationComponent) {
                    rotationComponent.forward(PARTICLE_SPEED * delta);

                    return;
                }
            }

            throw new IllegalStateException("Invalid particle was added");
        });

        this.updateParticles.clear();

        super.drawGame(windowWidth, windowHeight, delta);

        final double playerRadius = player.width / 2;
        final double playerCircleX = player.position.x + playerRadius, playerCircleY = player.position.y + playerRadius;

        for (final GameObject particle : this.movingParticles) {
            if (particle.getFramesAlive() < 10)
                continue;

            final double particleRadius = particle.width / 2;
            final double particleCircleX = particle.position.x + particleRadius, particleCircleY = particle.position.y + particleRadius;
            final double usedParticleRadius = particleRadius - 5;

            if (MathUtils.intersects(playerCircleX, playerCircleY, playerRadius, particleCircleX, particleCircleY, usedParticleRadius)) {
                System.out.printf("%d%n", movingParticles.size());
                System.out.printf("Player died as they touched a particle at %f %f %f %f %n", particle.position.x, particle.position.y, particle.width, particle.height);
                getLastApplication().openScene(new GameOverScene());
            }
        }

        final double enemyRadius = enemy.width / 2;
        final double enemyCircleX = enemy.position.x + enemyRadius, enemyCircleY = enemy.position.y + enemyRadius;
        final double usedEnemyRadius = enemyRadius - 5;

        if (MathUtils.intersects(playerCircleX, playerCircleY, playerRadius, enemyCircleX, enemyCircleY, usedEnemyRadius)) {
            System.out.printf("Player died as they touched the enemy%n");
            getLastApplication().openScene(new GameOverScene());
        }

        this.objects.addAll(this.updateParticles);
        this.movingParticles.addAll(this.updateParticles);
    }

}
