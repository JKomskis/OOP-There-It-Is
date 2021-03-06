package maps.world;

import entity.entitymodel.Entity;
import gameobject.GameObject;
import gameobject.GameObjectContainer;
import maps.Influence.InfluenceArea;
import maps.movelegalitychecker.MoveLegalityChecker;
import maps.tile.Direction;
import maps.tile.OverWorldTile;
import maps.tile.Tile;
import savingloading.Visitor;
import spawning.SpawnObservable;
import utilities.Coordinate;

import java.util.*;

/**
 * Created by dontf on 4/14/2018.
 */
public class OverWorld implements World {

    private Map<Coordinate, OverWorldTile> tiles;
    private Map<Coordinate, GameObjectContainer> gettableMap;

    public OverWorld(Map <Coordinate, OverWorldTile> tiles) {
        this.tiles = tiles;
        // Cloning the whole map for every getMap() call is costly, so let's just cache it once here
        // (Since we can only add Tiles at construction time anyway
        gettableMap = new HashMap<>(tiles);
        buildNeighborList();
    }

    private void buildNeighborList() {
        for(Map.Entry<Coordinate, OverWorldTile> entry: tiles.entrySet()) {
            Coordinate coordinate = entry.getKey();
            for(Direction direction: Direction.values()) {
                OverWorldTile neighbor = tiles.get(coordinate.getNeighbor(direction));
                entry.getValue().setNeighbor(direction, neighbor);
            }
        }
    }

    @Override
    public void update() {
        updatePhase();
        movementPhase();
        interactionPhase();
    }

    private void updatePhase() {
        for(OverWorldTile tile: tiles.values()) {
            tile.do_update(getTileMap());
        }
    }

    private void movementPhase() {
        Set<MoveLegalityChecker> updated = new HashSet<>();
        for(OverWorldTile tile: tiles.values()) {
            tile.do_moves(updated);
        }
    }

    private void interactionPhase() {
        for(OverWorldTile tile: tiles.values()) {
            tile.do_interactions();
        }
    }

    public OverWorldTile getTile(Coordinate c) { return tiles.get(c); }

    public Map<Coordinate, GameObjectContainer> getMap()
    {
        return gettableMap;
    }

    public void add(Coordinate p, OverWorldTile t)
    {
        tiles.put(p, t);
        gettableMap.put(p, t);
    }

    @Override
    public void add(Coordinate p, Entity e) {
        tiles.get(p).setEntity(e);
    }

    @Override
    public Tile getTileForEntity(Entity e) {
        for(OverWorldTile tile: tiles.values()) {
            if(tile.has(e))
                return tile;
        }
        return null;
    }

    @Override
    public void remove(Entity e) {
        for(OverWorldTile tile: tiles.values()) {
            if(tile.remove(e))
                return;
        }
    }

    @Override
    public Coordinate getEntityCoordinate(Entity e) {
        Entity trueEntity = e.getEntity ();
        for(Map.Entry<Coordinate, OverWorldTile> entry: tiles.entrySet()) {
            if(entry.getValue().has(trueEntity)) {
                return new Coordinate(entry.getKey());
            }
        }
        return null;
    }

    //throw away spawn events
    @Override
    public void notifySpawn(InfluenceArea IA, SpawnObservable spawner) {}

    @Override
    public void clearInfluenceAreas() {}

    @Override
    public Tile getTileForCoordinate(Coordinate c) {
        return tiles.get(c);
    }

    public Map<Coordinate, OverWorldTile> getTiles(){
        return tiles;
    }

    @Override
    public Map <Coordinate, Tile> getTileMap () {
        HashMap<Coordinate, Tile> temp = new HashMap<>();
        for (Coordinate c : tiles.keySet()) {
            temp.put (c, tiles.get(c));
        }
        return temp;
    }

    @Override
    public Map<Coordinate, GameObject> getInfluences() {
        Map<Coordinate, GameObject> iaMap = new HashMap<Coordinate,GameObject>();
        return iaMap;
    }


    @Override
    public void accept(Visitor v) {
        v.visitOverWorld(this);
    }
}
