package en.gregthegeek.gfactions.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.canarymod.Canary;
import net.canarymod.database.DataAccess;
import net.canarymod.database.Database;
import net.canarymod.database.exceptions.DatabaseReadException;
import net.canarymod.database.exceptions.DatabaseWriteException;

import en.gregthegeek.gfactions.faction.CachedFaction;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.gfactions.land.Land;
import en.gregthegeek.gfactions.player.gPlayer;
import en.gregthegeek.gfactions.relation.Relation;
import en.gregthegeek.util.AdvancedPropertiesFile;
import en.gregthegeek.util.Utils;

public class CanarySource implements Datasource {
    private static final Database db = Canary.db();
    private static final AdvancedPropertiesFile balances = getProps("balances.txt");
    
    public CanarySource() {
        DataAccess table = new FactionDataAccess(); // empty
        try {
            db.updateSchema(table);
        } catch (DatabaseWriteException e) {
            report(e);
        }
    }

    @Override
    public CachedFaction getFaction(int id) {
        try {
            FactionDataAccess da = new FactionDataAccess();
            db.load(da, new String[] {"id"}, new Object[] {id});
            if(da.hasData()) return da.toCachedFaction();
        } catch (DatabaseReadException e) {
            report(e);
        }
        return null;
    }

    @Override
    public Faction[] getAllFactions() {
        // this 'query' sucks
        try {
            List<DataAccess> list = new ArrayList<DataAccess>();
            db.loadAll(new FactionDataAccess(), list, new String[0], new Object[0]);
            Faction[] rt = new Faction[list.size()];
            for(int i=0; i<rt.length; i++) {
                rt[i] = ((FactionDataAccess) list.get(i)).toCachedFaction();
            }
            return rt;
        } catch (DatabaseReadException e) {
            report(e);
        }
        return new Faction[0];
    }

    @Override
    public Land[] getAllLand() {
        try {
            List<DataAccess> list = new ArrayList<DataAccess>();
            db.loadAll(new LandDataAccess(), list, new String[0], new Object[0]);
            Land[] rt = new Land[list.size()];
            for(int i=0; i<rt.length; i++) {
                rt[i] = ((LandDataAccess) list.get(i)).toLand();
            }
            return rt;
        } catch (DatabaseReadException e) {
            report(e);
        }
        return new Land[0];
    }

    @Override
    public Relation getRelation(Faction one, Faction two) {
        try {
            RelationDataAccess da = new RelationDataAccess();
            int a = one.getId();
            int b = two.getId();
            db.load(da, new String[] {"one", "two"}, new Object[] {a, b});
            if(da.hasData()) return da.toRelation();
            db.load(da, new String[] {"one", "two"}, new Object[] {b, a});
            if(da.hasData()) return da.toRelation();
        } catch (DatabaseReadException e) {
            report(e);
        }
        return new Relation(Relation.Type.NEUTRAL, one, two);
    }

    @Override
    public Relation[] getRelationsWith(Faction f) {
        try {
            List<DataAccess> list = new ArrayList<DataAccess>();
            int id = f.getId();
            db.loadAll(new LandDataAccess(), list, new String[] {"one"}, new Object[] {id});
            db.loadAll(new LandDataAccess(), list, new String[] {"two"}, new Object[] {id});
            Relation[] rt = new Relation[list.size()];
            for(int i=0; i<rt.length; i++) {
                rt[i] = ((RelationDataAccess) list.get(i)).toRelation();
            }
            return rt;
        } catch (DatabaseReadException e) {
            report(e);
        }
        return new Relation[0];
    }

    @Override
    public void fix() {
        // nah
    }

    @Override
    public gPlayer getPlayer(String name) {
        try {
            PlayerDataAccess da = new PlayerDataAccess();
            db.load(da, new String[] {"name"}, new Object[] {name});
            if(da.hasData()) return da.toGPlayer();
        } catch (DatabaseReadException e) {
            report(e);
        }
        return null;
    }

    @Override
    public void close() {
        // do nothing?
    }
    
    private boolean hasFaction(String name) {
        try {
            FactionDataAccess da = new FactionDataAccess();
            db.load(da, new String[] {"name"}, new Object[] {name});
            return da.hasData();
        } catch (DatabaseReadException e) {
            report(e);
        }
        return false;
    }

    @Override
    public void save(CachedFaction faction) {
        try {
            FactionDataAccess da = new FactionDataAccess(faction);
            if(hasFaction(faction.getName())) {
                db.update(da, FactionDataAccess.getUpdateFieldNames(), da.getUpdateFieldValues());
            } else {
                db.insert(da);
            }
        } catch (DatabaseWriteException e) {
            report(e);
        }
    }

    private boolean hasPlayer(String name) {
        try {
            PlayerDataAccess da = new PlayerDataAccess();
            db.load(da, new String[] {"name"}, new Object[] {name});
            return da.hasData();
        } catch (DatabaseReadException e) {
            report(e);
        }
        return false;
    }
    
    @Override
    public void save(gPlayer[] players) {
        DatabaseWriteException ex = null;
        for(gPlayer gp : players) {
            try {
                PlayerDataAccess da = new PlayerDataAccess(gp);
                if(hasPlayer(gp.getName())) {
                    db.update(da, PlayerDataAccess.getUpdateFieldNames(), da.getUpdateFieldValues());
                } else {
                    db.insert(da);
                }
            } catch (DatabaseWriteException e) {
                ex = e;
            }
        }
        if(ex != null) report(ex);
    }
    
    private boolean hasRelation(int one, int two) {
        try {
            RelationDataAccess da = new RelationDataAccess();
            db.load(da, new String[] {"one", "two"}, new Object[] {one, two});
            return da.hasData();
        } catch (DatabaseReadException e) {
            report(e);
        }
        return false;
    }

    @Override
    public void save(Relation[] relations) {
        DatabaseWriteException ex = null;
        for(Relation r : relations) {
            try {
                RelationDataAccess da = new RelationDataAccess(r);
                if(hasRelation(r.getOneId(), r.getTwoId())) {
                    db.update(da, new String[] {"type"}, new Object[] {r.type.ordinal()});
                } else {
                    db.insert(da);
                }
            } catch (DatabaseWriteException e) {
                ex = e;
            }
        }
        if(ex != null) report(ex);
    }
    
    private boolean hasLand(int x, int z) {
        try {
            LandDataAccess da = new LandDataAccess();
            db.load(da, new String[] {"x", "z"}, new Object[] {x, z});
            return da.hasData();
        } catch (DatabaseReadException e) {
            report(e);
        }
        return false;
    }

    @Override
    public void save(Land land) {
        try {
            LandDataAccess da = new LandDataAccess(land);
            if(hasLand(land.getX(), land.getZ())) {
                db.update(da, LandDataAccess.getUpdateFieldNames(), da.getUpdateFieldValues());
            } else {
                db.insert(da);
            }
        } catch (DatabaseWriteException e) {
            report(e);
        }
    }

    @Override
    public void delete(Faction f) {
        try {
            db.remove(new FactionDataAccess().getName(), new String[] {"id"}, new Object[] {f.getId()});
        } catch (DatabaseWriteException e) {
            report(e);
        }
    }

    @Override
    public void delete(Land l) {
        try {
            db.remove(new LandDataAccess().getName(), new String[] {"x", "z"}, new Object[] {l.getX(), l.getZ()});
        } catch (DatabaseWriteException e) {
            report(e);
        }
    }

    @Override
    public void delete(Relation r) {
        try {
            db.remove(new RelationDataAccess().getName(), new String[] {"one", "two"}, new Object[] {r.getOneId(), r.getTwoId()});
        } catch (DatabaseWriteException e) {
            report(e);
        }
    }

    @Override
    public int getBalance(String player) {
        return balances.getInt(player);
    }

    @Override
    public int getBalance(int fID) {
        return balances.getInt(Integer.toString(fID));
    }

    @Override
    public void savePlayerBalances(HashMap<String, Integer> players) {
        for(Entry<String, Integer>  e : players.entrySet()) {
            balances.setInt(e.getKey(), e.getValue());
        }
        try {
            balances.save();
        } catch (IOException e1) {
            Utils.warning("Error writing balances.txt file: %s", e1.getMessage());
        }
    }

    @Override
    public void saveFactionBalances(HashMap<Integer, Integer> factions) {
        for(Entry<Integer, Integer>  e : factions.entrySet()) {
            balances.setInt(Integer.toString(e.getKey()), e.getValue());
        }
        try {
            balances.save();
        } catch (IOException e1) {
            Utils.warning("Error writing balances.txt file: %s", e1.getMessage());
        }
    }
    
    private static void report(DatabaseReadException e) {
        Utils.warning("Error reading from database: %s", e.getMessage());
    }
    
    private static void report(DatabaseWriteException e) {
        Utils.warning("Error writing to database: %s", e.getMessage());
    }
    
    private static AdvancedPropertiesFile getProps(String name) {
        try {
            return new AdvancedPropertiesFile("config/gFactions/" + name);
        } catch (IOException e) {
            Utils.warning("Error reading balances.txt file: %s", e.getMessage());
        }
        return null;
    }
}
