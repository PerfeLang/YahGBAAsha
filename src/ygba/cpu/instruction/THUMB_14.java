package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_14 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int spValue = cpu.getSP() & 0xFFFFFFFC;
        
        if ((opcode & 0x0800) == 0) { // PUSH {Rlist}
            
            if ((opcode & 0x0100) != 0) { // PUSH LR
                spValue -= 4;
                memory.storeWord(spValue, cpu.getLR());
            }
            
            for (int i = 7; i >= 0; i--) {
                if ((opcode & (1 << i)) != 0) {
                    spValue -= 4;
                    memory.storeWord(spValue, cpu.getRegister(i));
                }
            }
            
        } else { // POP {Rlist}
            
            for (int i = 0; i <= 7; i++) {
                if ((opcode & (1 << i)) != 0) {
                    cpu.setRegister(i, memory.loadWord(spValue));
                    spValue += 4;
                }
            }
            
            if ((opcode & 0x0100) != 0) { // POP PC
                cpu.setPC(memory.loadWord(spValue));
                spValue += 4;
                cpu.flushTHUMBPipeline();
            }
            
        }
        
        cpu.setSP(spValue);
    }
}