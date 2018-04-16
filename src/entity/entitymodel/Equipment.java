package entity.entitymodel;

import items.takeableitems.ConsumableItem;
import items.takeableitems.TakeableItem;
import items.takeableitems.WeaponItem;
import items.takeableitems.WearableItem;
import utilities.Coordinate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dontf on 4/13/2018.
 */
public class Equipment {

    private final int defaultWeaponsSize = 5;

    private Map <EquipSlot, WearableItem> wearables;
    private WeaponItem[] weapons;
    private int maxSize;
    private Inventory inventory;
    private Entity entity;

    public Equipment(int maxSize, Inventory inventory, Entity entity) {
        this.maxSize = maxSize;
        this.inventory = inventory;
        this.entity = entity;
        this.wearables = new HashMap<>();
        this.weapons = new WeaponItem[defaultWeaponsSize];
    }

    public Equipment(Map<EquipSlot,
                     WearableItem> wearables,
                     WeaponItem[] weapons,
                     int maxSize,
                     Inventory inventory,
                     Entity entity)
    {
        this.wearables = wearables;
        this.weapons = weapons;
        this.maxSize = maxSize;
        this.inventory = inventory;
        this.entity = entity;
    }

    public void add (WearableItem wearable) {

        // Must remove from inventory first as to assert that there is room to add, else the item is lost forever!!!
        inventory.remove(wearable);

        WearableItem current = wearables.getOrDefault(wearable.getEquipType(), null);
        if (current != null) {
            this.remove(current);
        }

        wearables.put(wearable.getEquipType(), wearable);

    }

    public void add (WeaponItem weapon) {

        inventory.remove(weapon);

        for (int i = 0; i < weapons.length; ++i) {
            if (weapons[i] == null) {
                weapons [i] = weapon;
                return;
            }
        }

        this.remove(weapons [weapons.length - 1]);
        weapons [weapons.length - 1] = weapon;

    }

    public void consume (ConsumableItem consumable) {
        inventory.remove(consumable);
        consumable.applyEffect(entity);
    }

    public void remove (WearableItem wearable) {
        inventory.add(wearable);
        wearables.remove(wearable.getEquipType());
    }

    public void remove (WeaponItem weapon) {
        inventory.add(weapon);

        for (int i = 0; i < weapons.length; ++i) {
            if (weapons [i] == weapon) {
                weapons [i] = null;
            }
        }

    }

    public void select (int indexOfInventory) {
       TakeableItem takeable = inventory.select(indexOfInventory);
       takeable.activate(this);
    }

    public void useWeaponItem (int index, Coordinate point) {

        if (index < weapons.length && weapons [index] != null) {
            weapons [index].attack(entity, point);
        } else if (index >= weapons.length) {
            System.out.println("Cannot use weapon index of " + index);
            assert false;
        }

    }

}
