package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_17 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        cpu.generateUndefinedInstructionInterrupt(cpu.getCurrentPC());
    }
}