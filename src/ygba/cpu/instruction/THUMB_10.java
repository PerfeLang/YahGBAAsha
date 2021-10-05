package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_10 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rdIndex = opcode & 0x0007;
        int rbIndex = (opcode >>> 3) & 0x0007;
        int rbValue = cpu.getRegister(rbIndex);
        int offset = (opcode >>> 5) & 0x003E;
        
        if ((opcode & 0x0800) == 0) { // STRH Rd, [Rb, #nn]
            memory.storeHalfWord(rbValue + offset, (short) cpu.getRegister(rdIndex));
        } else { // LDRH Rd, [Rb, #nn]
            cpu.setRegister(rdIndex, memory.loadHalfWord(rbValue + offset) & 0x0000FFFF);
        }
    }
}