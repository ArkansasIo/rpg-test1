package dq1.core.wowui;

public class WowPanelModel {

    private final WowPanelId id;
    private final String menuLabel;
    private final String title;
    private final String subtitle;

    public WowPanelModel(WowPanelId id, String menuLabel, String title, String subtitle) {
        this.id = id;
        this.menuLabel = menuLabel;
        this.title = title;
        this.subtitle = subtitle;
    }

    public WowPanelId getId() {
        return id;
    }

    public String getMenuLabel() {
        return menuLabel;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }
}
