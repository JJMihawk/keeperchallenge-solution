package tws.keeper.solution;

import tws.keeper.model.*;
import tws.keeper.model.Observable;

import java.util.*;

import static tws.keeper.model.Action.*;

public class KeeperAI implements Keeper {

    private static final List<Action> availableActions = Arrays.asList(GO_UP, GO_RIGHT, GO_DOWN, GO_LEFT);

    private Observable maze;
    private Cell[] sight = new Cell[4]; // cells around the keeper
    private Set<Position> positions = new HashSet<>(); // visited positions
    private List<Position> travel = new ArrayList<>(); // the keeper's travel

    /**
     * Unexplored cells in order of vision and their positions in the travel
     */
    private List<Position> unexplored = new ArrayList<>();
    private List<Integer> posInTravel = new ArrayList<>();

    private boolean doorFound = false; // Has the door been found
    private List<Position> wayToDoor = new ArrayList<>(); // way to the door

    private Cell[][] map = new Cell[40][40]; // the keeper's map
    private boolean initialized = false; // Has the map been initialized


    /**
     * This Keeper Artificial Inteligence simply acts randomly
     *
     * @param m
     * @return
     */
    public Action act(Observable m) {
        maze=m;

        if (!initialized) ini();

        Position keeperPosition = maze.getKeeperPosition();

        travel.add(keeperPosition);
        positions.add(keeperPosition);
        removeUnexplored(keeperPosition);


        if (maze.getKeysFound() == maze.getTotalNumberOfKeys() && doorFound) {
            return goDoor(keeperPosition);
        }

        if (doorFound) {
            Boolean found = false;
            int i = 0;
            while (i < wayToDoor.size()-1 && !found){
                if (adjacent(keeperPosition,wayToDoor.get(i))){ // optimize the way to the door
                    found = true;
                    for (int j = wayToDoor.size()-1; j>i; j--){
                        wayToDoor.remove(j);
                    }
                }
                i++;
            }
            wayToDoor.add(keeperPosition);
        }


        look();
        writeMap();

        int t = -1;
        for (int i = 0; i<4 ; i++){
            if (sight[i] != Cell.WALL && !positions.contains(adjPos(keeperPosition,i))) { // add to unexplored the paths that we have not visited yet
                int index = unexplored.indexOf(adjPos(keeperPosition,i));
                if (index != -1){
                    unexplored.remove(index);
                    posInTravel.remove(index);
                }
                unexplored.add(adjPos(keeperPosition,i));
                posInTravel.add(travel.size()-1);
            }
            if (!doorFound && sight[i] == Cell.DOOR){ // if we see the door we start to remember the way
                doorFound = true;
                wayToDoor.add(adjPos(keeperPosition,i));
                wayToDoor.add(keeperPosition);
            }
            else if (sight[i] == Cell.KEY){
                t = i;
            }
        }
        if (t != -1) return availableActions.get(t); // go to the key


        while(noExit(unexplored.get(unexplored.size()-1))){ // if the cell has no exit we remove it
            positions.add(unexplored.get(unexplored.size()-1));
            unexplored.remove(unexplored.size()-1);
            posInTravel.remove(posInTravel.size()-1);
        }

        return goToUnexplored(keeperPosition);
    }

    /**
     * Step to go to the last unexplored path
     */
    private Action goToUnexplored(Position keeperPosition) {
        Action action = null;
        if(adjacent(keeperPosition,unexplored.get(unexplored.size()-1))){
            action = step(keeperPosition,unexplored.get(unexplored.size()-1));
            unexplored.remove(unexplored.size()-1);
            posInTravel.remove(posInTravel.size()-1);
        }
        else{
            int i = posInTravel.get(posInTravel.size()-1);
            Boolean found = false;
            while (i < travel.size()-1 && !found){
                if (adjacent(keeperPosition,travel.get(i))){
                    action = step(keeperPosition,travel.get(i));
                    found = true;
                }
                i++;
            }
        }
        return action;
    }

    /**
     * Remove a position from unexplored paths
     */
    private void removeUnexplored(Position position) {
        int a = unexplored.indexOf(position);
        if(a != -1){
            unexplored.remove(a);
            posInTravel.remove(a);
        }
    }

    /**
     * Initialize the map
     */
    private void ini() {
        for (int i=0; i<40; i++){
            map[0][i] = Cell.WALL;
            map[39][i] = Cell.WALL;
            map[i][0] = Cell.WALL;
            map[i][39] = Cell.WALL;
        }
        initialized = true;
    }

    /**
     * Check if a path has exit
     */
    private boolean noExit(Position pos) {
        int vert = pos.getVertical();
        int hor = pos.getHorizontal();
        return  (((map[vert-1][hor] == Cell.WALL || positions.contains(new Position(vert-1,hor)))? 1:0) +
                ((map[vert+1][hor] == Cell.WALL || positions.contains(new Position(vert+1,hor)))? 1:0) +
                ((map[vert][hor+1] == Cell.WALL || positions.contains(new Position(vert,hor+1)))? 1:0) +
                ((map[vert][hor-1] == Cell.WALL || positions.contains(new Position(vert,hor-1)))? 1:0)) == 4;
    }

    /**
     * Position of a cell adjacent to the given in the direction i
     */
    private Position adjPos(Position pos, int i) {
        int vert = pos.getVertical();
        int hor = pos.getHorizontal();
        switch (i){
            case 0: vert--;
                    break;
            case 1: hor++;
                    break;
            case 2: vert++;
                    break;
            case 3: hor--;
                    break;
        }
        return new Position(vert,hor);
    }

    /**
     * Look around
     */
    private void look() {
        sight[0] = maze.lookUp();
        sight[1] = maze.lookRight();
        sight[2] = maze.lookDown();
        sight[3] = maze.lookLeft();
    }

    /**
     * Complete the map with the new information
     */
    private void writeMap(){
        int vert = maze.getKeeperPosition().getVertical();
        int hor = maze.getKeeperPosition().getHorizontal();

        map[vert-1][hor] = sight[0];
        map[vert][hor+1] = sight[1];
        map[vert+1][hor] = sight[2];
        map[vert][hor-1] = sight[3];
    }

    /**
     * Step to go to the door
     */
    private Action goDoor(Position posIni) {
        Position posFin = wayToDoor.get(wayToDoor.size()-1);
        wayToDoor.remove(wayToDoor.size()-1);
        return step(posIni,posFin);
    }

    /**
     * Step from a position to another
     */
    private Action step(Position posIni, Position posFin) {
        int subtVert = posFin.getVertical() - posIni.getVertical();
        int subtHor = posFin.getHorizontal() - posIni.getHorizontal();
        if (subtVert == -1) return GO_UP;
        else if(subtVert == 1) return GO_DOWN;
        else if (subtHor == -1) return GO_LEFT;
        return GO_RIGHT;
    }

    /**
     * Check if two positions are adjacent
     */
    private Boolean adjacent(Position pos1, Position pos2){
        return (Math.abs(pos1.getHorizontal()-pos2.getHorizontal()) == 1 && pos1.getVertical()-pos2.getVertical() == 0 ) ||
                (Math.abs(pos1.getVertical()-pos2.getVertical()) == 1 && pos1.getHorizontal()-pos2.getHorizontal() == 0 );
    }






}