package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_13 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int spValue = cpu.getSP();
        int offset = (opcode & 0x007F) << 2;
        
        if ((opcode & 0x0080) != 0) offset = -offset;
        // ADD SP, #nn
        cpu.setSP(spValue + offset);
    }
}