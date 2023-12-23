package org.example.model;

public class ShipCommand {
    private long id;

    private long changeSpeed;

    private int rotate;

    private Shoot cannonShoot;

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
