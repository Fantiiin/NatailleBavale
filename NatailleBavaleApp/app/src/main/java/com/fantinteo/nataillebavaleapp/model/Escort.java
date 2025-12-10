package com.fantinteo.nataillebavaleapp.model;

import java.util.List;

public class Escort extends Ship {
    public static final int ESCORT_SIZE = 2;
    public Escort(List<ShipElement> elements) {
        super(ESCORT_SIZE);
        this.elements = elements;
    }
    @Override
    public String getName() {
        return "Escorteur";
    }
}