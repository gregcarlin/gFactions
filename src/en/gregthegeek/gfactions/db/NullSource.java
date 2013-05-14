package en.gregthegeek.gfactions.db;

import java.util.HashMap;

import en.gregthegeek.gfactions.faction.CachedFaction;
import en.gregthegeek.gfactions.faction.Faction;
import en.gregthegeek.gfactions.land.Land;
import en.gregthegeek.gfactions.player.gPlayer;
import en.gregthegeek.gfactions.relation.Relation;

public class NullSource implements Datasource {

    @Override
    public CachedFaction getFaction(int id) {
        return null;
    }

    @Override
    public Faction[] getAllFactions() {
        return new Faction[0];
    }

    @Override
    public Land[] getAllLand() {
        return new Land[0];
    }

    @Override
    public Relation getRelation(Faction one, Faction two) {
        return new Relation(Relation.Type.NEUTRAL, one, two);
    }

    @Override
    public Relation[] getRelationsWith(Faction f) {
        return new Relation[0];
    }

    @Override
    public void fix() {
        
    }

    @Override
    public gPlayer getPlayer(String name) {
        return null;
    }

    @Override
    public void close() {
        
    }

    @Override
    public void save(CachedFaction faction) {
        
    }

    @Override
    public void save(gPlayer[] players) {
        
    }

    @Override
    public void save(Relation[] relations) {
        
    }

    @Override
    public void save(Land land) {
        
    }

    @Override
    public void delete(Faction f) {
        
    }

    @Override
    public void delete(Land l) {
        
    }

    @Override
    public void delete(Relation r) {
        
    }

    @Override
    public int getBalance(String player) {
        return 0;
    }

    @Override
    public int getBalance(int fID) {
        return 0;
    }

    @Override
    public void savePlayerBalances(HashMap<String, Integer> players) {
        
    }

    @Override
    public void saveFactionBalances(HashMap<Integer, Integer> factions) {
        
    }
}
