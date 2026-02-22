package dq1.editor;

import dq1.core.GameAPI;
import dq1.core.StorySystem;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Story systems editor for acts/chapters/quests.
 */
public class StorySystemsEditorPanel extends JPanel {
    private final JComboBox<Integer> actSelector = new JComboBox<>();
    private final JComboBox<Integer> chapterSelector = new JComboBox<>();
    private final JTextField questName = new JTextField(16);
    private final JTextField questDesc = new JTextField(28);
    private final JTextArea output = new JTextArea();

    public StorySystemsEditorPanel() {
        super(new BorderLayout(8, 8));
        for (int i = 1; i <= StorySystem.ACT_COUNT; i++) {
            actSelector.addItem(i);
        }
        for (int i = 1; i <= StorySystem.CHAPTERS_PER_ACT; i++) {
            chapterSelector.addItem(i);
        }
        output.setEditable(false);
        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
        refreshView();
    }

    private JPanel buildTopBar() {
        JPanel top = new JPanel();
        top.add(new JLabel("Act:"));
        top.add(actSelector);
        top.add(new JLabel("Chapter:"));
        top.add(chapterSelector);
        top.add(new JLabel("Quest Name:"));
        top.add(questName);
        top.add(new JLabel("Description:"));
        top.add(questDesc);

        JButton addMain = new JButton("Add Main");
        addMain.addActionListener(e -> {
            if (!validateInput()) {
                return;
            }
            GameAPI.addQuest((Integer) actSelector.getSelectedItem(),
                    (Integer) chapterSelector.getSelectedItem(),
                    questName.getText().trim(),
                    questDesc.getText().trim());
            refreshView();
        });
        top.add(addMain);

        JButton addSide = new JButton("Add Side");
        addSide.addActionListener(e -> {
            if (!validateInput()) {
                return;
            }
            GameAPI.addSideQuest((Integer) actSelector.getSelectedItem(),
                    (Integer) chapterSelector.getSelectedItem(),
                    questName.getText().trim(),
                    questDesc.getText().trim());
            refreshView();
        });
        top.add(addSide);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshView());
        top.add(refresh);

        return top;
    }

    private boolean validateInput() {
        return !questName.getText().trim().isEmpty() && !questDesc.getText().trim().isEmpty();
    }

    private void refreshView() {
        List<String> lines = new ArrayList<>();
        lines.add("Story Systems Editor");
        lines.addAll(GameAPI.getStorySummaryLines());
        lines.add("");
        StorySystem.Act act = GameAPI.getActs().get((Integer) actSelector.getSelectedItem() - 1);
        StorySystem.Chapter chapter = act.getChapter((Integer) chapterSelector.getSelectedItem());
        lines.add("Selected Act " + act.actNumber + " Chapter " + chapter.chapterNumber);
        lines.add("Main Quests:");
        if (chapter.quests.isEmpty()) {
            lines.add("- (none)");
        }
        else {
            for (StorySystem.Quest q : chapter.quests) {
                lines.add("- " + q.name + " :: " + q.description);
            }
        }
        lines.add("Side Quests:");
        if (chapter.sideQuests.isEmpty()) {
            lines.add("- (none)");
        }
        else {
            for (StorySystem.Quest q : chapter.sideQuests) {
                lines.add("- " + q.name + " :: " + q.description);
            }
        }
        output.setText(String.join(System.lineSeparator(), lines));
    }
}
