package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_4 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int offset = opcode & 0x00FFFFFF;
        if ((offset & 0x00800000) != 0) offset |= 0xFF000000;
        offset <<= 2;
        
        if ((opcode & 0x01000000) != 0) cpu.setLR(cpu.getCurrentPC());
        cpu.setPC(cpu.getPC() + offset);
        cpu.flushARMPipeline();
    }
}