package en.gregthegeek.gfactions.db;

import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;
import en.gregthegeek.gfactions.player.gPlayer;
import en.gregthegeek.util.Utils;

public class PlayerDataAccess extends DataAccess {
    @Column(columnName = "name", dataType = Column.DataType.STRING)
    private String name;
    @Column(columnName = "power", dataType = Column.DataType.INTEGER)
    private int power;
    @Column(columnName = "bonusPower", dataType = Column.DataType.INTEGER)
    private int bonusPower;
    @Column(columnName = "title", dataType = Column.DataType.STRING)
    private String title;
    
    public PlayerDataAccess() {
        super("factions", "players");
        name = "";
        power = 0;
        bonusPower = 0;
        title = "";
    }
    
    public PlayerDataAccess(gPlayer gp) {
        super("factions", "players");
        name = gp.getName();
        power = gp.getPower();
        bonusPower = gp.bonusPower;
        title = gp.getTitle();
    }
    
    public gPlayer toGPlayer() {
        gPlayer rt = new gPlayer(name, power);
        rt.bonusPower = bonusPower;
        rt.setTitle(title);
        return rt;
    }

    @Override
    public DataAccess getInstance() {
        Utils.warning("Someone's calling getInstance() on PlayerDataAccess!");
        return null;
    }
    
    public Object[] getUpdateFieldValues() {
        return new Object[] {name, power, bonusPower, title};
    }
    
    public static String[] getUpdateFieldNames() {
        return new String[] {"name", "power", "bonusPower", "title"};
    }
}
