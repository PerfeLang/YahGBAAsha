package ygba.cpu.instruction;

import ygba.cpu.ARM7TDMI;
import ygba.memory.MemoryInterface;

public final class ARM_11 {
    
    public static void execute(ARM7TDMI cpu, MemoryInterface memory, int opcode) {
        if (!ARMState.isPreconditionSatisfied(cpu, opcode)) return;
        
        int rnIndex = (opcode >>> 16) & 0x0000000F;
        int rnValue = cpu.getRegister(rnIndex);
        
        int nbRegisters = 0;
        for (int i = 0; i <= 15; i++) {
            if ((opcode & (1 << i)) != 0) nbRegisters++;
        }
        
        int stackAddress = (rnValue & 0xFFFFFFFC);
        int finalAddress;
        if ((opcode & 0x00800000) != 0) { // Up
            finalAddress = stackAddress + (nbRegisters << 2);
            if ((opcode & 0x01000000) != 0) { // Increment Before
                stackAddress += 4;
            }
        } else { // Down
            finalAddress = stackAddress - (nbRegisters << 2);
            stackAddress = finalAddress;
            if ((opcode & 0x01000000) == 0) { // Decrement After
                stackAddress += 4;
            }
        }
        
        boolean isPCBitSet = ((opcode & 0x00008000) != 0);
        boolean isPSRBitSet = ((opcode & 0x00400000) != 0);
        
        if ((opcode & 0x00100000) == 0) { // STM
            int mode = (isPSRBitSet) ? cpu.USRMode : cpu.getMode();
            //if (mode == cpu.USRMode) System.out.println("Unsupported opcode (STM in User mode)");
            
            int i = 0;
            while (i < 15) {
                if ((opcode & (1 << i)) != 0) {
                    memory.storeWord(stackAddress, cpu.getRegister(i)); // TODO: USR mode
                    stackAddress += 4;
                    break;
                }
                i++;
            }
            
            if ((opcode & 0x00200000) != 0) { // Write-back
                cpu.setRegister(rnIndex, (rnValue & 0x00000003) | finalAddress);
            }
            
            for (i++; i < 15; i++) {
                if ((opcode & (1 << i)) != 0) {
                    memory.storeWord(stackAddress, cpu.getRegister(i)); // TODO: USR mode
                    stackAddress += 4;
                }
            }
            
            if (isPCBitSet) {
                memory.storeWord(stackAddress, cpu.getPC() + 4);
            }
            
        } else { // LDM
            int mode = (isPSRBitSet && !isPCBitSet) ? cpu.USRMode : cpu.getMode();
            //if (mode == cpu.USRMode) System.out.println("Unsupported opcode (LDM in User mode)");
            
            if ((opcode & 0x00200000) != 0) { // Write-back
                cpu.setRegister(rnIndex, (rnValue & 0x00000003) | finalAddress);
            }
            
            for (int i = 0; i < 15; i++) {
                if ((opcode & (1 << i)) != 0) {
                    cpu.setRegister(i, memory.loadWord(stackAddress)); // TODO: USR mode
                    stackAddress += 4;
                }
            }
            
            if (isPCBitSet) {
                cpu.setPC(memory.loadWord(stackAddress));
                if (isPSRBitSet) cpu.setCPSR(cpu.getSPSR());
                cpu.flushPipeline();
            }
        }
    }
}