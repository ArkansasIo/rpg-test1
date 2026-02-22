package mmorpg.entities;

public class FantasyEntityProfile {

    private final int id;
    private final FantasyEntityGroup group;
    private final int typeId;
    private final String typeName;
    private final String entityClass;
    private final String subClass;
    private final String subType;
    private final String name;

    public FantasyEntityProfile(int id, FantasyEntityGroup group, int typeId
            , String typeName, String entityClass, String subClass
            , String subType, String name) {

        this.id = id;
        this.group = group;
        this.typeId = typeId;
        this.typeName = typeName;
        this.entityClass = entityClass;
        this.subClass = subClass;
        this.subType = subType;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public FantasyEntityGroup getGroup() {
        return group;
    }

    public int getTypeId() {
        return typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public String getSubClass() {
        return subClass;
    }

    public String getSubType() {
        return subType;
    }

    public String getName() {
        return name;
    }
}
