package creatures;
import huglife.Creature;
import huglife.Direction;
import huglife.Action;
import huglife.Occupant;
import huglife.HugLifeUtils;
import java.awt.Color;
import java.util.Map;
import java.util.List;

public class Clorus extends Creature {
    private int r;
    private int g;
    private int b;
    private static final double energyLostMove = 0.03;
    private static final double energyLostStay = 0.01;
    
    public Clorus(double e) {
        super("clorus");
        r = 34;
        g = 0;
        b = 231;
        energy = e;
    }
    
    public Color color() {
        return color(r, g, b);
    }
    
    public void attack(Creature c) {
        energy += c.energy();
    }
    
    public void move() {
        energy -= energyLostMove;
    }
    
    public void stay() {
        energy -= energyLostStay;
    }
    
    public Clorus replicate() {
        energy *= 0.5;
        return new Clorus(energy);
    }
    
    public Action chooseAction(Map<Direction, Occupant> neighbors) {
        List<Direction> empties = getNeighborsOfType(neighbors, "empty");
        if (empties.isEmpty()) {
            return new Action(Action.ActionType.STAY);
        }
        List<Direction> plips = getNeighborsOfType(neighbors, "plip");
        if (!plips.isEmpty()) {
            return new Action(Action.ActionType.ATTACK, HugLifeUtils.randomEntry(plips));
        } else if (energy >= 1) {
            return new Action(Action.ActionType.REPLICATE, HugLifeUtils.randomEntry(empties));
        }
        return new Action(Action.ActionType.MOVE, HugLifeUtils.randomEntry(empties));
    }
}
