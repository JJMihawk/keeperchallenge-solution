package tws.keeper.model;

import tws.keeper.model.*;
import tws.keeper.model.Observable;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Random theMaze generator
 */
public class Maze implements Observable {

    private Cell[][] theMaze; // The theMaze
    private Keeper theKeeper; // The keeper
    private Position keeperPosition;  // The keeperPosition
    private Position doorPosition; // The door
    private List<Position> keysPositions; // The keys
    private List<Position> keysFoundPositions; // The keys
    private int numberOfKeys; // Total number of keys to find
    private int keysFound = 0; // Keys found
    private boolean mazeCompleted = false; // Is the lock open

    /**
     * Default tws.keeper.maze 40x40 7 keys
     */
    public Maze(Keeper keeper) {
        this(keeper, 40, 40, 7);
    }

    /**
     * Generate a new tws.keeper.maze
     */
    private Maze(Keeper keeper, int height, int width, int keys) {
        theKeeper = keeper;
        numberOfKeys = keys;
        theMaze = new Cell[height][width]; // empty tws.keeper.maze
        for (Cell[] row : theMaze) Arrays.parallelSetAll(row, (index) -> Cell.WALL); // all cells are walls for starters
        createRandomPath(); // generate a random tws.keeper.maze
        doorPosition = switchRandomCell(Cell.PATH, Cell.DOOR); // place the door
        keysPositions = new ArrayList<>();
        keysFoundPositions = new ArrayList<>();
        for (int i = 0; i < numberOfKeys; i++) keysPositions.add(switchRandomCell(Cell.PATH, Cell.KEY)); // the keys
        for (int i = 0; i < (width * height) / 100; i++)
            switchRandomCell(Cell.WALL, Cell.PATH); // place a few extra empty cells
        keeperPosition = randomInternalPosition(Cell.PATH); // place the keeper in an empty cell
    }

    /**
     * The width of the theMaze
     */
    private int width() {
        return theMaze[0].length;
    }

    /**
     * The height of the theMaze
     */
    private int height() {
        return theMaze.length;
    }

    /**
     * The keeper position
     */
    public Position getKeeperPosition() {
        return keeperPosition;
    }

    /**
     * The door position
     */
    public Position getDoorPosition() {
        return doorPosition;
    }

    /**
     * The keys position
     */
    public List<Position> getKeysPositions() {
        return keysPositions;
    }

    /**
     * The keys found position
     */
    public List<Position> getKeysFoundPositions() {
        return keysFoundPositions;
    }

    /**
     * Contents of the cell up
     */
    @Override
    public Cell lookUp() {
        return look(cellUp);
    }

    /**
     * Contents of the cell down
     */
    @Override
    public Cell lookDown() {
        return look(cellDown);
    }

    /**
     * Contents of the cell left
     */
    @Override
    public Cell lookLeft() {
        return look(cellLeft);
    }

    /**
     * Contents of the cell right
     */
    @Override
    public Cell lookRight() {
        return look(cellRight);
    }

    /**
     * How many keys in total
     */
    @Override
    public int getTotalNumberOfKeys() {
        return numberOfKeys;
    }

    /**
     * How many keys have been found
     */
    @Override
    public int getKeysFound() {
        return keysFound;
    }

    /**
     * Have we found all the keys and the door?
     */
    @Override
    public boolean isMazeCompleted() {
        return mazeCompleted;
    }

    /**
     * Request an action from the keeper
     */
    public void makeKeeperAct() {
        doAction(theKeeper.act(this));
    }

    /**
     * Perform the action if possible
     */
    private void doAction(Action action) {
        if (!mazeCompleted) {
            switch (action) {
                case GO_UP:
                    walk(cellUp);
                    break;
                case GO_DOWN:
                    walk(cellDown);
                    break;
                case GO_LEFT:
                    walk(cellLeft);
                    break;
                case GO_RIGHT:
                    walk(cellRight);
                    break;
            }
        }
    }

    /**
     * Generate a random path in the theMaze starting in this position
     */
    private void createRandomPath() {
        Stack<Position> pendingPaths = new Stack<>();
        pendingPaths.push(randomInternalPosition(Cell.WALL));
        do {
            Position pos = pendingPaths.pop();
            if (canContinue(pos)) {
                emptyCell(pos);
                pendingPaths.addAll(getAdjacentWalls(pos));
                Collections.shuffle(pendingPaths);
            }
        }
        while (!pendingPaths.isEmpty());
    }

    /**
     * Get possible exits
     */
    private boolean canContinue(Position position) {
        return isWithinLimits(position) && isWall(position) && getAdjacentWalls(position).size() > 2;
    }

    /**
     * Returns value of cell at this position
     */
    private Cell cellAt(Position position) {
        return theMaze[position.getVertical()][position.getHorizontal()];
    }

    /**
     * Empties this cell
     */
    private void emptyCell(Position position) {
        setCell(position, Cell.PATH);
    }

    /**
     * Returns value of cell at this position
     */
    private void setCell(Position position, Cell cell) {
        theMaze[position.getVertical()][position.getHorizontal()] = cell;
    }

    /**
     * Is this a wall
     */
    private boolean isWall(Position position) {
        return cellAt(position).equals(Cell.WALL);
    }

    /**
     * Is this a valid position
     */
    private boolean isWithinLimits(Position position) {
        return (position.getVertical() > 0) && (position.getVertical() < height() - 1) && (position.getHorizontal() > 0) && (position.getHorizontal() < width() - 1);
    }

    /**
     * Return an random internal cell with specified value
     */
    private Position randomInternalPosition(Cell cellvalue) {
        Position randomPosition;
        do randomPosition = new Position(1 + new Random().nextInt(height() - 2), 1 + new Random().nextInt(width() - 2));
        while (!cellAt(randomPosition).equals(cellvalue));
        return randomPosition;
    }

    /**
     * Randomly place objects in the theMaze
     */
    private Position switchRandomCell(Cell from, Cell to) {
        Position randomPosition = randomInternalPosition(from);
        setCell(randomPosition, to);
        return randomPosition;
    }

    /**
     * Adjacent cell functions
     */
    private static UnaryOperator<Position> cellUp = position -> new Position(position.getVertical() - 1, position.getHorizontal());
    private static UnaryOperator<Position> cellDown = position -> new Position(position.getVertical() + 1, position.getHorizontal());
    private static UnaryOperator<Position> cellLeft = position -> new Position(position.getVertical(), position.getHorizontal() - 1);
    private static UnaryOperator<Position> cellRight = position -> new Position(position.getVertical(), position.getHorizontal() + 1);

    /**
     * Adjacent walls
     */
    private List<Position> getAdjacentWalls(Position position) {
        return Stream.of(cellRight, cellDown, cellLeft, cellUp).map((cell) -> cell.apply(position)).filter(this::isWall).collect(Collectors.toList());
    }

    /**
     * Look in a direction
     */
    private Cell look(UnaryOperator<Position> where) {
        return cellAt(where.apply(getKeeperPosition()));
    }

    /**
     * Move the keeperPosition
     */
    private void walk(UnaryOperator<Position> where) {
        if (!look(where).equals(Cell.WALL)) {
            keeperPosition = where.apply(keeperPosition);
            if (cellAt(keeperPosition).equals(Cell.KEY)) {
                keysFoundPositions.add(keeperPosition);
                setCell(keeperPosition, Cell.PATH);
                keysFound++;
            }
            if (cellAt(keeperPosition).equals(Cell.DOOR) && (keysFound == getTotalNumberOfKeys())) mazeCompleted = true;
        }
    }

    /**
     * Return a json representation of this tws.keeper.maze
     */
    public String toJson() {
        return "{\n" +
                "\t\"height\":" + height() + ",\n" +
                "\t\"width\":" + width() + ",\n" +
                "\t\"cells\": [\n" + rowsToJson(theMaze) + "\n\t]\n" +
                "}" + "\n";
    }

    private String rowsToJson(Cell[][] rows) {
        return Arrays.stream(rows).map(row -> rowToJson(row)).collect(Collectors.joining(",\n"));
    }

    private String rowToJson(Cell[] row) {
        return "\t\t[" + Arrays.stream(row).map(cell -> "\"" + cell.toString() + "\"").collect(Collectors.joining(",")) + "]";
    }

}