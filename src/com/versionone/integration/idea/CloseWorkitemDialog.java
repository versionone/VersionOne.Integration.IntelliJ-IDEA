package com.versionone.integration.idea;

import com.versionone.common.sdk.Workitem;

import javax.swing.*;

public class CloseWorkitemDialog extends JDialog {

    private final Workitem item;

    CloseWorkitemDialog(Workitem item, JFrame parent) {
        super(parent, "Close workitem");

        setDefaultCloseOperation(JDialog.EXIT_ON_CLOSE);
        this.item = item;
    }
}
