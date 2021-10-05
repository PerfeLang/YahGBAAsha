package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class THUMB_19 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        int offset = opcode & 0x07FF;
        
        if ((opcode & 0x0800) == 0) {
            // BLL label
            if ((opcode & 0x0400) != 0) offset |= 0xFFFFF800;
            cpu.setLR(cpu.getPC() + (offset << 12));
        } else {
            // BLH label
            int lrValue = cpu.getLR();
            int pcValue = cpu.getCurrentPC();
            cpu.setPC(lrValue + (offset << 1));
            cpu.setLR(pcValue | 0x00000001);
            cpu.flushTHUMBPipeline();
        }
    }
}