package maps.world;

import entity.entitycontrol.EntityController;
import entity.entitymodel.Entity;
import savingloading.Visitable;
import savingloading.Visitor;
import utilities.Coordinate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dontf on 4/14/2018.
 */
public class Game implements TransitionObserver, Visitable {

    private World activeWorld;
    private OverWorld overWorld;
    private List<FoggyWorld> localWorlds;
    private static long curTime;
    private static long lastTimeUpdated;
    private Entity player;

    private TransitionObserver transitionObserver;

    public Game(World activeWorld,
                OverWorld overWorld,
                List<FoggyWorld> localWorlds,
                int curTime,
                Entity player)
    {
        this.activeWorld = activeWorld;
        this.overWorld = overWorld;
        this.localWorlds = localWorlds;
        Game.curTime = curTime;
        Game.lastTimeUpdated = System.currentTimeMillis();
        this.player = player;
    }

    public void setTransitionObserver(TransitionObserver transitionObserver)
    {
        this.transitionObserver = transitionObserver;
    }

    public static long getCurrentTime () {
        return curTime;
    }

    public void notifyTransition (Entity e, World target, Coordinate p) {
        if(isPlayer(e)) {
            updateWeaponItems(e, target);
            activeWorld.remove(e);
            setActiveWorld(target);
            activeWorld.add(p, e);
            activeWorld.clearInfluenceAreas();
            if(transitionObserver != null)
                transitionObserver.notifyTransition(e, target, p);
        }
    }

    private boolean isPlayer(Entity other) {
        return player == other;
    }

    private void setActiveWorld(World target) {
        activeWorld = target;
    }

    private void updateWeaponItems(Entity e, World target) {
        e.updateSpawnObservers(activeWorld, target);
    }

    public static void updateGameTime() {
        long currTimeMills = System.currentTimeMillis();
        curTime += (currTimeMills-lastTimeUpdated);
        //System.out.println("Current Time: " + getCurrentTime() + " += " + (currTimeMills - lastTimeUpdated));
        lastTimeUpdated = currTimeMills;
    }

    public void update()
    {
        updateGameTime();
        activeWorld.update();
    }

    public Entity getPlayer() { return player; }

    public void setPlayerController(EntityController controller)
    {
        player.setController(controller);
    }

    public Coordinate getCoordinate(Entity entity)
    {
        return activeWorld.getEntityCoordinate(entity);
    }

    public OverWorld getOverWorld(){
        return overWorld;
    }

    public List<FoggyWorld> getLocalWorlds(){
        return localWorlds;
    }

    public List<World> getWorlds(){
        List<World> worlds = new ArrayList<World>();
        worlds.add(overWorld);
        worlds.addAll(localWorlds);
        return worlds;
    }

    public List<World> getRealWorlds(){
        List<World> worlds = new ArrayList<World>();
        worlds.add(overWorld);
        for (FoggyWorld foggyWorld : localWorlds){
            worlds.add(foggyWorld.getLocalWorld());
        }
        return worlds;
    }

    @Override
    public void accept(Visitor v) {
        v.visitGame(this);
    }
}
