package com.versionone.integration.idea.editors;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Solomin
 * Date: 06.05.2010
 * Time: 15:01:17
 * To change this template use File | Settings | File Templates.
 */
public class RichDialogEditor extends JDialog {


    public RichDialogEditor(JFrame parent, String title) {
        super(parent, title, true);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(500, 500);
        setLayout(new BorderLayout());

        createConmponets();
    }


    void createConmponets() {
        //JRootPane panel = createRootPane();

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton();
        okButton.setText("OK");
        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //panel.add(buttonPanel, BorderLayout.PAGE_END);
        add(buttonPanel, BorderLayout.PAGE_END);
    }
}
