package com.versionone.integration.idea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ValidationResultDialog extends JDialog {
    private final String validationResult;

    public ValidationResultDialog(String validationResult) {
        super((JFrame)null, "Validation failed", true);
        this.validationResult = validationResult;
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(455, 333);
        setLayout(new BorderLayout());

        createControls();
    }

    private void createControls() {
        JTextArea validationResultTextArea = new JTextArea(validationResult);
        JScrollPane valuesScrollPane = new JScrollPane(validationResultTextArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(valuesScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton("OK");
        buttonPanel.add(okButton);
        add(buttonPanel, BorderLayout.PAGE_END);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });

        centerDialog();
    }

    private void centerDialog() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height)/ 2);
    }
}
