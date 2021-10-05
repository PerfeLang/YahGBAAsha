package ygba.ui;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

import ygba.YGBA;
import ygba.gfx.GFXScreen;
import ygba.memory.Memory;

public final class YGBAApplet extends MIDlet {
    public Display display = Display.getDisplay (this);
    public YGBA ygba;
    public Memory memory;
    public GFXScreen gfxScreen;
    public FileBrowser biosFileChooser;

    public void startApp() {
        ygba = new YGBA(this);
        memory = ygba.getMemory();
        gfxScreen = new GFXScreen(ygba.getGraphics(), memory.getIORegMemory(), this);

        // Autoload BIOS file on startup
        memory.loadBIOS("/bios.bin");

        biosFileChooser = new FileBrowser(this);
        biosFileChooser.setCommandListener(biosFileChooser);
        display.setCurrent(biosFileChooser);
    }

    protected void destroyApp(boolean unconditional) {
        ygba.stop();
        notifyDestroyed();
    }

    protected void pauseApp(){}
}