package dq1.core;

/**
 * Spell class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Spell {
    // Names and descriptions for 800 spell schools and classes
    public static final String[] SCHOOL_NAMES = new String[800];
    public static final String[] SCHOOL_DESCRIPTIONS = new String[800];
    public static final String[] CLASS_NAMES = new String[800];
    public static final String[] CLASS_DESCRIPTIONS = new String[800];

    static {
        for (int i = 0; i < 800; i++) {
            SCHOOL_NAMES[i] = "School " + (i + 1);
            SCHOOL_DESCRIPTIONS[i] = "Description for School " + (i + 1);
            CLASS_NAMES[i] = "Magic Class " + (i + 1);
            CLASS_DESCRIPTIONS[i] = "Description for Magic Class " + (i + 1);
        }
    }
    
    public static final Spell EMPTY 
            = new Spell(0, "", 0, 0, false, false, "Arcane", "Basic", "Normal", "None", "None", 0);
    
    private final int id;
    private final String name;
    private final int level;
    private final int mp;
    private final boolean useInBattle;
    private final boolean useInMap;

    private final String magicSchool;
    private final String spellClass;
    private final String spellType;
    private final String attribute;
    private final String subtype;
    private final int subStat;
    private int schoolIndex = 0; // 0-799
    private int magicClassIndex = 0; // 0-799

    private Script script;

    public Spell(int id, String name, int level, int mp
                                    , boolean useInBattle, boolean useInMap, String magicSchool, String spellClass, String spellType, String attribute, String subtype, int subStat) {
        
        this.id = id;
        this.name = name;
        this.level = level;
        this.mp = mp;
        this.useInBattle = useInBattle;
        this.useInMap = useInMap;
        this.magicSchool = magicSchool;
        this.spellClass = spellClass;
        this.spellType = spellType;
        this.attribute = attribute;
        this.subtype = subtype;
        this.subStat = subStat;
    }
    
    public Spell(String serializedData) {
        String[] args = serializedData.trim().split(",");
        String[] h = args[0].trim().split("\\s+");
        id = Integer.parseInt(h[1]);
        name = args[1].trim();
        level = Integer.parseInt(args[2].trim());
        mp = Integer.parseInt(args[3].trim());
        useInBattle = Boolean.parseBoolean(args[4].trim());
        useInMap = Boolean.parseBoolean(args[5].trim());
        magicSchool = args.length > 6 ? args[6].trim() : "Arcane";
        spellClass = args.length > 7 ? args[7].trim() : "Basic";
        spellType = args.length > 8 ? args[8].trim() : "Normal";
        attribute = args.length > 9 ? args[9].trim() : "None";
        subtype = args.length > 10 ? args[10].trim() : "None";
        subStat = args.length > 11 ? Integer.parseInt(args[11].trim()) : 0;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getMp() {
        return mp;
    }

    public boolean isUseInBattle() {
        return useInBattle;
    }

    public boolean isUseInMap() {
        return useInMap;
    }

    public String getMagicSchool() {
        return magicSchool;
    }

    public String getSpellClass() {
        return spellClass;
    }

    public String getSpellType() {
        return spellType;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getSubtype() {
        return subtype;
    }

    public int getSubStat() {
        return subStat;
    }

    public int getSchool() {
        return schoolIndex;
    }

    public void setSchool(int school) {
        this.schoolIndex = Math.max(0, Math.min(799, school));
    }

    public int getMagicClass() {
        return magicClassIndex;
    }

    public void setMagicClass(int magicClass) {
        this.magicClassIndex = Math.max(0, Math.min(799, magicClass));
    }

    public String getSchoolName() {
        return SCHOOL_NAMES[schoolIndex];
    }

    public String getSchoolDescription() {
        return SCHOOL_DESCRIPTIONS[schoolIndex];
    }

    public String getMagicClassName() {
        return CLASS_NAMES[magicClassIndex];
    }

    public String getMagicClassDescription() {
        return CLASS_DESCRIPTIONS[magicClassIndex];
    }

    public Script getScript() {
        return script;
    }

    public void setScript(Script script) {
        this.script = script;
    }
    
    // return boolean -> cast successfully
    public boolean cast(String when) throws Exception {
        if (script != null) {
            return script.execute(when);
        }
        return false;
    }

    @Override
    public String toString() {
        return "Spell{" + "id=" + id + ", name=" + name + ", level=" + level 
                + ", mp=" + mp + ", useInBattle=" + useInBattle 
                + ", useInMap=" + useInMap + '}';
    }
    
}
