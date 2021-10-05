package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_15 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int rbIndex = (opcode >>> 8) & 0x0007;
        int rbValue = cpu.getRegister(rbIndex) & 0xFFFFFFFC;
        
        if ((opcode & 0x0800) == 0) { // STMIA Rb!, {Rlist}
            
            for (int i = 0; i <= 7; i++) {
                if ((opcode & (1 << i)) != 0) {
                    memory.storeWord(rbValue, cpu.getRegister(i));
                    rbValue += 4;
                }
            }
            
        } else { // LDMIA Rb!, {Rlist}
            
            for (int i = 0; i <= 7; i++) {
                if ((opcode & (1 << i)) != 0) {
                    cpu.setRegister(i, memory.loadWord(rbValue));
                    rbValue += 4;
                }
            }
            
        }
        
        cpu.setRegister(rbIndex, rbValue);
    }
}