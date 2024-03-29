package org.example.model;

import org.example.model.command.ShipCommand;

import java.util.*;

public class Ship {
    private long id;
    private long x;
    private long y;
    private int size;
    private int  hp;
    private int maxHp;
    private Direction direction;
    private long speed;
    private long maxSpeed;
    private long minSpeed;
    private long maxChangeSpeed;
    private long cannonCooldown;
    private long cannonCooldownLeft;
    private long cannonRadius;
    private long scanRadius;
    private long cannonShootSuccessCount;

    public static enum Direction {
        south(0, 1),
        north(0, -1),
        west(-1, 0),
        east(1, 0);

        Direction(int xDirection, int yDirection) {
            this.xDirection = xDirection;
            this.yDirection = yDirection;
        }

        public final int xDirection;
        public final int yDirection;

        public Direction findDirection(long newX, long newY) {
            if (newX == south.xDirection && newY == south.yDirection) {
                return south;
            } else if (newX == north.xDirection && newY == north.yDirection) {
                return north;
            } else if (newX == west.xDirection && newY == west.yDirection) {
                return west;
            } else {
                return east;
            }
        }

        public Direction applyRotate(Integer rotate) {
            if (rotate == null) return this;
            if (rotate == 90) {
                return findDirection(-yDirection, xDirection);
            } else {
                return findDirection(yDirection, -xDirection);
            }
        }
    }

    public Set<Tile> toTiles() {
        Set<Tile> tiles = new HashSet<>();
        long curX = this.x;
        long curY = this.y;
        for (int i = 0; i < size; ++i) {
            tiles.add(new Tile(curX, curY));
            curX += this.direction.xDirection;
            curY += this.direction.yDirection;
        }
        return tiles;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getX() {
        return x;
    }

    public void setX(long x) {
        this.x = x;
    }

    public long getY() {
        return y;
    }

    public void setY(long y) {
        this.y = y;
    }

    public long getHeadX() {
        return x + (long) getDirection().xDirection * (getSize() - 1);
    }

    public long getHeadY() {
        return y + (long) getDirection().yDirection * (getSize() - 1);
    }

    public long getForwardX() {
        return x + getDirection().xDirection * getSpeed();
    }

    public long getForwardY() {
        return y + getDirection().yDirection * getSpeed();
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public long getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(long maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public long getMinSpeed() {
        return minSpeed;
    }

    public void setMinSpeed(long minSpeed) {
        this.minSpeed = minSpeed;
    }

    public long getMaxChangeSpeed() {
        return maxChangeSpeed;
    }

    public void setMaxChangeSpeed(long maxChangeSpeed) {
        this.maxChangeSpeed = maxChangeSpeed;
    }

    public long getCannonCooldown() {
        return cannonCooldown;
    }

    public void setCannonCooldown(long cannonCooldown) {
        this.cannonCooldown = cannonCooldown;
    }

    public long getCannonCooldownLeft() {
        return cannonCooldownLeft;
    }

    public void setCannonCooldownLeft(long cannonCooldownLeft) {
        this.cannonCooldownLeft = cannonCooldownLeft;
    }

    public long getCannonRadius() {
        return cannonRadius;
    }

    public void setCannonRadius(long cannonRadius) {
        this.cannonRadius = cannonRadius;
    }

    public long getScanRadius() {
        return scanRadius;
    }

    public void setScanRadius(long scanRadius) {
        this.scanRadius = scanRadius;
    }

    public long getCannonShootSuccessCount() {
        return cannonShootSuccessCount;
    }

    public void setCannonShootSuccessCount(long cannonShootSuccessCount) {
        this.cannonShootSuccessCount = cannonShootSuccessCount;
    }

    public static class Cell {
        private final long x;

        private final long y;

        public Cell(long x, long y) {
            this.x = x;
            this.y = y;
        }

        public long getX() {
            return x;
        }

        public long getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return x == cell.x && y == cell.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public List<Cell> getNextCells(ShipCommand shipCommand) {
        long newSpeed = getSpeed() + shipCommand.getChangeSpeed();
        long newX = x + getDirection().xDirection * newSpeed;
        long newY = y + getDirection().yDirection * newSpeed;
        Direction newDir = getDirection().applyRotate(shipCommand.getRotate());
        List<Cell> cells = new ArrayList<>();
        for (int i = 0; i < getSize(); i++) {
            cells.add(new Cell(newX + newDir.xDirection * i, newY + newDir.yDirection * i));
        }
        return cells;
    }
}
