package en.gregthegeek.gfactions.db;

import en.gregthegeek.gfactions.relation.Relation;
import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;

public class RelationDataAccess extends DataAccess {
    @Column(columnName = "type", dataType = Column.DataType.INTEGER)
    public int type;
    @Column(columnName = "one", dataType = Column.DataType.INTEGER)
    public int one;
    @Column(columnName = "two", dataType = Column.DataType.INTEGER)
    public int two;
    
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
        return new RelationDataAccess();
    }
}
