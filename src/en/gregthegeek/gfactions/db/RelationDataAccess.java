package en.gregthegeek.gfactions.db;

import en.gregthegeek.gfactions.relation.Relation;
import en.gregthegeek.util.Utils;
import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;

public class RelationDataAccess extends DataAccess {
    @Column(columnName = "type", dataType = Column.DataType.INTEGER)
    private int type;
    @Column(columnName = "one", dataType = Column.DataType.INTEGER)
    private int one;
    @Column(columnName = "two", dataType = Column.DataType.INTEGER)
    private int two;
    
    public RelationDataAccess() {
        super("factions", "relations");
        type = -1;
        one = -1;
        two = -1;
    }
    
    public RelationDataAccess(Relation relation) {
        super("factions", "relations");
        type = relation.type.ordinal();
        one = relation.getOneId();
        two = relation.getTwoId();
    }
    
    public Relation toRelation() {
        return new Relation(Relation.Type.values()[type], one, two);
    }

    @Override
    public DataAccess getInstance() {
        Utils.warning("Someone's calling getInstance() on FactionDataAccess!");
        return null;
    }
}
