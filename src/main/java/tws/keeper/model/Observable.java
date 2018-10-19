package tws.keeper.model;

public interface Observable {

    Cell lookUp();

    Cell lookDown();

    Cell lookLeft();

    Cell lookRight();

    int getKeysFound();

    int getTotalNumberOfKeys();

    boolean isMazeCompleted();

    Position getKeeperPosition();

}

