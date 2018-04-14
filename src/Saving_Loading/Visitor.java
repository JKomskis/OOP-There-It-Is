package Saving_Loading;

import EnitityModel.Entity;
import Tiles.Tile;
import World.Game;
import World.World;
import item.Item;

/**
 * Created by dontf on 4/13/2018.
 */
public interface Visitor {

    public void visitTile (Tile t);
    public void visitEntity (Entity e);
    public void visitItem (Item i);
    public void visitWorld (World w);
    public void visitGame (Game g);

}
