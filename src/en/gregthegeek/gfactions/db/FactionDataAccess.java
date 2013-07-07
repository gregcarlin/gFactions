package en.gregthegeek.gfactions.db;

import en.gregthegeek.gfactions.faction.CachedFaction;
import en.gregthegeek.util.Utils;

import net.canarymod.CanaryDeserializeException;
import net.canarymod.api.world.position.Location;
import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;

public class FactionDataAccess extends DataAccess {
    @Column(columnName = "id", dataType = Column.DataType.INTEGER, columnType = Column.ColumnType.PRIMARY, autoIncrement = true)
    private int id;
    @Column(columnName = "name", dataType = Column.DataType.STRING, columnType = Column.ColumnType.UNIQUE)
    private String name;
    @Column(columnName = "desc", dataType = Column.DataType.STRING)
    private String desc;
    @Column(columnName = "isOpen", dataType = Column.DataType.BOOLEAN)
    private boolean isOpen;
    @Column(columnName = "isPeaceful", dataType = Column.DataType.BOOLEAN)
    private boolean isPeaceful;
    @Column(columnName = "admin", dataType = Column.DataType.STRING, columnType = Column.ColumnType.UNIQUE)
    private String admin;
    @Column(columnName = "home", dataType = Column.DataType.STRING)
    private String home;
    @Column(columnName = "mods", dataType = Column.DataType.STRING, isList = true)
    private String[] mods;
    @Column(columnName = "members", dataType = Column.DataType.STRING, isList = true)
    private String[] members;
    
    public FactionDataAccess() {
        super("factions", "factions");
        this.id = -1;
        this.name = "";
        this.desc = "";
        this.isOpen = false;
        this.isPeaceful = false;
        this.admin = "";
        this.home = "";
        this.mods = new String[0];
        this.members = new String[0];
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
        this.mods = fac.getMods();
        this.members = fac.getMembers();
    }
    
    public CachedFaction toCachedFaction() {
        Location h;
        try {
            h = Location.fromString(home);
        } catch (CanaryDeserializeException e) {
            h = null;
        }
        CachedFaction rt = new CachedFaction(id, name, desc, isOpen, isPeaceful, admin, h);
        rt.addMods(mods);
        rt.addMembers(members);
        return rt;
    }

    @Override
    public DataAccess getInstance() {
        Utils.warning("Someone's calling getInstance() on FactionDataAccess!");
        return null;
    }
    
    public Object[] getUpdateFieldValues() {
        return new Object[] {name, desc, isOpen, isPeaceful, admin, home, mods, members};
    }
    
    public static String[] getUpdateFieldNames() {
        return new String[] {"name", "desc", "isOpen", "isPeaceful", "admin", "home", "mods", "members"};
    }
}
