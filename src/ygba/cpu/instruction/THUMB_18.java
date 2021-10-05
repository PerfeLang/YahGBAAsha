package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_18 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        // B label
        int offset = (opcode & 0x03FF) << 1;
        if ((opcode & 0x0400) != 0) offset |= 0xFFFFF800;
        
        cpu.setPC(cpu.getPC() + offset);
        cpu.flushTHUMBPipeline();
    }
}