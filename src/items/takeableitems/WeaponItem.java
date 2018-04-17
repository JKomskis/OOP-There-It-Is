package items.takeableitems;

import commands.Command;
import entity.entitymodel.Entity;
import entity.entitymodel.Equipment;
import savingloading.Visitor;
import skills.SkillType;
import utilities.Coordinate;

public class WeaponItem extends TakeableItem {

    private Command command;
    private int damage;
    private int attackSpeed;
    private SkillType requiredSkill;

    public WeaponItem(String name, Command command, int damage, int attackSpeed, SkillType requiredSkill) {
        super(name);
        this.command = command;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.requiredSkill = requiredSkill;
    }

    @Override
    public void activate(Equipment e) {

    }

    public void attack(Entity attacker, Coordinate location) {

    }

    public Command getCommand() {
        return command;
    }

    public int getDamage(){
        return damage;
    }

    public int getAttackSpeed(){
        return attackSpeed;
    }

    public SkillType getRequiredSkill() {
        return requiredSkill;
    }

    @Override
    public void accept(Visitor v) {
        v.visitWeaponItem(this);
    }
}
