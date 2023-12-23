package org.example.model.command;

import org.example.model.Shoot;

public class ShipCommand {
    private long id;

    private long changeSpeed;

    private int rotate;

    private Shoot cannonShoot;

    public ShipCommand() {
        this.id = 0;
        this.changeSpeed = 0;
        this.rotate = 0;
        this.cannonShoot = null;
    }

    public ShipCommand(long id) {
        this();
        this.id = id;
    }

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
