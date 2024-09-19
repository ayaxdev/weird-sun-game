package me.ajax.somr;

import me.ajaxdev.dackel.components.IObjectComponent;
import me.ajaxdev.dackel.components.SingleAngleRotationComponent;
import me.ajaxdev.dackel.object.GameObject;

public class EnemyFollowPlayerComponent implements IObjectComponent {

    public final GameObject parent;
    public final GameObject followed;

    public EnemyFollowPlayerComponent(final GameObject parent, final GameObject followed) {
        this.parent = parent;
        this.followed = followed;
    }

    @Override
    public void update(double windowWidth, double windowHeight, double delta) {
        for (final IObjectComponent component : parent.components) {
            if (component instanceof SingleAngleRotationComponent rotationComponent) {
                rotationComponent.angle = calculateAngle(parent.position.x, parent.position.y, followed.position.x, followed.position.y);

                return;
            }
        }

        throw new IllegalStateException("Invalid parent object");
    }

    public static double calculateAngle(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;

        return getAngle(dx, dy);
    }

    public static double getAngle(double x, double y) {
        double angleInRadians = Math.atan2(y, x);

        double angleInDegrees = Math.toDegrees(angleInRadians);

        if (angleInDegrees < 0) {
            angleInDegrees += 360;
        }

        return angleInDegrees;
    }

    @Override
    public GameObject getGameObject() {
        return parent;
    }
}
