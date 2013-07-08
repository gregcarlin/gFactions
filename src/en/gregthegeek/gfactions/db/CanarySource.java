package en.gregthegeek.gfactions.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
    protected static final String PREFIX = "factions";
    private static final AdvancedPropertiesFile balances = getProps("balances.txt");
    
    public CanarySource() {
        try {
            Database.get().updateSchema(new FactionDataAccess());
        } catch (DatabaseWriteException e) {
            report(e, "CanarySource()");
        }
    }

    @Override
    public CachedFaction getFaction(int id) {
        try {
            FactionDataAccess da = new FactionDataAccess();
            Database.get().load(da, new String[] {"id"}, new Object[] {id});
            if(da.hasData()) return da.toCachedFaction();
        } catch (DatabaseReadException e) {
            report(e, "getFaction(int)");
        }
        return null;
    }

    @Override
    public Faction[] getAllFactions() {
        // this 'query' sucks
        try {
            List<DataAccess> list = new ArrayList<DataAccess>();
            Database.get().loadAll(new FactionDataAccess(), list, new String[0], new Object[0]);
            Faction[] rt = new Faction[list.size()];
            for(int i=0; i<rt.length; i++) {
                rt[i] = ((FactionDataAccess) list.get(i)).toCachedFaction();
            }
            return rt;
        } catch (DatabaseReadException e) {
            report(e, "getAllFactions()");
        }
        return new Faction[0];
    }

    @Override
    public Land[] getAllLand() {
        try {
            List<DataAccess> list = new ArrayList<DataAccess>();
            Database.get().loadAll(new LandDataAccess(), list, new String[0], new Object[0]);
            Land[] rt = new Land[list.size()];
            for(int i=0; i<rt.length; i++) {
                rt[i] = ((LandDataAccess) list.get(i)).toLand();
            }
            return rt;
        } catch (DatabaseReadException e) {
            report(e, "getAllLand()");
        }
        return new Land[0];
    }

    @Override
    public Relation getRelation(Faction one, Faction two) {
        try {
            RelationDataAccess da = new RelationDataAccess();
            int a = one.getId();
            int b = two.getId();
            Database.get().load(da, new String[] {"one", "two"}, new Object[] {a, b});
            if(da.hasData()) return da.toRelation();
            Database.get().load(da, new String[] {"one", "two"}, new Object[] {b, a});
            if(da.hasData()) return da.toRelation();
        } catch (DatabaseReadException e) {
            report(e, "getRelation(Faction, Faction)");
        }
        return new Relation(Relation.Type.NEUTRAL, one, two);
    }

    @Override
    public Relation[] getRelationsWith(Faction f) {
        try {
            List<DataAccess> list = new ArrayList<DataAccess>();
            int id = f.getId();
            Database.get().loadAll(new LandDataAccess(), list, new String[] {"one"}, new Object[] {id});
            Database.get().loadAll(new LandDataAccess(), list, new String[] {"two"}, new Object[] {id});
            Relation[] rt = new Relation[list.size()];
            for(int i=0; i<rt.length; i++) {
                rt[i] = ((RelationDataAccess) list.get(i)).toRelation();
            }
            return rt;
        } catch (DatabaseReadException e) {
            report(e, "getRelationsWith(Faction)");
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
            Database.get().load(da, new String[] {"name"}, new Object[] {name});
            if(da.hasData()) return da.toGPlayer();
        } catch (DatabaseReadException e) {
            report(e, "getPlayer(String)");
        }
        return null;
    }

    @Override
    public void close() {
        // do nothing?
    }
    
    private boolean hasFaction(int id) { // TODO: remove comments
        try {
            System.out.printf("searching for fac with id %d%n", id);
            FactionDataAccess da = new FactionDataAccess();
            Database.get().load(da, new String[] {"id"}, new Object[] {id});
            System.out.printf("loaded, hasData: %s%n", da.hasData() ? "yes" : "no");
            return da.hasData();
        } catch (DatabaseReadException e) {
            System.out.printf("read exception while looking for fac%n");
            report(e, "hasFaction(int)");
        }
        return false;
    }

    @Override
    public void save(CachedFaction faction) {
        try {
            FactionDataAccess da = new FactionDataAccess(faction);
            if(hasFaction(da.id)) {
                //System.out.printf("UPDATING FACTION %d WITH VALUES %s%n", da.id, da.getUpdateFieldValues());
                Database.get().update(da, new String[] {"id"}, new Object[] {da.id});
            } else {
                //System.out.printf("INSERTING NEW FACTION WITH VALUES %s%n", da.getUpdateFieldValues());
                Database.get().insert(da);
            }
        } catch (DatabaseWriteException e) {
            report(e, "save(CachedFaction)");
        }
    }

    private boolean hasPlayer(String name) {
        try {
            PlayerDataAccess da = new PlayerDataAccess();
            Database.get().load(da, new String[] {"name"}, new Object[] {name});
            return da.hasData();
        } catch (DatabaseReadException e) {
            report(e, "hasPlayer(String)");
        }
        return false;
    }
    
    @Override
    public void save(gPlayer[] players) {
        DatabaseWriteException ex = null;
        for(gPlayer gp : players) {
            try {
                PlayerDataAccess da = new PlayerDataAccess(gp);
                if(hasPlayer(da.name)) {
                    Database.get().update(da, new String[] {"name"}, new Object[] {da.name});
                } else {
                    Database.get().insert(da);
                }
            } catch (DatabaseWriteException e) {
                ex = e;
            }
        }
        if(ex != null) report(ex, "save(gPlayer[])");
    }
    
    private boolean hasRelation(int one, int two) {
        try {
            RelationDataAccess da = new RelationDataAccess();
            Database.get().load(da, new String[] {"one", "two"}, new Object[] {one, two});
            return da.hasData();
        } catch (DatabaseReadException e) {
            report(e, "hasRelation(int, int)");
        }
        return false;
    }

    @Override
    public void save(Relation[] relations) {
        DatabaseWriteException ex = null;
        for(Relation r : relations) {
            try {
                RelationDataAccess da = new RelationDataAccess(r);
                if(hasRelation(da.one, da.two)) {
                    Database.get().update(da, new String[] {"one", "two"}, new Object[] {da.one, da.two});
                } else {
                    Database.get().insert(da);
                }
            } catch (DatabaseWriteException e) {
                ex = e;
            }
        }
        if(ex != null) report(ex, "save(Relation[])");
    }
    
    private boolean hasLand(int x, int z) {
        try {
            LandDataAccess da = new LandDataAccess();
            Database.get().load(da, new String[] {"x", "z"}, new Object[] {x, z});
            return da.hasData();
        } catch (DatabaseReadException e) {
            report(e, "hasLand(int, int)");
        }
        return false;
    }

    @Override
    public void save(Land land) {
        try {
            LandDataAccess da = new LandDataAccess(land);
            if(hasLand(da.x, da.z)) {
                Database.get().update(da, new String[] {"x", "z"}, new Object[] {da.x, da.z});
            } else {
                Database.get().insert(da);
            }
        } catch (DatabaseWriteException e) {
            report(e, "save(Land)");
        }
    }

    @Override
    public void delete(Faction f) {
        try {
            Database.get().remove(new FactionDataAccess().getName(), new String[] {"id"}, new Object[] {f.getId()});
        } catch (DatabaseWriteException e) {
            report(e, "delete(Faction)");
        }
    }

    @Override
    public void delete(Land l) {
        try {
            Database.get().remove(new LandDataAccess().getName(), new String[] {"x", "z"}, new Object[] {l.getX(), l.getZ()});
        } catch (DatabaseWriteException e) {
            report(e, "delete(Land)");
        }
    }

    @Override
    public void delete(Relation r) {
        try {
            Database.get().remove(new RelationDataAccess().getName(), new String[] {"one", "two"}, new Object[] {r.getOneId(), r.getTwoId()});
        } catch (DatabaseWriteException e) {
            report(e, "delete(Relation)");
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
    
    private static void report(DatabaseReadException e, String caller) {
        Utils.warning("Error reading from database: %s", e.getMessage());
        Utils.warning("This error from: %s%n", caller);
    }
    
    private static void report(DatabaseWriteException e, String caller) {
        Utils.warning("Error writing to database: %s", e.getMessage());
        Utils.warning("This error from: %s%n", caller);
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
