package dq1.core;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashSet;
import java.util.Set;
import static dq1.core.Settings.*;

/**
 * Input class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Input implements KeyListener, MouseListener {
    
    private static final Set<Integer> keyPressed = new HashSet<>();
    private static final Set<Integer> keyPressedConsumed = new HashSet<>();
    private static KeyListener listener;
    private static int lastKeyPressed = -1;

    public static void setListener(KeyListener listener) {
        Input.listener = listener;
    }

    public static synchronized void clearState() {
        keyPressed.clear();
        keyPressedConsumed.clear();
        lastKeyPressed = -1;
    }

    public static synchronized int consumeLastKeyPressed() {
        int keyCode = lastKeyPressed;
        lastKeyPressed = -1;
        return keyCode;
    }

    public static synchronized boolean isKeyPressed(int keyCode) {
        return keyPressed.contains(keyCode);
    }

    public static synchronized boolean isKeyJustPressed(int keyCode) {
        if (!keyPressedConsumed.contains(keyCode) 
                && keyPressed.contains(keyCode)) {
            
            keyPressedConsumed.add(keyCode);
            return true;
        }
        return false;
    }

    private static synchronized void onPress(int keyCode) {
        keyPressed.add(keyCode);
    }

    private static synchronized void onRelease(int keyCode) {
        keyPressed.remove(keyCode);
        keyPressedConsumed.remove(keyCode);
    }
    
    @Override
    public synchronized void keyTyped(KeyEvent e) {
        if (listener != null) {
            listener.keyTyped(e);
        }
    }

    @Override
    public synchronized void keyPressed(KeyEvent e) {
        onPress(e.getKeyCode());
        lastKeyPressed = e.getKeyCode();
        if (listener != null) {
            listener.keyPressed(e);
        }
    }
    
    @Override
    public synchronized void keyReleased(KeyEvent e) {
        onRelease(e.getKeyCode());
        if (listener != null) {
            listener.keyReleased(e);
        }
    }

    @Override
    public synchronized void mousePressed(MouseEvent e) {
        if (!MOUSE_ENABLED) {
            return;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            onPress(KEY_CONFIRM);
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            onPress(KEY_CANCEL);
        }
        Component source = (Component) e.getSource();
        source.requestFocusInWindow();
    }

    @Override
    public synchronized void mouseReleased(MouseEvent e) {
        if (!MOUSE_ENABLED) {
            return;
        }
        if (e.getButton() == MouseEvent.BUTTON1) {
            onRelease(KEY_CONFIRM);
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            onRelease(KEY_CANCEL);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }
    
}
