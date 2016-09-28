/**
 * @name        Simple Java NotePad
 * @package ph.notepad
 * @file UI.java
 *
 * @author Pierre-Henry Soria
 * @email pierrehenrysoria@gmail.com
 * @link        http://github.com/pH-7
 *
 * @copyright   Copyright Pierre-Henry SORIA, All Rights Reserved.
 * @license     Apache (http://www.apache.org/licenses/LICENSE-2.0)
 * @create      2012-05-04
 * @update      2016-24-3
 *
 * @modifiedby  Achintha Gunasekara
 * @modemail    contact@achinthagunasekara.com
 *
 * @modifiedby  Marcus Redgrave-Close
 * @modemail   	marcusrc1@hotmail.co.uk
 */
package simplejavatexteditor;

import elastic.pl.interpreter.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Scanner;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DefaultEditorKit;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

public class UI extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;
    private final Container container;
    private final RSyntaxTextArea textArea;
    private final JMenuBar menuBar;
    private final JMenu menuFile, menuEdit, menuFind;
    private final JMenuItem newFile, openFile, saveFile, close, cut, copy, paste, clearFile, selectAll, quickFind;
    private final JToolBar mainToolbar;
    JButton newButton, openButton, saveButton, clearButton, quickButton, execButton,
            spaceButton1, spaceButton2;
    private final Action selectAllAction;
    private JTextArea errorText;
    // setup icons - File Menu
    private final ImageIcon newIcon = new ImageIcon("icons/new.png");
    private final ImageIcon openIcon = new ImageIcon("icons/open.png");
    private final ImageIcon saveIcon = new ImageIcon("icons/save.png");
    private final ImageIcon closeIcon = new ImageIcon("icons/run.png");

    // setup icons - Edit Menu
    private final ImageIcon clearIcon = new ImageIcon("icons/clear.png");
    private final ImageIcon cutIcon = new ImageIcon("icons/cut.png");
    private final ImageIcon copyIcon = new ImageIcon("icons/copy.png");
    private final ImageIcon pasteIcon = new ImageIcon("icons/paste.png");
    private final ImageIcon selectAllIcon = new ImageIcon("icons/selectall.png");

    // setup icons - Search Menu
    private final ImageIcon searchIcon = new ImageIcon("icons/search.png");
    private DefaultTableModel model;

    AutoComplete autocomplete;
    private boolean hasListener = false;
    private JTable table;

    public UI() {

        try {
            // Set the Look and Feel of the application to the operating
            // system's look and feel.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        } catch (UnsupportedLookAndFeelException e) {
        }

        container = getContentPane();

        // Set the initial size of the window
        setSize(900, 800);

        // Set the title of the window
        setTitle("Untitled | " + SimpleJavaTextEditor.NAME);

        // Set the default close operation (exit when it gets closed)
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Set a default font for the TextArea
        textArea = new RSyntaxTextArea("", 0, 0);
        textArea.setFont(new Font("Century Gothic", Font.BOLD, 12));
        textArea.setTabSize(2);
        textArea.setFont(new Font("Century Gothic", Font.BOLD, 12));
        textArea.setTabSize(2);
        textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        textArea.setCodeFoldingEnabled(true);
        textArea.setShowMatchedBracketPopup(true);

        errorText = new JTextArea();
        errorText.setBackground(Color.BLACK);
        errorText.setForeground(Color.WHITE);
        // This is why we didn't have to worry about the size of the TextArea!
        getContentPane().setLayout(new BorderLayout()); // the BorderLayout bit makes it fill it automatically

        RTextScrollPane sp = new RTextScrollPane(textArea);
        model = new DefaultTableModel();
        table = new JTable(model);
        model.addColumn("Memslot");
        model.addColumn("Value");

        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(table), sp);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(250);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(250, 50);
        table.setMinimumSize(minimumSize);
        errorText.setWrapStyleWord(true);
        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                splitPane, new JScrollPane(errorText));
        splitPane2.setResizeWeight(0.6);
        splitPane2.setDividerLocation(450);
        errorText.setEditable(false);
        errorText.setText("Welcome to Elastic PL Editor 1.0\nHere, you will see compiler error and warnings.");
        getContentPane().add(splitPane2);

        // Set the Menus
        menuFile = new JMenu("File");
        menuEdit = new JMenu("Edit");
        menuFind = new JMenu("Search");

        // Set the Items Menu
        newFile = new JMenuItem("New", newIcon);
        openFile = new JMenuItem("Open", openIcon);
        saveFile = new JMenuItem("Save", saveIcon);
        close = new JMenuItem("Quit", closeIcon);
        clearFile = new JMenuItem("Clear", clearIcon);
        quickFind = new JMenuItem("Quick", searchIcon);

        // Set the Menu Bar into the our GUI
        menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuFind);
        this.setJMenuBar(menuBar);

        // Set Actions:
        selectAllAction = new SelectAllAction("Select All", clearIcon, "Select all text", new Integer(KeyEvent.VK_A),
                textArea);

        this.setJMenuBar(menuBar);

        // New File
        newFile.addActionListener(this);  // Adding an action listener (so we know when it's been clicked).
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK)); // Set a keyboard shortcut
        menuFile.add(newFile); // Adding the file menu

        // Open File
        openFile.addActionListener(this);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        menuFile.add(openFile);

        // Save File
        saveFile.addActionListener(this);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        menuFile.add(saveFile);

        // Close File
        /*
		 * Along with our "CTRL+F4" shortcut to close the window, we also have
		 * the default closer, as stated at the beginning of this tutorial. this
		 * means that we actually have TWO shortcuts to close: 
		 * 1) the default close operation (example, Alt+F4 on Windows)
		 * 2) CTRL+F4, which we are
		 * about to define now: (this one will appear in the label).
         */
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        close.addActionListener(this);
        menuFile.add(close);

        // Select All Text
        selectAll = new JMenuItem(selectAllAction);
        selectAll.setText("Select All");
        selectAll.setIcon(selectAllIcon);
        selectAll.setToolTipText("Select All");
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        menuEdit.add(selectAll);

        // Clear File (Code)
        clearFile.addActionListener(this);
        clearFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
        menuEdit.add(clearFile);

        // Cut Text
        cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setIcon(cutIcon);
        cut.setToolTipText("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuEdit.add(cut);

        // Copy Text
        copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setIcon(copyIcon);
        copy.setToolTipText("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuEdit.add(copy);

        // Paste Text
        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setIcon(pasteIcon);
        paste.setToolTipText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        menuEdit.add(paste);

        // Find Word
        quickFind.addActionListener(this);
        quickFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        menuFind.add(quickFind);

        mainToolbar = new JToolBar();
        this.add(mainToolbar, BorderLayout.NORTH);
        // used to create space between button groups
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 50);

        newButton = new JButton(newIcon);
        newButton.setToolTipText("New");
        newButton.addActionListener(this);
        mainToolbar.add(newButton);
        mainToolbar.addSeparator();

        openButton = new JButton(openIcon);
        openButton.setToolTipText("Open");
        openButton.addActionListener(this);
        mainToolbar.add(openButton);
        mainToolbar.addSeparator();

        saveButton = new JButton(saveIcon);
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(this);
        mainToolbar.add(saveButton);
        mainToolbar.addSeparator();

        clearButton = new JButton(clearIcon);
        clearButton.setToolTipText("Clear All");
        clearButton.addActionListener(this);
        mainToolbar.add(clearButton);
        mainToolbar.addSeparator();

        quickButton = new JButton(searchIcon);
        quickButton.setToolTipText("Quick Search");
        quickButton.addActionListener(this);
        mainToolbar.add(quickButton);

        // create space between button groups
        spaceButton1 = new JButton();
        spaceButton1.setBorder(emptyBorder);
        mainToolbar.add(spaceButton1);

        // create space between button groups
        spaceButton2 = new JButton();
        spaceButton2.setBorder(emptyBorder);
        mainToolbar.add(spaceButton2);

        execButton = new JButton(closeIcon);
        execButton.setToolTipText("Execute Program");
        execButton.addActionListener(this);
        mainToolbar.add(execButton);
    }

    // Make the TextArea available to the autocomplete handler
    protected JTextArea getEditor() {
        return textArea;
    }

    public void executeCode() {
        while (model.getRowCount() > 0) {
            model.removeRow(0);
        }
        InputStream is = new ByteArrayInputStream(getEditor().getText().getBytes());
        ElasticPLParser parser = new ElasticPLParser(is);
        errorText.setText("");
        try {
            parser.CompilationUnit();
            ((ASTCompilationUnit) parser.rootNode()).reset();
            ((ASTCompilationUnit) parser.rootNode()).fillRandomIntNumber();

            boolean tooMuchStackUsage = RuntimeEstimator.exceedsStackUsage((SimpleNode) parser.rootNode());

            errorText.append("[!] Stack usage exceeded: " + tooMuchStackUsage + "\n");
            if (tooMuchStackUsage) {
                errorText.append("[!] aborted execution" + "\n");
            } else {
                errorText.append("[!] AST depth: " + ((ASTCompilationUnit) parser.rootNode()).getDepth() + "\n");
                long WCET = RuntimeEstimator.worstWeight((SimpleNode) parser.rootNode());
                errorText.append("[!] Worst case execution time: " + WCET + "\n");
                parser.rootNode().interpret();
            }

        } catch (ParseException e) {
            errorText.append("Elastic Programming Language Interpreter Version 0.1:  Encountered errors during parse: " + e.getMessage() + "\n");
            return;
        } catch (Exception e1) {
            errorText.append("Elastic Programming Language Interpreter Version 0.1:  Encountered errors during interpretation/tree building." + "\n");
            return;
        }

        LinkedHashMap<String, Integer> MP = ((ASTCompilationUnit) parser.rootNode()).getNotNullDumpState();

        Iterator<String> keySetIterator = MP.keySet().iterator();

        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            model.addRow(new Object[]{key, MP.get(key)});;
        }
        errorText.append("[!] Exit Stack Pointer: " + MyNode.top + "\n");
        boolean bountyFound = ((ASTCompilationUnit) parser.rootNode()).verifyBounty();
        errorText.append("[!] Bounty requirement met: " + bountyFound + "\n");

    }

    public void actionPerformed(ActionEvent e) {
        // If the source of the event was our "close" option
        if (e.getSource() == close || e.getSource() == execButton) {
            this.executeCode();
        } // If the source was the "new" file option
        else if (e.getSource() == newFile || e.getSource() == newButton) {
            FEdit.clear(textArea);
        } // If the source was the "open" option
        else if (e.getSource() == openFile || e.getSource() == openButton) {
            JFileChooser open = new JFileChooser(); // open up a file chooser (a dialog for the user to  browse files to open)
            int option = open.showOpenDialog(this); // get the option that the user selected (approve or cancel)

            /*
			 * NOTE: because we are OPENing a file, we call showOpenDialog~ if
			 * the user clicked OK, we have "APPROVE_OPTION" so we want to open
			 * the file
             */
            if (option == JFileChooser.APPROVE_OPTION) {
                FEdit.clear(textArea); // clear the TextArea before applying the file contents
                try {
                    // create a scanner to read the file (getSelectedFile().getPath() will get the path to the file)
                    Scanner scan = new Scanner(new FileReader(open.getSelectedFile().getPath()));
                    while (scan.hasNext()) // while there's still something to
                    // read
                    {
                        textArea.append(scan.nextLine() + "\n"); // append the line to the TextArea
                    }
                } catch (Exception ex) { // catch any exceptions, and...
                    // ...write to the debug console
                    System.out.println(ex.getMessage());
                }
            }
        } // If the source of the event was the "save" option
        else if (e.getSource() == saveFile || e.getSource() == saveButton) {
            // Open a file chooser
            JFileChooser fileChoose = new JFileChooser();
            // Open the file, only this time we call
            int option = fileChoose.showSaveDialog(this);

            /*
			 * ShowSaveDialog instead of showOpenDialog if the user clicked OK
			 * (and not cancel)
             */
            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = fileChoose.getSelectedFile();
                    // Set the new title of the window
                    setTitle(file.getName() + " | " + SimpleJavaTextEditor.NAME);
                    // Create a buffered writer to write to a file
                    BufferedWriter out = new BufferedWriter(new FileWriter(file.getPath()));
                    // Write the contents of the TextArea to the file
                    out.write(textArea.getText());
                    // Close the file stream
                    out.close();

                    //If the user saves files with supported
                    //file types more than once, we need to remove
                    //previous listeners to avoid bugs.
                    if (hasListener) {
                        textArea.getDocument().removeDocumentListener(autocomplete);
                        hasListener = false;
                    }

                    //With the keywords located in a separate class,
                    //we can support multiple languages and not have to do
                    //much to add new ones.
                    SupportedKeywords kw = new SupportedKeywords();
                    ArrayList<String> arrayList;
                    String[] list = {".java", ".cpp"};

                    //Iterate through the list, find the supported
                    //file extension, apply the appropriate getter method from
                    //the keyword class
                    for (int i = 0; i < list.length; i++) {
                        if (file.getName().endsWith(list[i])) {
                            switch (i) {
                                case 0:
                                    String[] jk = kw.getJavaKeywords();
                                    arrayList = kw.setKeywords(jk);
                                    autocomplete = new AutoComplete(this, arrayList);
                                    textArea.getDocument().addDocumentListener(autocomplete);
                                    hasListener = true;
                                    break;
                                case 1:
                                    String[] ck = kw.getCppKeywords();
                                    arrayList = kw.setKeywords(ck);
                                    autocomplete = new AutoComplete(this, arrayList);
                                    textArea.getDocument().addDocumentListener(autocomplete);
                                    hasListener = true;
                                    break;
                            }
                        }
                    }
                } catch (Exception ex) { // again, catch any exceptions and...
                    // ...write to the debug console
                    System.out.println(ex.getMessage());
                }
            }
        }

        // Clear File (Code)
        if (e.getSource() == clearFile || e.getSource() == clearButton) {
            FEdit.clear(textArea);
        }
        // Find
        if (e.getSource() == quickFind || e.getSource() == quickButton) {
            new Find(textArea);
        }

    }

    class SelectAllAction extends AbstractAction {

        /**
         * Used for Select All function
         */
        private static final long serialVersionUID = 1L;

        public SelectAllAction(String text, ImageIcon icon, String desc, Integer mnemonic, final JTextArea textArea) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e) {
            textArea.selectAll();
        }
    }

}
