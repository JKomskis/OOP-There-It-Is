package entity.entitycontrol;


import entity.entitycontrol.controllerActions.ControllerAction;
import entity.entitymodel.Entity;
import entity.entitymodel.Equipment;
import entity.entitymodel.interactions.EntityInteraction;
import entity.vehicle.Vehicle;
import gameobject.GameObject;
import gameview.displayable.widget.DialogObservable;
import gameview.displayable.widget.DialogObserver;
import items.takeableitems.TakeableItem;
import maps.tile.Tile;
import savingloading.Visitable;
import spawning.SpawnObserver;
import utilities.Coordinate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class EntityController implements Visitable, DialogObservable{

    private Entity controlledEntity;
    private Equipment equipment;
    private Coordinate entityLocation;
    private Collection<ControllerAction> actions;
    //make sure if we load in and we are on a vehicle that this is set correctly
    private boolean inVehicle;
    private boolean dismounting;
    private Vehicle mount;

    public EntityController(Entity entity, Equipment equipment,
                            Coordinate entityLocation, Collection<ControllerAction> actions) {
        this.controlledEntity = entity;
        this.equipment = equipment;
        this.entityLocation = entityLocation;
        this.actions = actions;
        this.inVehicle = !entity.isOnMap();
        dismounting = false;
    }


    //this is the specific functionality that each entity controller is responsible for implementing
    protected abstract void processController();
    public abstract void interact(EntityController interacter);
    public abstract void notifyFreeMove(Entity e);
    public abstract void notifyInventoryManagment(Entity e);
    public abstract void notifyInteraction(Entity player, Entity interactee);
    public abstract List<EntityInteraction> getInteractionList ();
    public abstract void notifyShopping(Entity trader);
    public abstract void notifyLevelUp(Entity e);
    public abstract void notifyMainMenu(Entity e);
    public abstract void notifyUseItem(Entity player, EntityController interactee);

    // used to update ai;
    public abstract void updateMap (Map <Coordinate, Tile> map);

    public abstract void enrage(Entity e);
    public abstract void pacify();
    public abstract boolean isAggroed ();

    protected Entity getControlledEntity() { return controlledEntity; }

    public void setControllerActions(Collection<ControllerAction> actions){
        this.actions = actions;
    }

    //this is the functionality all entity controllers need

    public final void update(Map<Coordinate, Tile> mapOfContainers){
        boolean found = false;

        //find the entity in the map and set his location
        Collection<GameObject> gameObjectList;
        //iterate through all the entries in the map of GameObjectContainers
        for(Map.Entry<Coordinate, Tile> container : mapOfContainers.entrySet()){
            gameObjectList = container.getValue().getGameObjects();
            //iterate through all the gameObjects in each gameObjectContainer
            for(GameObject gameObject : gameObjectList){
                if((!inVehicle && gameObject == controlledEntity) || (inVehicle && gameObject == mount)){
                    entityLocation = container.getKey();
                    found = true;
                }
            }
        }
        if(!found){
            throw new java.lang.RuntimeException("EntityController::update() : The controlled entity is not in this list of GameObjectContainers");
        }

        //iterate through the controllerActions and tell them to update so they can do their action if they need to
        for(ControllerAction action : actions){
            action.update();
        }

        // checking if entity is trying to dismount
        if(dismounting) {
            System.out.println("dismounting");
            if (dismountTo(mapOfContainers.get(entityLocation))) {
                controlledEntity.setOnMap(true);
                inVehicle = false;
                mount.removeDriver();
                mount = null;
            }
            dismounting = false;
            System.out.println(mount == null);
        }

    }

    public void addAction(ControllerAction action) {
        actions.add(action);
    }

    public final void notifyMount(Vehicle mount){
        if(!inVehicle){
            inVehicle = true;
            this.mount = mount;
        }
        else{
            throw new java.lang.RuntimeException("EntityController::notifyMount() : The controlled entity cannot mount because it is already in a vehicle");
        }
    }

    public final void notifyDismount(){
        if(inVehicle){
            dismounting = true;
        }
        else{
            System.out.println("You Cannot Dismount Yourself!");
            //throw new java.lang.RuntimeException("EntityController::notifyDismount() : The controlled entity cannot dismount because it is not currently in a vehicle");
        }
    }

    public boolean isInVehicle(){
        return inVehicle;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void useWeapon (int index) {
        equipment.useWeaponItem(0, entityLocation);
    }

    public void useItem (TakeableItem item) {
        item.activate(equipment);
    }

    public Coordinate getEntityLocation(){
        return entityLocation;
    }

    public boolean has(GameObject o) {
        return equipment.has(o);
    }

    public void updateSpawnObservers(SpawnObserver oldObserver, SpawnObserver newObserver) {
        if(equipment != null)
            equipment.updateSpawnObservers(oldObserver, newObserver);
    }

    private final boolean dismountTo (Tile toTile) {
        return toTile.placeEntityOnNeighbor(controlledEntity);
    }

    @Override
    public final void register (DialogObserver o) {
        observers.add(o);
    }

    @Override
    public final void unregister (DialogObserver o) {
        observers.remove(o);
    }

    @Override
    public final void notifyAllObservers (String message) {
        for (DialogObserver o : observers) {
            o.notfiy(message);
        }
    }

    public Entity getEntity () {
        return inVehicle ? mount : controlledEntity;
    }

}
