package ygba.ui;

import java.util.Enumeration;
import javax.microedition.io.Connector;
import java.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

/**
 * The <code>FileBrowser</code> custom component lets the user list files and
 * directories. It's uses FileConnection Optional Package (JSR 75). The FileConnection
 * Optional Package APIs give J2ME devices access to file systems residing on mobile devices,
 * primarily access to removable storage media such as external memory cards.
 */
public class FileBrowser extends List implements CommandListener {
    private final String UP_DIRECTORY = "..."; // Special string denotes upper directory
    private final String MEGA_ROOT = "/"; // Special string that denotes upper directory accessible by this browser. this virtual directory contains all roots.
    private final char SEPERATOR = '/'; // Separator character as defined by FC specification
    private final String FILE_PROTOCOL = "file:///"; // Separator character as defined by FC specification
    private Image dirIcon;
    private Image fileIcon;
    private final YGBAApplet yGBAApplet;
    private String currDirName;
    private Command exitCommand = new Command ("Exit", Command.EXIT, 0);

    /**
     * Creates a new instance of FileBrowser for given <code>YGBAApplet</code> object.
     * @param yGBAApplet non null YGBAApplet object.
     */
    public FileBrowser(YGBAApplet yGBAApplet) {
        super("File Browser", List.IMPLICIT);
        this.yGBAApplet = yGBAApplet;
        
        currDirName = MEGA_ROOT;
        try {
            dirIcon = Image.createImage("/dir.png");
            fileIcon = Image.createImage("/file.png");
        }
        catch (IOException e) {
            e.printStackTrace ();
        }
        
        // Initialize fileBrowser
        addCommand(exitCommand);
        setCommandListener(this);

        showDir();
    }

    private void showDir() {
        // Show file list in the current directory
        Enumeration e;
        FileConnection currDir = null;

        try {
            deleteAll();
            if (MEGA_ROOT.equals(currDirName)) {
                e = FileSystemRegistry.listRoots();
            } else {
                currDir = (FileConnection) Connector.open(FILE_PROTOCOL + currDirName);
                e = currDir.list();
                append(UP_DIRECTORY, dirIcon);
            }

            while (e.hasMoreElements()) {
                String fileName = (String) e.nextElement();
                if (fileName.charAt(fileName.length() - 1) == SEPERATOR) {
                    // This is directory
                    append(fileName, dirIcon);
                } else {
                    // this is regular file
                    append(fileName, fileIcon);
                }
            }

            if (currDir != null) {
                currDir.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Indicates that a command event has occurred on Displayable displayable.
     * @param command a <code>Command</code> object identifying the command. This is either
     * one of the applications have been added to <code>Displayable</code> with <code>addCommand(Command)</code>
     * or is the implicit <code>SELECT_COMMAND</code> of List.
     * @param displayable the <code>Displayable</code> on which this event has occurred
     */
    public void commandAction(Command command, Displayable displayable) {
        if (command == List.SELECT_COMMAND) {
            List curr = (List) displayable;
            String currFile = curr.getString(curr.getSelectedIndex());

            if (currFile.endsWith(String.valueOf(SEPERATOR)) || currFile.equals(UP_DIRECTORY)) {
                openDir(currFile);
            }
            else {
                yGBAApplet.memory.loadROM(FILE_PROTOCOL + currDirName + currFile);
                yGBAApplet.ygba.reset();
                yGBAApplet.ygba.run();
                yGBAApplet.display.setCurrent(yGBAApplet.gfxScreen);
            }
        }
        else if (command == exitCommand) {
            yGBAApplet.destroyApp(true);
        }
    }

    private void openDir(String fileName) {
        // In case of directory just change the current directory and show it
        if (currDirName.equals(MEGA_ROOT)) {
            if (fileName.equals(UP_DIRECTORY)) {
                // can not go up from MEGA_ROOT
                return;
            }
            currDirName = fileName;
        } else if (fileName.equals(UP_DIRECTORY)) {
            // Go up one directory
            // TODO use setFileConnection when implemented
            int i = currDirName.lastIndexOf(SEPERATOR, currDirName.length() - 2);
            if (i != -1) {
                currDirName = currDirName.substring(0, i + 1);
            } else {
                currDirName = MEGA_ROOT;
            }
        } else {
            currDirName = currDirName + fileName;
        }
        showDir();
    }
}