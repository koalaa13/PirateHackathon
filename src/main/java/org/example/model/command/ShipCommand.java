package org.example.model.command;

import org.example.model.Shoot;

public class ShipCommand {
    private long id = 0;

    private long changeSpeed = 0;

    private int rotate = 0;

    private Shoot cannonShoot = null;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getChangeSpeed() {
        return changeSpeed;
    }

    public void setChangeSpeed(long changeSpeed) {
        this.changeSpeed = changeSpeed;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public Shoot getCannonShoot() {
        return cannonShoot;
    }

    public void setCannonShoot(Shoot cannonShoot) {
        this.cannonShoot = cannonShoot;
    }
}
