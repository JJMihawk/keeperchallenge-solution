package tws.keeper.springboot;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tws.keeper.solution.KeeperAI;
import tws.keeper.model.Maze;
import tws.keeper.model.Keeper;
import tws.keeper.model.Position;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MazeRestApi {

    private Maze maze;
    private Keeper keeper;

    {
        reset();
    }

    @RequestMapping(value = "/maze", method = RequestMethod.GET)
    public ResponseEntity<String> maze() {
        return new ResponseEntity(maze.toJson(), HttpStatus.OK);
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public ResponseEntity<String> status() {
        return new ResponseEntity(getStatusJson(), HttpStatus.OK);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    public ResponseEntity reset() {
        keeper = new KeeperAI();
        maze = new Maze(keeper);
        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/act", method = RequestMethod.GET)
    public ResponseEntity<String> act() {
        maze.makeKeeperAct();
        return new ResponseEntity(HttpStatus.OK);
    }

    private String getStatusJson() {
        return "{\n" +
                " \"keeper\":" + maze.getKeeperPosition().toJson() + ",\n" +
                " \"door\":" + maze.getDoorPosition().toJson() + ",\n" +
                " \"keys\":[" + maze.getKeysPositions().stream().map(Position::toJson).collect(Collectors.joining(",")) + "],\n" +
                " \"found\":[" + maze.getKeysFoundPositions().stream().map(Position::toJson).collect(Collectors.joining(",")) + "],\n" +
                " \"complete\":" + maze.isMazeCompleted() +
                "}\n";
    }

}