package me.ajax.somr;

import me.ajaxdev.dackel.Application;
import me.ajaxdev.dackel.components.IObjectComponent;
import me.ajaxdev.dackel.components.SingleAngleRotationComponent;
import me.ajaxdev.dackel.object.GameObject;
import me.ajaxdev.dackel.util.Timer;
import me.ajaxdev.dackel.util.Vec2d;

import java.util.function.Supplier;

import static me.ajax.somr.GameScene.NORMAL_ENEMY_SIZE;

public class EnemyAttackComponent implements IObjectComponent {

    public final GameObject parentObject;
    public final GameScene parentScene;
    public final Application parentApplication;

    private boolean lastDisabled = false;
    private final Supplier<Boolean> isEnabled;

    /**
     * Resets after an attack has been finished.
     * Afters 2000ms new attack stage begins.
     */
    private final Timer attackTimer = new Timer();

    private boolean attacking = false;
    private Attack attackStage = Attack.ENLARGE;

    /**
     * Is reset when the size is changing because of the Enlarge attack.
     * The enemy starts shrinking when 2000ms is hit.
     */
    private final Timer enlargeTimer = new Timer();
    private boolean shrinking = false;

    public EnemyAttackComponent(final GameObject parentObject, final GameScene parentScene, final Application parentApplication) {
        this.parentObject = parentObject;
        this.parentScene = parentScene;
        this.parentApplication = parentApplication;

        for (final IObjectComponent component : parentObject.components) {
            if (component instanceof StartingEnlargementComponent enlargementComponent) {
                isEnabled = enlargementComponent::isFinished;

                return;
            }
        }

        throw new IllegalArgumentException("Provided Object doesn't enlarge");
    }

    public void update(double windowWidth, double windowHeight, double delta) {
        if (!isEnabled.get()) {
            lastDisabled = true;

            return;
        }

        // Resets the attack timer once the enemy begins
        if (lastDisabled) {
            attackTimer.reset();

            lastDisabled = false;
        }

        if (!attacking && attackTimer.hasTimeElapsedMs(2000)) {
            attacking = true;

            attackStage = switch (attackStage) {
                case ENLARGE -> Attack.SHOOT_SPIKES;
                case SHOOT_SPIKES -> Attack.ENLARGE;
            };
        }

        if (attacking) {
            switch (attackStage) {
                case ENLARGE -> {
                    final double currentSize = (parentObject.width + parentObject.height) / 2;

                    if (shrinking) {
                        if (currentSize > NORMAL_ENEMY_SIZE) {
                            final double sizeDiff = 0.1 * delta;

                            double nextWidth = currentSize - sizeDiff;
                            double nextHeight = parentObject.height - sizeDiff;

                            if (nextWidth == currentSize || nextHeight == parentObject.height) {
                                nextWidth = NORMAL_ENEMY_SIZE;
                                nextHeight = NORMAL_ENEMY_SIZE;
                            }

                            parentObject.width = nextWidth;
                            parentObject.height = nextHeight;

                            final double positionOffset = sizeDiff / 2;

                            parentObject.position.add(positionOffset, positionOffset);

                            enlargeTimer.reset();
                        } else {
                            shrinking = false;

                            attackTimer.reset();
                            attacking = false;
                        }
                    } else {
                        if (currentSize < Attack.ENLARGE_GOAL_SIZE) {
                            final double sizeDiff = 0.1 * delta;

                            double nextWidth = currentSize + sizeDiff;
                            double nextHeight = parentObject.height + sizeDiff;

                            if (nextWidth == currentSize || nextHeight == parentObject.height) {
                                nextWidth = Attack.ENLARGE_GOAL_SIZE;
                                nextHeight = Attack.ENLARGE_GOAL_SIZE;
                            }

                            parentObject.width = nextWidth;
                            parentObject.height = nextHeight;

                            final double positionOffset = sizeDiff / 2;

                            parentObject.position.sub(positionOffset, positionOffset);

                            enlargeTimer.reset();
                        } else if (enlargeTimer.hasTimeElapsedMs(2000)) {
                            shrinking = true;
                        }
                    }
                }

                case SHOOT_SPIKES -> {
                    for (int angle = 0; angle < 360; angle += 60) {
                        final double enemySize = (parentObject.width + parentObject.height) / 2;
                        final double particleSize = 50;

                        final Vec2d particlePosition = this.parentObject.position.copyAndForward(angle, enemySize / 2 + 10);

                        particlePosition.add(enemySize / 2, enemySize / 2);
                        particlePosition.sub(particleSize / 2, particleSize / 2);

                        final GameObject particleObject = new GameObject(this.parentApplication.textureManager.get("particle"), particlePosition.x, particlePosition.y, 50, 50, -1);
                        final SingleAngleRotationComponent rotationComponent = new SingleAngleRotationComponent(particleObject);
                        rotationComponent.rotate(angle);
                        particleObject.components.add(rotationComponent);

                        this.parentScene.updateParticles.add(particleObject);
                    }

                    attackTimer.reset();
                    attacking = false;
                }
            }
        }
    }

    @Override
    public GameObject getGameObject() {
        return parentObject;
    }

    private enum Attack {
        ENLARGE, SHOOT_SPIKES;

        public static final double ENLARGE_GOAL_SIZE = 500;

    }
}
