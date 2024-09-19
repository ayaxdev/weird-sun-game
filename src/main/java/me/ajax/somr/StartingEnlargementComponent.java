package me.ajax.somr;

import me.ajaxdev.dackel.components.IObjectComponent;
import me.ajaxdev.dackel.object.GameObject;

public class StartingEnlargementComponent implements IObjectComponent {

    public final GameObject parent;
    public final double goal, speed;

    private boolean finished = false;

    public StartingEnlargementComponent(final GameObject parent, final double goal, final double speed) {
        this.parent = parent;
        this.goal = goal;
        this.speed = speed;
    }

    public void update(double windowWidth, double windowHeight, double delta) {
        if (finished)
            return;

        final double currentSize = (parent.width + parent.height) / 2;

        if (currentSize < goal) {
            final double sizeDiff = speed * delta;

            double nextWidth = currentSize + sizeDiff;
            double nextHeight = parent.height + sizeDiff;

            if (nextWidth == currentSize || nextHeight == parent.height) {
                nextWidth = goal;
                nextHeight = goal;
            }

            parent.width = nextWidth;
            parent.height = nextHeight;

            final double positionOffset = sizeDiff / 2;

            parent.position.sub(positionOffset, positionOffset);
        } else {
            finished = true;
        }
    }

    public GameObject getGameObject() {
        return parent;
    }

    public boolean isFinished() {
        return finished;
    }
}
