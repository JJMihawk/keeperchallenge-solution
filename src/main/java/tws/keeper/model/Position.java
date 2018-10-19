package tws.keeper.model;

public class Position {

    private final int vertical, horizontal;

    /**
     * Construct an immutable instance
     */
    public Position(int vert, int horz) {
        vertical = vert;
        horizontal = horz;
    }

    /**
     * Getter
     */
    public int getVertical() {
        return vertical;
    }

    /**
     * Getter
     */
    public int getHorizontal() {
        return horizontal;
    }

    @Override
    public int hashCode() {
        return 100*this.vertical+this.horizontal;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Position && this.vertical ==
                ((Position) other).vertical && this.horizontal == ((Position) other).horizontal;
    }

    @Override
    public String toString() {
        return "Pos(" + vertical + "," + horizontal + ")";
    }

    public String toJson() {
        return "{\"vertical\":" + vertical + ",\"horizontal\":" + horizontal + "}";
    }

}

