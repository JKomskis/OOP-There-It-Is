package Vehicles;

import entitymodel.Entity;
import entitymodel.EntityInteraction;
import entitymodel.EntityStats;
import entitymodel.Inventory;
import entitycontrol.EntityController;
import Tiles.Direction;
import actions.Action;
import commands.TimedEffect;

import java.util.List;

/**
 * Created by dontf on 4/13/2018.
 */
public class Vehicle extends Entity {

    private Entity driver;

    public Vehicle(Direction direction,
                   EntityStats stats,
                   List<Action> actions,
                   List<TimedEffect> effects,
                   List<EntityInteraction> actorInteractions,
                   List<EntityInteraction> acteeInteractions,
                   EntityController controller,
                   Inventory inventory,
                   Entity driver)
    {
        super(direction, stats, actions, effects, actorInteractions, acteeInteractions, controller, inventory);
        this.driver = driver;
    }

    public void setDriver (Entity driver) {
        this.driver = driver;
    }
}
