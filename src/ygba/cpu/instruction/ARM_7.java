package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_7 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rmIndex = opcode & 0x0000000F;
        int rmValue = cpu.getRegister(rmIndex);
        int rsIndex = (opcode >>> 8) & 0x0000000F;
        int rsValue = cpu.getRegister(rsIndex);
        int rdIndex = (opcode >>> 16) & 0x0000000F;
        int rdValue;
        
        if ((opcode & 0x00200000) == 0) {
            rdValue = (rmValue * rsValue);
        } else {
            int rnIndex = (opcode >>> 12) & 0x0000000F;
            int rnValue = cpu.getRegister(rnIndex);
            rdValue = (rmValue * rsValue) + rnValue;
        }
        
        cpu.setRegister(rdIndex, rdValue);
        
        if ((opcode & 0x00100000) != 0) {
            cpu.setZFlag(rdValue == 0);
            cpu.setNFlag(rdValue < 0);
        }
    }
}