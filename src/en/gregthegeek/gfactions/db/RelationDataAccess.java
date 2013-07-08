package en.gregthegeek.gfactions.db;

import en.gregthegeek.gfactions.relation.Relation;
import net.canarymod.database.Column;
import net.canarymod.database.DataAccess;

public class RelationDataAccess extends DataAccess {
    private static final String NAME = "relations";
    
    @Column(columnName = "type", dataType = Column.DataType.INTEGER)
    public int type;
    @Column(columnName = "one", dataType = Column.DataType.INTEGER)
    public int one;
    @Column(columnName = "two", dataType = Column.DataType.INTEGER)
    public int two;
    
    public RelationDataAccess() {
        super(CanarySource.PREFIX, NAME);
        type = -1;
        one = -1;
        two = -1;
    }
    
    public RelationDataAccess(Relation relation) {
        super(CanarySource.PREFIX, NAME);
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
