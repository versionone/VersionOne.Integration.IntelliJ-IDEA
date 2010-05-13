package net.atlanticbb.tantlinger.shef;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;


import net.atlanticbb.tantlinger.ui.text.CompoundUndoManager;
import net.atlanticbb.tantlinger.ui.text.HTMLUtils;
import net.atlanticbb.tantlinger.ui.text.WysiwygHTMLEditorKit;
import net.atlanticbb.tantlinger.ui.text.actions.ClearStylesAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLEditorActionFactory;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLInlineAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTextEditAction;
import net.atlanticbb.tantlinger.ui.text.actions.IndentAction;

import net.atlanticbb.tantlinger.i18n.I18n;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLLineBreakAction;
import net.atlanticbb.tantlinger.ui.DefaultAction;
import net.atlanticbb.tantlinger.ui.text.Entities;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLElementPropertiesAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLFontAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLFontColorAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLHorizontalRuleAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLImageAction;
import net.atlanticbb.tantlinger.ui.text.actions.SpecialCharAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLLinkAction;
import net.atlanticbb.tantlinger.ui.text.actions.HTMLTableAction;
import net.atlanticbb.tantlinger.ui.text.actions.FindReplaceAction;
import net.atlanticbb.tantlinger.ui.UIUtils;

import org.bushe.swing.action.ActionList;
import org.bushe.swing.action.ActionManager;
import org.bushe.swing.action.ActionUIFactory;

/**
 *
 * @author Bob Tantlinger
 */
public class HTMLEditorPane extends JPanel 
{
	 /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private static final String INVALID_TAGS[] = {"html", "head", "body", "title"};

    private JEditorPane wysEditor;
    private JEditorPane focusedEditor;
    private JTabbedPane tabs;
    private JToolBar formatToolBar;

    private JPopupMenu wysPopupMenu;
    
    private ActionList actionList;
    
    private FocusListener focusHandler = new FocusHandler();
    private CaretListener caretHandler = new CaretHandler();
    private MouseListener popupHandler = new PopupHandler();
    
    
    public HTMLEditorPane()
    {
    	initUI();
    }
    
    public void setCaretPosition(int pos)
    {
        wysEditor.setCaretPosition(pos);
        wysEditor.requestFocusInWindow();
    }
    
    private void initUI()
    {
        createEditorTabs();
        createEditorActions();
        setLayout(new BorderLayout());
        add(formatToolBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);    
        
    }

    
    private void createEditorActions()
    {        
        actionList = new ActionList("editor-actions");
        ActionList editActions = HTMLEditorActionFactory.createEditActionList();

        //create editor popupmenus
        wysPopupMenu = ActionUIFactory.getInstance().createPopupMenu(editActions);
        createFormatToolBar();
    }
    
    private void createFormatToolBar()
    {
        formatToolBar = new JToolBar();
        formatToolBar.setFloatable(false);
        formatToolBar.setFocusable(false);
        
        Action act = new HTMLInlineAction(HTMLInlineAction.BOLD);
        act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
        actionList.add(act);
        addToToolBar(formatToolBar, act);
        
        act = new HTMLInlineAction(HTMLInlineAction.ITALIC);
        act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
        actionList.add(act);
        addToToolBar(formatToolBar, act);
        
        act = new HTMLInlineAction(HTMLInlineAction.UNDERLINE);
        act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
        actionList.add(act);
        addToToolBar(formatToolBar, act);

        act = new HTMLInlineAction(HTMLInlineAction.STRIKE);
        act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
        actionList.add(act);
        addToToolBar(formatToolBar, act);
        formatToolBar.addSeparator();
        
        List alst = HTMLEditorActionFactory.createListElementActionList();
        for(Iterator it = alst.iterator(); it.hasNext();)
        {
            act = (Action)it.next();
            act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
            actionList.add(act);
            addToToolBar(formatToolBar, act);
        }
        formatToolBar.addSeparator();
        
        alst = HTMLEditorActionFactory.createAlignActionList();
        for(Iterator it = alst.iterator(); it.hasNext();)
        {
            act = (Action)it.next();
            act.putValue(ActionManager.BUTTON_TYPE, ActionManager.BUTTON_TYPE_VALUE_TOGGLE);
            actionList.add(act);
            addToToolBar(formatToolBar, act);
        }
        formatToolBar.addSeparator();

        act = new IndentAction(IndentAction.INDENT);
        addToToolBar(formatToolBar, act);
        actionList.add(act);
        act = new IndentAction(IndentAction.OUTDENT);
        addToToolBar(formatToolBar, act);
        actionList.add(act);
        formatToolBar.addSeparator();

        addToToolBar(formatToolBar, CompoundUndoManager.UNDO);
        actionList.add(CompoundUndoManager.UNDO);
        addToToolBar(formatToolBar, CompoundUndoManager.REDO);
        actionList.add(CompoundUndoManager.REDO);

        formatToolBar.addSeparator();
        act = new ClearStylesAction();
        actionList.add(act);
        addToToolBar(formatToolBar, act);

        actionList.addActionListenerToAll(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                updateState();
            }
        });
    }
    
    private void addToToolBar(JToolBar toolbar, Action act)
    {
        AbstractButton button = ActionUIFactory.getInstance().createButton(act);
        configToolbarButton(button);
        toolbar.add(button);
    }
    
    /**
     * Converts an action list to an array. 
     * Any of the null "separators" or sub ActionLists are ommited from the array.
     * @param lst
     * @return
     */
    private Action[] toArray(ActionList lst)
    {
        List acts = new ArrayList();
        for(Iterator it = lst.iterator(); it.hasNext();)
        {
            Object v = it.next();
            if(v != null && v instanceof Action)
                acts.add(v);
        }
        
        return (Action[])acts.toArray(new Action[acts.size()]);
    }
        
    private void configToolbarButton(AbstractButton button)
    {
        button.setText(null);
        button.setMnemonic(0);
        button.setMargin(new Insets(1, 1, 1, 1));
        button.setMaximumSize(new Dimension(22, 22));
        button.setMinimumSize(new Dimension(22, 22));
        button.setPreferredSize(new Dimension(22, 22));
        button.setFocusable(false);
        button.setFocusPainted(false);
        //button.setBorder(plainBorder);
        Action a = button.getAction();
        if(a != null)
            button.setToolTipText(a.getValue(Action.NAME).toString());
    }
    
    private void createEditorTabs()
    {
        tabs = new JTabbedPane(SwingConstants.BOTTOM);
        wysEditor = createWysiwygEditor();
        
        tabs.addTab("Edit", new JScrollPane(wysEditor));
    }
    
    private JEditorPane createWysiwygEditor()
    {
        JEditorPane ed = new JEditorPane();
        ed.setEditorKitForContentType("text/html", new WysiwygHTMLEditorKit());
       
        ed.setContentType("text/html"); 
        
        insertHTML(ed, "<p></p>", 0);        
                
        ed.addCaretListener(caretHandler);
        ed.addFocusListener(focusHandler);
        ed.addMouseListener(popupHandler);
        
        
        HTMLDocument document = (HTMLDocument)ed.getDocument();
        CompoundUndoManager cuh = new CompoundUndoManager(document, new UndoManager());
        document.addUndoableEditListener(cuh);
                
        return ed;        
    }
    
    //  inserts html into the wysiwyg editor TODO remove JEditorPane parameter
    private void insertHTML(JEditorPane editor, String html, int location) 
    {       
        try 
        {
            HTMLEditorKit kit = (HTMLEditorKit) editor.getEditorKit();
            Document doc = editor.getDocument();
            StringReader reader = new StringReader(HTMLUtils.jEditorPaneizeHTML(html));
            kit.read(reader, doc, location);
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void setText(String text)
    {
    	String topText = removeInvalidTags(text);  
        
        if(tabs.getSelectedIndex() == 0)
        {           
                      
            wysEditor.setText("");
            insertHTML(wysEditor, topText, 0);            
            CompoundUndoManager.discardAllEdits(wysEditor.getDocument());
            
        }
    }
    
    public String getText()
    {
    	String topText = "";
    	if(tabs.getSelectedIndex() == 0)
        {           
           topText = removeInvalidTags(wysEditor.getText());          
            
        }
    	
    	return topText;
    }
    
    
    /* *******************************************************************
     *  Methods for dealing with HTML between wysiwyg and source editors 
     * ******************************************************************/
    private String removeInvalidTags(String html)
    {
        for(int i = 0; i < INVALID_TAGS.length; i++)
        {
            html = deleteOccurance(html, '<' + INVALID_TAGS[i] + '>');
            html = deleteOccurance(html, "</" + INVALID_TAGS[i] + '>');
        }
           
        return html.trim();
    }
    
    private String deleteOccurance(String text, String word)
    {
        //if(text == null)return "";
        StringBuffer sb = new StringBuffer(text);       
        int p;
        while((p = sb.toString().toLowerCase().indexOf(word.toLowerCase())) != -1)
        {           
            sb.delete(p, p + word.length());            
        }
        return sb.toString();
    }

    /* ************************************* */
    
    private void updateState()
    {
        actionList.putContextValueForAll(HTMLTextEditAction.EDITOR, focusedEditor);
        actionList.updateEnabledForAll();
    }
    
    
    
    
    
    
    private class CaretHandler implements CaretListener
    {
        /* (non-Javadoc)
         * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
         */
        public void caretUpdate(CaretEvent e)
        {            
            updateState();
        }        
    }
    
    private class PopupHandler extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        { checkForPopupTrigger(e); }
        
        public void mouseReleased(MouseEvent e)
        { checkForPopupTrigger(e); }
        
        private void checkForPopupTrigger(MouseEvent e)
        {
            if(e.isPopupTrigger())
            {                    
                JPopupMenu p = null;
                if(e.getSource() == wysEditor)
                    p =  wysPopupMenu;
                else
                    return;
                p.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }
    
    private class FocusHandler implements FocusListener
    {
        public void focusGained(FocusEvent e)
        {
            if(e.getComponent() instanceof JEditorPane)
            {
                JEditorPane ed = (JEditorPane)e.getComponent();
                CompoundUndoManager.updateUndo(ed.getDocument());
                focusedEditor = ed;
                
                updateState();
               // updateEnabledStates();
            }
        }
        
        public void focusLost(FocusEvent e)
        {
            
            if(e.getComponent() instanceof JEditorPane)
            {
                //focusedEditor = null;
                //wysiwygUpdated();
            }
        }
    }

}
