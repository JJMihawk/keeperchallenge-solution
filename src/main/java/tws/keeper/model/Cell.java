package tws.keeper.model;

/**
 * Represent the contents of a Cell
 */
public enum Cell {

    WALL("wall"), PATH("path"), KEY("key"), DOOR("door");

    private String stringValue;

    Cell(String theValue) {
        stringValue = theValue;
    }

    public String toString() {
        return stringValue;
    }

}