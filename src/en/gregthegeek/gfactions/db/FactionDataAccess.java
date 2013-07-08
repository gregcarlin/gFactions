package en.gregthegeek.gfactions.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import en.gregthegeek.gfactions.faction.CachedFaction;

import net.canarymod.CanaryDeserializeException;
import net.canarymod.api.world.position.Location;
import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;

public class FactionDataAccess extends DataAccess {
    @Column(columnName = "id", dataType = Column.DataType.INTEGER, columnType = Column.ColumnType.PRIMARY, autoIncrement = true)
    public int id;
    @Column(columnName = "name", dataType = Column.DataType.STRING, columnType = Column.ColumnType.UNIQUE)
    public String name;
    @Column(columnName = "desc", dataType = Column.DataType.STRING)
    public String desc;
    @Column(columnName = "isOpen", dataType = Column.DataType.BOOLEAN)
    public boolean isOpen;
    @Column(columnName = "isPeaceful", dataType = Column.DataType.BOOLEAN)
    public boolean isPeaceful;
    @Column(columnName = "admin", dataType = Column.DataType.STRING, columnType = Column.ColumnType.UNIQUE)
    public String admin;
    @Column(columnName = "home", dataType = Column.DataType.STRING)
    public String home;
    @Column(columnName = "mods", dataType = Column.DataType.STRING, isList = true)
    public List<String> mods;
    @Column(columnName = "members", dataType = Column.DataType.STRING, isList = true)
    public List<String> members;
    
    public FactionDataAccess() {
        super("factions", "factions");
        this.id = -1;
        this.name = "";
        this.desc = "";
        this.isOpen = false;
        this.isPeaceful = false;
        this.admin = "";
        this.home = "";
        this.mods = new ArrayList<String>(0);
        this.members = new ArrayList<String>(0);
    }
    
    public FactionDataAccess(CachedFaction fac) {
        super("factions");
        this.id = fac.getId();
        this.name = fac.getName();
        this.desc = fac.getDescription();
        this.isOpen = fac.isOpen();
        this.isPeaceful = fac.isPeaceful();
        this.admin = fac.getAdmin();
        Location h = fac.getHome();
        this.home = h == null ? "" : h.toString();
        this.mods = Arrays.asList(fac.getMods());
        this.members = Arrays.asList(fac.getMembers());
    }
    
    public CachedFaction toCachedFaction() {
        Location h;
        try {
            h = Location.fromString(home);
        } catch (CanaryDeserializeException e) {
            h = null;
        }
        CachedFaction rt = new CachedFaction(id, name, desc, isOpen, isPeaceful, admin, h);
        rt.addMods(mods.toArray(new String[0]));
        rt.addMembers(members.toArray(new String[0]));
        return rt;
    }

    @Override
    public DataAccess getInstance() {
        return new FactionDataAccess();
    }
    
    public Object[] getUpdateFieldValues() {
        return new Object[] {name, desc, isOpen, isPeaceful, admin, home, mods, members};
    }
    
    public static String[] getUpdateFieldNames() {
        return new String[] {"name", "desc", "isOpen", "isPeaceful", "admin", "home", "mods", "members"};
    }
}
