package com.versionone.integration.idea.editors;

import net.atlanticbb.tantlinger.shef.HTMLEditorPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.versionone.common.sdk.Workitem;

/**
 * Dialog with HTML editors
 */
public class RichDialogEditor extends JDialog {
    private Workitem item;
    private final JTable table;

    public RichDialogEditor(JFrame parent, JTable table, String title, Workitem item) {
        super(parent, title, true);

        this.item = item;
        this.table = table;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(500, 500);
        setMinimumSize(new Dimension(500, 500));
        setLayout(new BorderLayout());

        createConmponets();
        centerDialog();
    }

    private void centerDialog() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height)/ 2);
    }


    void createConmponets() {
        final HTMLEditorPane editor = new HTMLEditorPane();
        String text = (String)item.getProperty(Workitem.DESCRIPTION_PROPERTY);
        editor.setText((text == null ? "" : text));

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton();
        okButton.setText("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                item.setProperty(Workitem.DESCRIPTION_PROPERTY, editor.getText());
                setVisible(false);
                table.editingStopped(new ChangeEvent(RichDialogEditor.this));
            }
        });
        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        getContentPane().add(editor, BorderLayout.CENTER);
    }
}
