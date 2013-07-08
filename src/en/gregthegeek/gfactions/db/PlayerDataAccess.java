package en.gregthegeek.gfactions.db;

import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;
import en.gregthegeek.gfactions.player.gPlayer;

public class PlayerDataAccess extends DataAccess {
    @Column(columnName = "name", dataType = Column.DataType.STRING)
    public String name;
    @Column(columnName = "power", dataType = Column.DataType.INTEGER)
    public int power;
    @Column(columnName = "bonusPower", dataType = Column.DataType.INTEGER)
    public int bonusPower;
    @Column(columnName = "title", dataType = Column.DataType.STRING)
    public String title;
    
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
        return new PlayerDataAccess();
    }
    
    public Object[] getUpdateFieldValues() {
        return new Object[] {name, power, bonusPower, title};
    }
    
    public static String[] getUpdateFieldNames() {
        return new String[] {"name", "power", "bonusPower", "title"};
    }
}
