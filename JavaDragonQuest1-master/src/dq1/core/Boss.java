package dq1.core;

public class Boss {
    private int bossType = 0; // 0-199
    private int bossClass = 0; // 0-199
    private boolean isWorldBoss = false;

    public static final String[] TYPE_NAMES = new String[200];
    public static final String[] TYPE_DESCRIPTIONS = new String[200];
    public static final String[] CLASS_NAMES = new String[200];
    public static final String[] CLASS_DESCRIPTIONS = new String[200];

    static {
        for (int i = 0; i < 200; i++) {
            TYPE_NAMES[i] = "Boss Type " + (i + 1);
            TYPE_DESCRIPTIONS[i] = "Description for Boss Type " + (i + 1);
            CLASS_NAMES[i] = "Boss Class " + (i + 1);
            CLASS_DESCRIPTIONS[i] = "Description for Boss Class " + (i + 1);
        }
    }

    public int getBossType() { return bossType; }
    public void setBossType(int bossType) { this.bossType = Math.max(0, Math.min(199, bossType)); }

    public int getBossClass() { return bossClass; }
    public void setBossClass(int bossClass) { this.bossClass = Math.max(0, Math.min(199, bossClass)); }

    public boolean isWorldBoss() { return isWorldBoss; }
    public void setWorldBoss(boolean worldBoss) { this.isWorldBoss = worldBoss; }

    public String getBossTypeName() { return TYPE_NAMES[bossType]; }
    public String getBossTypeDescription() { return TYPE_DESCRIPTIONS[bossType]; }
    public String getBossClassName() { return CLASS_NAMES[bossClass]; }
    public String getBossClassDescription() { return CLASS_DESCRIPTIONS[bossClass]; }
}