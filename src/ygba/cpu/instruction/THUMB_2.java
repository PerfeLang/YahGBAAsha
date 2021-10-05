package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_2 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = opcode & 0x0007;
        int rsIndex = (opcode >>> 3) & 0x0007;
        int rsValue = cpu.getRegister(rsIndex);
        int rdValue;
        int operand3 = (opcode >>> 6) & 0x0007;
        
        if ((opcode & 0x0400) == 0) { // operand3 is a register
            operand3 = cpu.getRegister(operand3);
        }
        
        if ((opcode & 0x0200) == 0) { // ADD Rd, Rs, (Rn / #nn)
            rdValue = rsValue + operand3;
            cpu.setVCFlagsForADD(rsValue, operand3, rdValue);
        } else { // SUB Rd, Rs, (Rn / #nn)
            rdValue = rsValue - operand3;
            cpu.setVCFlagsForSUB(rsValue, operand3, rdValue);
        }
        
        cpu.setRegister(rdIndex, rdValue);
        cpu.setZFlag(rdValue == 0);
        cpu.setNFlag(rdValue < 0);
    }
}