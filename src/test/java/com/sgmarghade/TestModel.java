package com.sgmarghade;

/**
 * Created by swapnil on 25/05/15.
 */
public class TestModel {
    private String name = "swapnil";
    private String surname = "marghade";

    public TestModel(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
