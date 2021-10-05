package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_10 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rdIndex = (opcode >>> 12) & 0x0000000F;
        int rnIndex = (opcode >>> 16) & 0x0000000F;
        int rnValue = cpu.getRegister(rnIndex);
        
        int offset;
        if ((opcode & 0x00400000) == 0) {
            int rmIndex = opcode & 0x0000000F;
            int rmValue = cpu.getRegister(rmIndex);
            offset = rmValue;
        } else {
            offset = ((opcode & 0x00000F00) >>> 4) | (opcode & 0x0000000F);
        }
        
        boolean isPostIndexing = ((opcode & 0x01000000) == 0);
        boolean isDown = ((opcode & 0x00800000) == 0);
        boolean isWriteBack = isPostIndexing || ((opcode & 0x00200000) != 0);
        
        if (isDown) offset = -offset;
        if (!isPostIndexing) rnValue += offset;
        
        int instruction = (opcode & 0x00000060);
        
        if ((opcode & 0x00100000) == 0) { // Store to memory
            if (instruction == 0x00000020) { // STR{cond}H  Rd, <Address>
                int rdValue = cpu.getRegister(rdIndex);
                if (rdIndex == cpu.PC) rdValue += 4;
                memory.storeHalfWord(rnValue, (short) rdValue);
            }
        } else { // Load from memory
            switch (instruction) {
                case 0x00000020: // LDR{cond}H  Rd, <Address>
                    cpu.setRegister(rdIndex, memory.loadHalfWord(rnValue) & 0x0000FFFF);
                    break;
                case 0x00000040: // LDR{cond}SB Rd, <Address>
                    cpu.setRegister(rdIndex, memory.loadByte(rnValue));
                    break;
                case 0x00000060: // LDR{cond}SH Rd, <Address>
                    cpu.setRegister(rdIndex, memory.loadHalfWord(rnValue));
                    break;
                default:
                    return;
            }
            
            if (rdIndex == cpu.PC) cpu.flushARMPipeline();
            if (rdIndex == rnIndex) return;
        }
        
        if (isWriteBack) {
            if (isPostIndexing) rnValue += offset;
            cpu.setRegister(rnIndex, rnValue);
            if (rnIndex == cpu.PC) cpu.flushARMPipeline();
        }
    }
}