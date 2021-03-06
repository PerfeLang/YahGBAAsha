package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_6 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        // LDR Rd, [PC, #nn]
        int rdIndex = (opcode >>> 8) & 0x0007;
        int offset = (cpu.getPC() & 0xFFFFFFFC) + ((opcode & 0x00FF) << 2);
        cpu.setRegister(rdIndex, memory.loadWord(offset));
    }
}