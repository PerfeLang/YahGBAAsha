package ygba.gfx;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Image;
import ygba.memory.IORegMemory;
import ygba.ui.YGBAApplet;

public final class GFXScreen extends Canvas {
    
    private final GFX gfx;
    private final IORegMemory iorMem;
    private final YGBAApplet main;
    private Image input;
    
    public GFXScreen(GFX gfx, IORegMemory iorMem, YGBAApplet main) {
        this.gfx = gfx;
        this.iorMem = iorMem;
        this.main = main;
        
        try
        {
            input = Image.createImage("/input.png");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void paint(Graphics g) {
        g.drawImage(gfx.getImage(), 0, 0, Graphics.SOLID);
        g.drawImage(input, 0, 0, Graphics.SOLID);
    }
    
    protected void keyPressed(int keycode) {
        main.ygba.stop();
        main.memory.unloadROM();
        main.display.setCurrent (main.biosFileChooser);
    }
    
    protected void pointerPressed(int x, int y) {
        iorMem.pointerPressed(x, y);
    }

    protected void pointerReleased(int x, int y) {
        iorMem.pointerReleased(x, y);
    }

    public void clear() {
        gfx.reset();
    }
}