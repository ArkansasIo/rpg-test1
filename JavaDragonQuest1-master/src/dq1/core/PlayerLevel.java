package dq1.core;

/**
 * PlayerLevel class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class PlayerLevel {

    public static int lastLevel;
    
    private final int lv;
    private final int str;
    private final int agi;
    private final int hp;
    private final int mp;
    private final int xp;
    private final String playerClass;
    private final String playerType;
    private final String attribute;
    private final String subtype;
    private final int subStat;
    private final int worldTier;

    public PlayerLevel(int lv, int str, int agi, int hp, int mp, int xp, String playerClass, String playerType, String attribute, String subtype, int subStat, int worldTier) {
        this.lv = lv;
        this.str = str;
        this.agi = agi;
        this.hp = hp;
        this.mp = mp;
        this.xp = xp;
        this.playerClass = playerClass;
        this.playerType = playerType;
        this.attribute = attribute;
        this.subtype = subtype;
        this.subStat = subStat;
        this.worldTier = worldTier;
    }

    public PlayerLevel(String serializedData) {
        String[] args = serializedData.trim().split(",");
        String[] h = args[0].trim().split("\\s+");
        lv = Integer.parseInt(h[1]);
        str = Integer.parseInt(args[1].trim());
        agi = Integer.parseInt(args[2].trim());
        hp = Integer.parseInt(args[3].trim());
        mp = Integer.parseInt(args[4].trim());
        xp = Integer.parseInt(args[5].trim());
        playerClass = args.length > 6 ? args[6].trim() : "Hero";
        playerType = args.length > 7 ? args[7].trim() : "Normal";
        attribute = args.length > 8 ? args[8].trim() : "None";
        subtype = args.length > 9 ? args[9].trim() : "None";
        subStat = args.length > 10 ? Integer.parseInt(args[10].trim()) : 0;
        worldTier = args.length > 11 ? Integer.parseInt(args[11].trim()) : 1;
    }

    public int getLv() {
        return lv;
    }

    public int getStr() {
        return str;
    }

    public int getAgi() {
        return agi;
    }

    public int getHP() {
        return hp;
    }

    public int getMP() {
        return mp;
    }

    public int getXP() {
        return xp;
    }

    public String getPlayerClass() { return playerClass; }
    public String getPlayerType() { return playerType; }
    public String getAttribute() { return attribute; }
    public String getSubtype() { return subtype; }
    public int getSubStat() { return subStat; }
    public int getWorldTier() { return worldTier; }

    @Override
    public String toString() {
        return "PlayerLevel{" + "lv=" + lv + ", str=" + str + ", agi=" 
                    + agi + ", hp=" + hp + ", mp=" + mp + ", xp=" + xp + '}';
    }
    
}