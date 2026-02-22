package dq1.core.rpg;

public class RpgActionResult {

    private final boolean success;
    private final RpgActionType actionType;
    private final String message;

    private RpgActionResult(boolean success, RpgActionType actionType, String message) {
        this.success = success;
        this.actionType = actionType;
        this.message = message;
    }

    public static RpgActionResult ok(RpgActionType actionType, String message) {
        return new RpgActionResult(true, actionType, message);
    }

    public static RpgActionResult fail(RpgActionType actionType, String message) {
        return new RpgActionResult(false, actionType, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public RpgActionType getActionType() {
        return actionType;
    }

    public String getMessage() {
        return message;
    }
}
