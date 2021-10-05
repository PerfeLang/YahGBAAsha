package ygba.memory;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import ygba.dma.DirectMemoryAccess;
import ygba.gfx.GFX;
import ygba.time.Time;

public final class Memory
        implements MemoryInterface {
    
    private final int
            MemoryBankMask = 0x0F000000,
            MemoryAddressMask = 0x00FFFFFF;
    
    private MemoryInterface[] bank;
    
    private SystemMemory sysMem;
    private GamePakMemory gp1Mem, gp2Mem;
    
    
    public Memory() {
        bank = new MemoryInterface[0x10];
        
        sysMem = new SystemMemory();
        gp1Mem = new GamePakMemory(1);
        gp2Mem = new GamePakMemory(2);
        
        bank[0x00] = sysMem;
        bank[0x01] = new UnusedMemory();
        bank[0x02] = new EWorkMemory();
        bank[0x03] = new IWorkMemory();
        bank[0x04] = new IORegMemory();
        bank[0x05] = new PaletteMemory();
        bank[0x06] = new VideoMemory();
        bank[0x07] = new ObjectMemory();
        bank[0x08] = bank[0x0A] = bank[0x0C] = gp1Mem;
        bank[0x09] = bank[0x0B] = bank[0x0D] = gp2Mem;
        bank[0x0E] = bank[0x0F] = new SaveMemory();
    }
    
    public void connectToDMA(DirectMemoryAccess dma) {
        getIORegMemory().connectToDMA(dma);
    }
    
    public void connectToGraphics(GFX gfx) {
        getIORegMemory().connectToGraphics(gfx);
    }
    
    public void connectToTime(Time time) {
        getIORegMemory().connectToTime(time);
    }
    
    
    public MemoryInterface getBank(int bankNumber) {
        return bank[bankNumber & 0x0F];
    }
    
    public IORegMemory getIORegMemory() {
        return (IORegMemory) getBank(0x04);
    }
    
    public int getInternalOffset(int bankNumber, int offset) {
        return ((MemoryManager) getBank(bankNumber)).getInternalOffset(offset);
    }
    
    public int getSize(int bankNumber) {
        return ((MemoryManager) getBank(bankNumber)).getSize();
    }
    
    public byte getByte(int offset) {
        return bank[(offset & MemoryBankMask) >>> 24].getByte(offset);
    }
    
    public short getHalfWord(int offset) {
        return bank[(offset & MemoryBankMask) >>> 24].getHalfWord(offset);
    }
    
    public int getWord(int offset) {
        return bank[(offset & MemoryBankMask) >>> 24].getWord(offset);
    }
    
    
    public void setByte(int offset, byte value) {
        bank[(offset & MemoryBankMask) >>> 24].setByte(offset, value);
    }
    
    public void setHalfWord(int offset, short value) {
        bank[(offset & MemoryBankMask) >>> 24].setHalfWord(offset, value);
    }
    
    public void setWord(int offset, int value) {
        bank[(offset & MemoryBankMask) >>> 24].setWord(offset, value);
    }
    
    
    public byte loadByte(int offset) {
        return bank[(offset & MemoryBankMask) >>> 24].loadByte(offset);
    }
    
    public short loadHalfWord(int offset) {
        return bank[(offset & MemoryBankMask) >>> 24].loadHalfWord(offset);
    }
    
    public int loadWord(int offset) {
        return bank[(offset & MemoryBankMask) >>> 24].loadWord(offset);
    }
    
    
    public void storeByte(int offset, byte value) {
        bank[(offset & MemoryBankMask) >>> 24].storeByte(offset, value);
    }
    
    public void storeHalfWord(int offset, short value) {
        bank[(offset & MemoryBankMask) >>> 24].storeHalfWord(offset, value);
    }
    
    public void storeWord(int offset, int value) {
        bank[(offset & MemoryBankMask) >>> 24].storeWord(offset, value);
    }
    
    
    public void softReset() {
        for (int i = 0; i < bank.length; i++) {
            ((MemoryManager) bank[i]).softReset();
        }
    }
    
    public void hardReset() {
        for (int i = 0; i < bank.length; i++) {
            ((MemoryManager) bank[i]).hardReset();
        }
    }
    
    public void reset() {
        softReset();
    }
    
    private void readStream(InputStream stream, byte[] buffer) throws IOException {
        int bytesRead = 0;
        int pos = 0;
        int size = buffer.length;
        do {
            pos += bytesRead;
            size -= bytesRead;
            bytesRead = stream.read(buffer, pos, size);
        } while ((bytesRead != -1) && (size > 0));
    }
    
    public void loadBIOS(String biosFileURL) {
        try {
            InputStream biosStream = Memory.class.getResourceAsStream(biosFileURL);
            
            int biosFileSize = biosStream.available();
            if (biosFileSize != (16 * 1024)) { // GBA BIOS size = 16KB
                throw new IOException("Wrong BIOS size");
            }
            
            readStream(biosStream, sysMem.getSpace());
            biosStream.close();
        } catch (IOException e) {
            System.out.println("Failed loading BIOS file: " + e.getMessage());
        }
    }
    
    public void loadROM(String romFileURL) {
        try {
            FileConnection fileConnection = (FileConnection) Connector.open(romFileURL);
            InputStream romStream = fileConnection.openInputStream();

            int romFileSize = (int) fileConnection.fileSize();
            int rom1Size, rom2Size;
            if (romFileSize <= 0x01000000) {
                rom1Size =  romFileSize;
                rom2Size = 0;
            } else if (romFileSize <= 0x02000000) {
                rom1Size = 0x01000000;
                rom2Size = romFileSize - rom1Size;
            } else {
                throw new IOException("Invalid ROM size");
            }
            
            byte[] rom1 = gp1Mem.createSpace(rom1Size);
            byte[] rom2 = gp2Mem.createSpace(rom2Size);
            readStream(romStream, rom1);
            readStream(romStream, rom2);
            romStream.close();
        } catch (IOException e) {
            System.out.println("Failed loading ROM file: " + e.getMessage());
            unloadROM();
        }
    }
    
    public void unloadROM() {
        gp1Mem.createSpace(0x0);
        gp2Mem.createSpace(0x0);
    }
}