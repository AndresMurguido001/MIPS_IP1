      .data
arr: .word   0 : 12        # "array" of 12 words to contain values
n: .word  12             # size of "array" 
      .text
      # Get arr and n
      la $s0, arr        # $s0 = starting address of arr
      la $t0, n          # $t0 = address of n
      lw $s1, 0($t0)     # #s1 = RAM[0+$t0] = RAM[address of n] = n
      
      # Populate with twelve values
      # Offsets are constant
      addi $t1, $zero, 55
      sw $t1, 0($s0)
      addi $t1, $zero, 88
      sw $t1, 4($s0)
      addi $t1, $zero, 0
      sw $t1, 8($s0)
      addi $t1, $zero, 22
      sw $t1, 12($s0)
      addi $t1, $zero, 77
      sw $t1, 16($s0)
      addi $t1, $zero, 44
      sw $t1, 20($s0)
      addi $t1, $zero, 99
      sw $t1, 24($s0)
      addi $t1, $zero, 33
      sw $t1, 28($s0)
      addi $t1, $zero, 110
      sw $t1, 32($s0)
      addi $t1, $zero, 66
      sw $t1, 36($s0)
      addi $t1, $zero, 121
      sw $t1, 40($s0)
      addi $t1, $zero, 11
      sw $t1, 44($s0)

      ##################################################################
      # AT THIS POINT: $s0 is arr, which has been initialized
      #                $s1 is n, which has been initialized to 12
      #################################################################
      
      ##################################################################
      # INITIALIZE I
      addi $s2, $zero, 11       # Initialize i to 4.
                               # You can change this to any value 1-11
                               # I recommend trying a few when testing
                               # The array that prints should have elements
                               # i and i+1 swapped, for any i that you supply
      ##################################################################

      ##################################################################
      
      # PUT CODE HERE
      # $s0 = arr location
      # $s2 = i
      # $s4 = element
      # s3 = j
      
      addi $s3, $zero, 0 # initialize j
      addi $s4, $zero, 0 # initialize element
      
      addi $s3, $s2, -1 # j = i - 1
      
      sll $t0, $s2, 2 # i * 4
      add $t0, $s0, $t0 # add i to array location $t0 = i + arr
      lw $s4, 0($t0) # element = arr[i]
      
      # obtain RAM indicies
      addi $t0, $s3, 1 # $t0 = [j+1]
      sll $t0, $t0, 2 # $t0 = [j+1] * 4
      sll $t1, $s3, 2 # $t1 = j * 4
      
      # retrieve array item arr[j]
      add $t1, $s0, $t1 # $t1 = arr + (j)
      lw $t2, 0($t1) # $t2 = arr[j]
      
      #store arr[j] in arr[j+1]
      add $t1, $t0, $s0 # $t1 = arr + [j+1]
      sw $t2, 0($t1) # arr[j+1] = arr[j]
      
      addi $s3, $s3, -1 # j = j - 1
      addi $t0, $s3, 1 # $t0 = j + 1
      sll $t0, $t0, 2 # [j + 1] * 4
      
      add $t0, $s0, $t0 # $t0 = arr + [j+1]
      sw $s4, 0($t0) # arr[j+1] = element
      
      ##################################################################
                         
      ##################################################################
      # PRINTS ARRAY, DO NOT MODIFY
      la   $a0, arr        
      add  $a1, $s1, $zero  
      jal  print            
      li   $v0, 10          
      syscall               
      ##################################################################

########################################################################
#########  routine to print the numbers on one line. 
#########  don't touch anything below this line!!!!

      .data
space:.asciiz  " "          # space to insert between numbers
head: .asciiz  "Sorted array:\n"
      .text
print:add  $s0, $zero, $a0  # starting address of array
      add  $t1, $zero, $a1  # initialize loop counter to array size
      la   $a0, head        # load address of print heading
      li   $v0, 4           # specify Print String service
      syscall               # print heading
out:  lw   $a0, 0($s0)      # load number for syscall
      li   $v0, 1           # specify Print Integer service
      syscall               # print number
      la   $a0, space       # load address of spacer for syscall
      li   $v0, 4           # specify Print String service
      syscall               # output string
      addi $s0, $s0, 4      # increment address
      addi $t1, $t1, -1     # decrement loop counter
      bgtz $t1, out         # repeat if not finished
      jr   $ra              # return
########################################################################
	
