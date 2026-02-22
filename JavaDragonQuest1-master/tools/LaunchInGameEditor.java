public class LaunchInGameEditor {
    public static void main(String[] args) throws Exception {
        Class<?> cls = Class.forName("dq1.editor.GameEditorFrame");
        java.lang.reflect.Method m = cls.getMethod("showEditor");
        m.invoke(null);
    }
}
