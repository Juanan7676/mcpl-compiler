package juanan76.compiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class Main {
	
	public static Map<String,Integer> declaredVariables;
	public static Map<String,Integer> declaredLabels;
	public static Map<String,List<MCPLInstruction>> declaredFunctions;
	public static List<MCPLInstruction> instructions;
	
	public static final String[] opcodes = new String[] {
	"NOP", "LDA", "LDB", "LDC", "MVAB", "MVAC", "MVBA", "MVBC", "MVCA", "MVCB", "MVA", "MVB", 
	"MVC","SVA","SVB","SVC","ADD","ADDB","ADDC","ADDV","SUB","SUBB","SUBC","SUBV","MUL","MULB",
	"MULC","MULV","DIV","DIVB","DIVC","DIVV","CMPAB","CMPAC","CMPBC","CMPAV","CMPBV","CMPCV",
	"TSTA","TSTB","TSTC","TSTV","JMP","JLE","JGE","JEQ","OUTA","OUTB","OUTC","OUTV","OUTL","HLT"
	};
	
	public static void main(String[] args)
	{
		System.out.println("+--------------------------------+");
		System.out.println("+     ASPARGUS MCPL COMPILER     +");
		System.out.println("+      Version 1.0.0 - rev 1     +");
		System.out.println("+--------------------------------+");
		System.out.println();
		System.out.println("An open-source Java compiler for Aspargus processors.");
		System.out.println();
		
		if (args.length > 1)
		{
			System.out.println("I'm going to compile file "+args[0]+".");
			System.out.println("I'm going to store the machine code at "+args[1]+".");
			try {
				FileHandler input = new FileHandler(args[0],false);
				FileHandler output = new FileHandler(args[1],true);
				
				declaredVariables = new HashMap<String,Integer>();
				declaredLabels = new HashMap<String,Integer>();
				declaredFunctions = new HashMap<String,List<MCPLInstruction>>();
				instructions = new Vector<MCPLInstruction>();
				
				String l, fun = null, currfun = null;
				boolean onCommentary = false;
				boolean isCmp = false, invert = false;
				int counter = 0;
				
				while ((l=input.in.readLine())!=null)
				{
					counter++;
					if (onCommentary)
					{
						if (l.contains("*/")) onCommentary = false;
						continue;
					}
					if (l.startsWith("#")) continue;
					if (l.startsWith("/*"))
					{
						onCommentary = true;
						continue;
					}
					if (l.replaceAll(" ", "").equals("")) continue;
					
					l = l.replaceAll("\t", "");
					
					if (l.startsWith(":"))
					{
						Main.declaredLabels.put(l.substring(1), Main.instructions.size());
						continue;
					}
					
					MCPLInstruction ins = new MCPLInstruction(l,counter);
					fun = ins.compile();
					if (!isCmp && ins.opcode >= 0x20 && ins.opcode <= 0x29)
					{
						isCmp = true;
						invert = ins.invertCMP;
					}
					else if (ins.opcode >= 0x20 && ins.opcode <= 0x29)
						throw new CompileException("Conditional jump instruction without preceeding CMP on line"+counter);
					else if (isCmp)
					{
						if (ins.opcode < 0x2B || ins.opcode > 0x2D) throw new CompileException("CMP without conditional jump instruction on line "+(counter-1)+": "+ins.literal);
						isCmp = false;
						if (invert && ins.opcode != 0x2D) ins.opcode = (ins.opcode==0x2B) ? 0x2C : 0x2B;
					}
					
					
					if (fun != null)
					{
						if (fun.equals("") && currfun != null)
						{ // ret statement, end function
							fun = null;
							currfun = null;
						}
						else
						{ // def statement, new function
							if (currfun != null) throw new CompileException("Illegal function declaration inside a function: "+ins.literal+" on line "+ins.line);
							declaredFunctions.put(fun, new Vector<MCPLInstruction>());
							currfun = fun;
							fun = null;
						}
					}
					
					if (ins.writable) 
					{
						if (currfun==null) instructions.add(ins);
						else declaredFunctions.get(currfun).add(ins);
					}
				}
				
				input.in.close();
				
				// Resolve labels of JMP instructions
				for (MCPLInstruction i : Main.instructions)
				{
					if (i.opcode >= 0x2A && i.opcode <= 0x2D)
					{
						if (!Main.declaredLabels.containsKey(i.var)) throw new CompileException("Unrecognized label"+i.var+" at instruction "+i.literal+" on line "+i.line); 
						i.payload = Main.declaredLabels.get(i.var);
					}
				}
				
				// Assign memory addresses to variables
				int k = 0;
				Map<String,Integer> values = new HashMap<String,Integer>();
				for (String var : declaredVariables.keySet())
				{
					values.put(var,declaredVariables.get(var));
					declaredVariables.replace(var, instructions.size()+k);
					k++;
				}
				
				// Write instructions to file & replace variables in instructions with memory addresses
				for (MCPLInstruction i : instructions)
				{
					if (i.payload==-1)
						i.payload = declaredVariables.get(i.var);
					if (i.writable) output.writeInstruction(i.opcode, i.payload, (i.opcode==63) ? "HLT" : opcodes[i.opcode]+" "+i.payload);
				}
				for (String a : values.keySet())
				{
					if (values.get(a) != null) output.writeInstruction(0, values.get(a), "VARIABLE "+a+" = "+values.get(a));
					else output.writeInstruction(0, 0, "VARIABLE "+a);
				}
				output.out.flush();
				output.out.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (CompileException e) {
				System.out.println("A compilation error has ocurred: \n");
				System.out.println(e.getMessage()+"\n");
				System.out.println("Compilation result: FAILURE");
				return;
			}
		}
		System.out.println("\nCompilation result: SUCCESS");
	}
}
