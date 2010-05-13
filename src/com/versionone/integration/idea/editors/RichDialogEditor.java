package com.versionone.integration.idea.editors;

import net.atlanticbb.tantlinger.shef.HTMLEditorPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.versionone.common.sdk.Workitem;
import com.versionone.integration.idea.MultiValueEditor;

/**
 * Created by IntelliJ IDEA.
 * User: Solomin
 * Date: 06.05.2010
 * Time: 15:01:17
 * To change this template use File | Settings | File Templates.
 */
public class RichDialogEditor extends JDialog {
    private Workitem item;
    private final JTable table;

    public RichDialogEditor(JFrame parent, JTable table, String title, Workitem item) {
        super(parent, title, true);

        this.item = item;
        this.table = table;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        //setModal(true);
        setSize(500, 500);
        setLayout(new BorderLayout());

        createConmponets();
        centerDialog();
    }

    private void centerDialog() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation((screenSize.width - getSize().width) / 2, (screenSize.height - getSize().height)/ 2);
    }
    /*
    void createConmponets() {
        //HTMLEditorPane editor = new HTMLEditorPane();
        //editor.setText("<b> testing </b>");
//NEW,OPEN,SAVE,SEPARATOR,CUT,COPY,PASTE,SEPARATOR,,," +
//",SEPARATOR,LEFT,CENTER,RIGHT,JUSTIFY,SEPARATOR,STYLESELECT

//"ULIST,OLIST,SEPARATOR,DEINDENT,INDENT,SEPARATOR,ANCHOR,SEPARATOR" +
//",IMAGE,SEPARATOR,CLEARFORMATS,SEPARATOR,VIEWSOURCE,SEPARATOR," +
//"STRIKE,SUPERSCRIPT,SUBSCRIPT,INSERTCHARACTER,SEPARATOR,FIND,COLOR,T" +
//"ABLE,SEPARATOR"
        //, "UNDO", "REDO"
        KafenioPanelConfigurationInterface config = new KafenioPanelConfiguration();
        config.setCustomToolBar1("BOLD, ITALIC, UNDERLINE, STRIKE, " +
                            "SEPARATOR, LEFT, CENTER, RIGHT, JUSTIFY," +
                            "SEPARATOR");
        config.setCustomToolBar2("CLEARFORMATS, SEPARATOR, ULIST, OLIST, " +
                                "SEPARATOR, DEINDENT, INDENT, SEPARATOR, UNDO, REDO");
        config.setShowMenuBar(true);
        config.setShowToolbar(true);
        config.setShowToolbar2(true);
        KafenioPanel editor = new KafenioPanel(config);
        editor.setDocumentText("<b> Test </b>");

        JPanel buttonPanel = new JPanel();
        JButton okButton = new JButton();
        okButton.setText("OK");
        JButton cancelButton = new JButton();
        cancelButton.setText("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        //getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        getContentPane().add(editor, BorderLayout.CENTER);
        ///add(panel);
    }*/

    void createConmponets() {
        final HTMLEditorPane editor = new HTMLEditorPane();
        String text = (String)item.getProperty(Workitem.DESCRIPTION_PROPERTY);
        editor.setText((text == null ? "" : text));

        //JRootPane panel = createRootPane();
        //JEditorPane editorPane = null;
        //editorPane = new JEditorPane();
        //HTMLEditorKit kit = new HTMLEditorKit();
        //JScrollPane scrollPane = new JScrollPane(editorPane);
        
        //editorPane.setEditorKit(kit);
        //editorPane.setText("<b>123</b>");
        //editorPane.setSize(200,200);

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


        //getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
        getContentPane().add(editor, BorderLayout.CENTER); 
        ///add(panel);
    }
}
