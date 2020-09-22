import java.io.*;

public class MIPSSimulator {
   private static String[] registerNames = {"$zero", "$at", "$v0", "$v1", "$a0", "$a1", "$a2", "$a3",
                                     "$t0", "$t1", "$t2", "$t3", "$t4", "$t5", "$t6", "$t7",
                                     "$s0", "$s1", "$s2", "$s3", "$s4", "$s5", "$s6", "$s7",
                                     "$t8", "$t9", "$k0", "$k1", "$gp", "$sp", "$fp", "$ra"};

   ////////////////////////////////////////////////////////////////////////////////////
   // HELPER FUNCTIONS, DO NOT MODIFY
   public static String getBits(String INST, int start, int end) {
      return INST.substring(31-start, 31-end+1);
   }

   public static int bin2Dec(String bits, boolean twos) {
      int answer = 0;
      int p = bits.length()-1;
      for (int i = 0; i < bits.length(); i++) {
         if (bits.charAt(i) == '1') {
            answer += (int) Math.pow(2, p);
            if (twos && i == 0)
               answer *= -1;
         }
         p--;
      }
      return answer;
   }

   public static String test(int[] storage, int index, int value) {
      if (index > storage.length)
         return "FAIL.  INVALID REGISTER OR ADDRESS";
      else if (storage[index] == value)
         return "PASS";
      else
         return "FAIL. VALUE IS: "+storage[index];
   }

   public static int toUnsigned(String bits) {
      return bin2Dec(bits, false);
   }

   public static int toSigned(String bits) {
      return bin2Dec(bits, true);
   }
   ////////////////////////////////////////////////////////////////////////////////////
   public static int getRegNum(String reg) {
      int pos = 0;
      while (pos < 32 && !registerNames[pos].equals(reg))
         pos++;
      if (pos == 32) System.out.println("WARNING: UNKNOWN REGISTER "+reg);
      return pos;
   }

   public static String printInstruction(String INST) {
      String opcode = getBits(INST, 31, 26);

      if (opcode.equals("100011")) { // LW
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            return "lw "+registerNames[t]+", "+offset+"("+registerNames[s]+")"; 
      }

         else if (opcode.equals("101011")) { // SW
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            return "sw "+registerNames[t]+", "+offset+"("+registerNames[s]+")"; 
         }

         else if (opcode.equals("000000")) { // RTYPE
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int d = toUnsigned(getBits(INST, 15, 11));
            String functcode = getBits(INST, 5, 0);
            if (functcode.equals("100100")) // AND
               return "and "+registerNames[d]+", "+registerNames[s]+", "+registerNames[t]; 
            else if (functcode.equals("100101")) // OR
               return "or "+registerNames[d]+", "+registerNames[s]+", "+registerNames[t]; 
            else if (functcode.equals("100000")) // ADD
               return "add "+registerNames[d]+", "+registerNames[s]+", "+registerNames[t]; 
            else if (functcode.equals("100010")) // SUB
               return "sub "+registerNames[d]+", "+registerNames[s]+", "+registerNames[t]; 
            else if (functcode.equals("101010")) // SLT
               return "slt "+registerNames[d]+", "+registerNames[s]+", "+registerNames[t];
            else if (functcode.equals("000000")) // SLL
               return "sll "+registerNames[d]+", "+registerNames[t]+", "+toUnsigned(getBits(INST, 10, 6));
            else if (functcode.equals("011000")) // MULT
               return "mult "+registerNames[s]+", "+registerNames[t];
            else if (functcode.equals("010000")) // MFLO
               return "mflo "+registerNames[d];
         }

         else if (opcode.equals("001000")) { // ADDI
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            return "addi "+registerNames[t]+", "+registerNames[s]+", "+offset; 
         }
         else if (opcode.equals("000100")) { // BEQ
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            return "beq "+registerNames[s]+", "+registerNames[t]+", "+offset; 
         }
         else if (opcode.equals("000101")) { // BNE
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            return "bne "+registerNames[s]+", "+registerNames[t]+", "+offset; 
         }

         else if (opcode.equals("000010")) { // J
            int target = toUnsigned(getBits(INST, 25, 0));
            return "j "+target;
         }
         return "UNKNOWN OPCODE";
   }

   public static void main(String[] args) {

      ////////////////////////////////////////////////////////////////////////////////
      // INITIALIZE RAM, REGISTERS AND INSTRUCTIONS.  DO NOT MODIFY
      int[] RAM = new int[2048];  // 8K of RAM
      int[] REG = new int[32];
      int LO = 0;
      REG[0] = 0;

      ////////////////////////////////////////////////////////////////////////////////
      //
      // INITIAL STATE OF PROJECT, FALL 2020
      RAM[0] = 55; // Will assume array starts at 0
      RAM[4] = 88;
      RAM[8] = 0;
      RAM[12] = 22;
      RAM[16] = 77;
      RAM[20] = 44;
      RAM[24] = 99;
      RAM[28] = 33;
      RAM[32] = 110;
      RAM[36] = 66;
      RAM[40] = 121;
      RAM[44] = 11;

      REG[16] = 0;  // $s0, address of start of array
      ///////////////////////////////////////////////////////////////////////////////

      String[] INSTRUCTIONS = new String[1000];

     
      int count = 0;
      try (BufferedReader br = new BufferedReader(new FileReader("memfile.dat")))
      {
         String line = br.readLine();
         while (line != null) {
            INSTRUCTIONS[count] = line;
            line = br.readLine();
            count++;
         }
      }
      catch(IOException e) {
         System.out.println("ERROR INVALID INPUT FILE");
         System.exit(1);
      }
      ///////////////////////////////////////////////////////////////////////////////
   
      System.out.println("****************************************************************");
      int pc = 0;
      while (pc < count) {
         String INST = INSTRUCTIONS[pc];
         System.out.println("CURRENT INSTRUCTION: "+printInstruction(INST));
         String opcode = getBits(INST, 31, 26);
   
         if (opcode.equals("100011")) { // LW
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            REG[t] = RAM[REG[s]+offset];
         }     

         else if (opcode.equals("101011")) { // SW
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            RAM[REG[s]+offset] = REG[t];
         }

         else if (opcode.equals("000000")) { // RTYPE
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int d = toUnsigned(getBits(INST, 15, 11));
            int h = toUnsigned(getBits(INST, 10, 6));
            String functcode = getBits(INST, 5, 0);
            if (functcode.equals("100100")) // AND
               REG[d] = REG[s] & REG[t];
            else if (functcode.equals("100101")) // OR
               REG[d] = REG[s] | REG[t];
            else if (functcode.equals("100000")) // ADD
               REG[d] = REG[s] + REG[t];
            else if (functcode.equals("100010")) // SUB
               REG[d] = REG[s] - REG[t];
            else if (functcode.equals("101010"))  // SLT
               if (REG[s] < REG[t])
                  REG[d] = 1;
               else
                  REG[d] = 0;
            else if (functcode.equals("000000")) // SLL
               REG[d] = REG[t] * (int) Math.pow(2, h);
            else if (functcode.equals("011000")) // MULT
               LO = REG[s] * REG[t];
            else if (functcode.equals("010000")) // MFLO
               REG[d] = LO;
         }

         else if (opcode.equals("001000")) { // ADDI
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            REG[t] = REG[s]+offset;
         }

         else if (opcode.equals("000100")) { // BEQ
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            if (REG[s] == REG[t])
               pc += offset;
         }
         else if (opcode.equals("000101")) { // BNE
            int s = toUnsigned(getBits(INST, 25, 21));
            int t = toUnsigned(getBits(INST, 20, 16));
            int offset = toSigned(getBits(INST, 15, 0));
            if (REG[s] != REG[t])
               pc += offset;
         }

         if (opcode.equals("000010")) { // J
            int target = toUnsigned(getBits(INST, 25, 0));
            pc = target;
         }
         else
            pc++;
      }
      System.out.println("****************************************************************");


      /////////////////////////////////////////////////////////
      // TESTS
      
      /*System.out.println("Test if $s3=12: "+test(REG, getRegNum("$s3"), 12));
      System.out.println("Test if $s4=7: "+test(REG, getRegNum("$s4"), 7));
      System.out.println("Test if $s5=11: "+test(REG, getRegNum("$s5"), 11));
      System.out.println("Test if $t4=1: "+test(REG, getRegNum("$t4"), 1));
      System.out.println("Test if $s7=7: "+test(REG, getRegNum("$s7"), 7));
      System.out.println("Test if RAM[80] = 7: "+test(RAM, 80, 7));
      System.out.println("Test if $s2=7: "+test(REG, getRegNum("$s2"), 7));
      System.out.println("Test if RAM[84] = 7: "+test(RAM, 84, 7));*/
      System.out.print("Sorted array: ");
      for (int i = 0; i < REG[17]*4; i += 4)
         System.out.print(RAM[i]+" ");
      System.out.println();

      // OTHERS, IF YOU WANT
      /////////////////////////////////////////////////////////
   }
};
