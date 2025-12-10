package com.fantinteo.nataillebavaleapp.model;

import java.io.Serializable;
import java.util.List;

public abstract class Ship implements Serializable {
    private final int size;
    protected List<ShipElement> elements;
    private boolean isSunk = false;

    public Ship(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public abstract String getName();

    public List<ShipElement> getElements() {
        return elements;
    }

    public boolean isSunk() {
        if (!isSunk) {
            for (ShipElement element : elements) {
                if (!element.isTouched()) {
                    return false;
                }
            }
            this.isSunk = true;
        }
        return isSunk;
    }

    public static class ShipElement implements Serializable {
        private int x;
        private int y;
        private boolean isTouched;

        public ShipElement(int x, int y) {
            this.x = x;
            this.y = y;
            this.isTouched = false;
        }

        public boolean isTouched() {
            return isTouched;
        }

        public void setTouched(boolean touched) {
            isTouched = touched;
        }

        public int getX() { return x; }
        public int getY() { return y; }
    }
}