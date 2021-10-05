package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_3 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rnIndex = opcode & 0x0000000F;
        int rnValue = cpu.getRegister(rnIndex);
        
        cpu.setTFlag((rnValue & 0x00000001) != 0);
        cpu.setPC(rnValue);
        cpu.flushPipeline();
    }
}