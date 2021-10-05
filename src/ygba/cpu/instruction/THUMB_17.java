package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_17 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        // SWI nn
        cpu.generateSoftwareInterrupt(cpu.getCurrentPC());
    }
}