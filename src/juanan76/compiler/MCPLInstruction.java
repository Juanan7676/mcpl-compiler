package juanan76.compiler;

public class MCPLInstruction {
	String literal;
	int line;
	int opcode;
	int payload;
	boolean writable;
	boolean invertCMP;
	String var;
	
	public MCPLInstruction(String l, int line) {
		this.literal = l;
		this.line = line;
		this.writable = true;
	}
	
	private static boolean testForNumber(String str)
	{
		try {
			Integer.parseInt(str);
		} 
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
	
	/*
	 * Compiles an MCPL instruction. Returns the name of the function it's compiling into, null
	 * if it's the main function.
	 */
	public String compile() throws CompileException
	{
		String[] var = literal.split(" ");
		
		if (var[0].equals("mov"))
		{
			if (var.length == 1) throw new CompileException("Illegal syntax at instruction "+literal+" on line "+line);
			String[] payload = literal.substring(4).replaceAll(" ", "").split(",");
			if (payload.length != 2) throw new CompileException("Illegal syntax at instruction "+literal+" on line "+line);
			if (payload[0].equalsIgnoreCase("%eax%") && payload[1].equalsIgnoreCase("%ebx%"))
			{ // MVAB
				this.opcode = 4;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%eax%") && payload[1].equalsIgnoreCase("%ecx%"))
			{ // MVAC
				this.opcode = 5;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%ebx%") && payload[1].equalsIgnoreCase("%eax%"))
			{ // MVBA
				this.opcode = 6;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%ebx%") && payload[1].equalsIgnoreCase("%ecx%"))
			{ // MVBC
				this.opcode = 7;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%ecx%") && payload[1].equalsIgnoreCase("%eax%"))
			{ // MVCA
				this.opcode = 8;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%ecx%") && payload[1].equalsIgnoreCase("%ebx%"))
			{ // MVCB
				this.opcode = 9;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%eax%") && payload[1].equalsIgnoreCase("%out%"))
			{ // OUTA
				this.opcode = 0x2E;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%ebx%") && payload[1].equalsIgnoreCase("%out%"))
			{ // OUTB
				this.opcode = 0x2F;
				this.payload = 0;
			}
			else if (payload[0].equalsIgnoreCase("%ecx%") && payload[1].equalsIgnoreCase("%out%"))
			{ // OUTC
				this.opcode = 0x30;
				this.payload = 0;
			}
			
			else if (Main.declaredVariables.containsKey(payload[0]))
			{ // mov var,?
				if (payload[1].equalsIgnoreCase("%eax%"))
				{
					this.opcode = 10;
					this.payload = -1;
					this.var = payload[0];
				}
				else if (payload[1].equalsIgnoreCase("%ebx%"))
				{
					this.opcode = 11;
					this.payload = -1;
					this.var = payload[0];
				}
				else if (payload[1].equalsIgnoreCase("%ecx%"))
				{
					this.opcode = 12;
					this.payload = -1;
					this.var = payload[0];
				}
				else throw new CompileException("Trying to move a variable to a non-register: "+literal+" on line "+line);
			}
			else if (Main.declaredVariables.containsKey(payload[1]))
			{ // mov ?,var
				if (payload[0].equalsIgnoreCase("%eax%"))
				{
					this.opcode = 13;
					this.payload = -1;
					this.var = payload[1];
				}
				else if (payload[0].equalsIgnoreCase("%ebx%"))
				{
					this.opcode = 14;
					this.payload = -1;
					this.var = payload[1];
				}
				else if (payload[0].equalsIgnoreCase("%ecx%"))
				{
					this.opcode = 15;
					this.payload = -1;
					this.var = payload[1];
				}
				else throw new CompileException("Trying to move a non-register into a variable: "+literal+" on line "+line);
			}
			else if (testForNumber(payload[0]))
			{ //mov constant,?
				if (payload[1].equalsIgnoreCase("%eax%"))
				{
					this.opcode = 1;
					this.payload = Integer.parseInt(payload[0]);
				}
				else if (payload[1].equalsIgnoreCase("%ebx%"))
				{
					this.opcode = 2;
					this.payload = Integer.parseInt(payload[0]);
				}
				else if (payload[1].equalsIgnoreCase("%ecx%"))
				{
					this.opcode = 3;
					this.payload = Integer.parseInt(payload[0]);
				}
				else throw new CompileException("Trying to move a constant to a non-register: "+literal+" on line "+line);
			}
			else throw new CompileException("Illegal instruction mov: "+literal+" on line "+line);
		}
		else if (var[0].equals("add"))
		{
			String payload = literal.substring(4).replaceAll(" ", "");
			if (payload.equalsIgnoreCase("%ebx%"))
			{
				this.opcode = 0x11;
				this.payload = 0;
			}
			else if (payload.equalsIgnoreCase("%ecx%"))
			{
				this.opcode = 0x12;
				this.payload = 0;
			}
			else if (Main.declaredVariables.containsKey(payload))
			{
				this.opcode = 0x13;
				this.payload = -1;
				this.var = payload;
			}
			else if (testForNumber(payload))
			{
				this.opcode = 0x10;
				this.payload = Integer.parseInt(payload);
			}
			else throw new CompileException("Illegal ADD instruction: "+literal+" on line "+line);
		}
		
		else if (var[0].equals("sub"))
		{
			String payload = literal.substring(4).replaceAll(" ", "");
			if (payload.equalsIgnoreCase("%ebx%"))
			{
				this.opcode = 0x15;
				this.payload = 0;
			}
			else if (payload.equalsIgnoreCase("%ecx%"))
			{
				this.opcode = 0x16;
				this.payload = 0;
			}
			else if (Main.declaredVariables.containsKey(payload))
			{
				this.opcode = 0x17;
				this.payload = -1;
				this.var = payload;
			}
			else if (testForNumber(payload))
			{
				this.opcode = 0x14;
				this.payload = Integer.parseInt(payload);
			}
			else throw new CompileException("Illegal SUB instruction: "+literal+" on line "+line);
		}
		
		else if (var[0].equals("mul"))
		{
			String payload = literal.substring(4).replaceAll(" ", "");
			if (payload.equalsIgnoreCase("%ebx%"))
			{
				this.opcode = 0x19;
				this.payload = 0;
			}
			else if (payload.equalsIgnoreCase("%ecx%"))
			{
				this.opcode = 0x1A;
				this.payload = 0;
			}
			else if (Main.declaredVariables.containsKey(payload))
			{
				this.opcode = 0x1B;
				this.payload = -1;
				this.var = payload;
			}
			else if (testForNumber(payload))
			{
				this.opcode = 0x18;
				this.payload = Integer.parseInt(payload);
			}
			else throw new CompileException("Illegal MUL instruction: "+literal+" on line "+line);
		}
		
		else if (var[0].equals("div"))
		{
			String payload = literal.substring(4).replaceAll(" ", "");
			if (payload.equalsIgnoreCase("%ebx%"))
			{
				this.opcode = 0x1D;
				this.payload = 0;
			}
			else if (payload.equalsIgnoreCase("%ecx%"))
			{
				this.opcode = 0x1E;
				this.payload = 0;
			}
			else if (Main.declaredVariables.containsKey(payload))
			{
				this.opcode = 0x1F;
				this.payload = -1;
				this.var = payload;
			}
			else if (testForNumber(payload))
			{
				this.opcode = 0x1C;
				this.payload = Integer.parseInt(payload);
			}
			else throw new CompileException("Illegal DIV instruction: "+literal+" on line "+line);
		}
		else if (var[0].equals("cmp"))
		{
			if (var.length == 1) throw new CompileException("Illegal syntax at instruction "+literal+" on line "+line);
			String[] payload = literal.substring(4).replaceAll(" ", "").split(",");
			if (payload.length != 2) throw new CompileException("Illegal syntax at instruction "+literal+" on line "+line);
			if ((payload[0].equalsIgnoreCase("%eax%") && payload[1].equalsIgnoreCase("%ebx")) || (payload[1].equalsIgnoreCase("%eax%") && payload[0].equalsIgnoreCase("%ebx%")))
			{
				this.opcode = 0x20;
				this.payload = 0;
				this.invertCMP = payload[0].equalsIgnoreCase("%ebx%");
			}
			else if ((payload[0].equalsIgnoreCase("%eax%") && payload[1].equalsIgnoreCase("%ecx")) || (payload[1].equalsIgnoreCase("%eax%") && payload[0].equalsIgnoreCase("%ecx%")))
			{
				this.opcode = 0x21;
				this.payload = 0;
				this.invertCMP = payload[0].equalsIgnoreCase("%ecx%");
			}
			else if ((payload[0].equalsIgnoreCase("%ebx%") && payload[1].equalsIgnoreCase("%ecx")) || (payload[1].equalsIgnoreCase("%ebx%") && payload[0].equalsIgnoreCase("%ecx%")))
			{
				this.opcode = 0x22;
				this.payload = 0;
				this.invertCMP = payload[0].equalsIgnoreCase("%ecx%");
			}
			else if (((payload[0].equalsIgnoreCase("%eax%")) && Main.declaredVariables.containsKey(payload[1])) || ((payload[1].equalsIgnoreCase("%eax%")) && Main.declaredVariables.containsKey(payload[0])))
			{
				this.opcode = 0x23;
				this.payload = -1;
				this.var = (payload[0].equalsIgnoreCase("%eax%")) ? payload[1] : payload[0];
				this.invertCMP = !payload[0].equalsIgnoreCase("%eax%");
			}
			else if (((payload[0].equalsIgnoreCase("%ebx%")) && Main.declaredVariables.containsKey(payload[1])) || ((payload[1].equalsIgnoreCase("%ebx%")) && Main.declaredVariables.containsKey(payload[0])))
			{
				this.opcode = 0x24;
				this.payload = -1;
				this.var = (payload[0].equalsIgnoreCase("%ebx%")) ? payload[1] : payload[0];
				this.invertCMP = !payload[0].equalsIgnoreCase("%ebx%");
			}
			else if (((payload[0].equalsIgnoreCase("%ecx%")) && Main.declaredVariables.containsKey(payload[1])) || ((payload[1].equalsIgnoreCase("%ecx%")) && Main.declaredVariables.containsKey(payload[0])))
			{
				this.opcode = 0x25;
				this.payload = -1;
				this.var = (payload[0].equalsIgnoreCase("%ecx%")) ? payload[1] : payload[0];
				this.invertCMP = !payload[0].equalsIgnoreCase("%ecx%");
			}
			else if ((payload[0].equalsIgnoreCase("%eax%") && testForNumber(payload[1])) || (payload[1].equalsIgnoreCase("%eax%") && testForNumber(payload[0])))
			{
				this.opcode = 0x26;
				this.payload = (payload[0].equalsIgnoreCase("%eax%")) ? Integer.parseInt(payload[1]) : Integer.parseInt(payload[0]);
			}
			else if ((payload[0].equalsIgnoreCase("%ebx%") && testForNumber(payload[1])) || (payload[1].equalsIgnoreCase("%ebx%") && testForNumber(payload[0])))
			{
				this.opcode = 0x27;
				this.payload = (payload[0].equalsIgnoreCase("%ebx%")) ? Integer.parseInt(payload[1]) : Integer.parseInt(payload[0]);
			}
			else if ((payload[0].equalsIgnoreCase("%ecx%") && testForNumber(payload[1])) || (payload[1].equalsIgnoreCase("%ecx%") && testForNumber(payload[0])))
			{
				this.opcode = 0x28;
				this.payload = (payload[0].equalsIgnoreCase("%ecx%")) ? Integer.parseInt(payload[1]) : Integer.parseInt(payload[0]);
			}
			else if ((Main.declaredVariables.containsKey(payload[0]) && testForNumber(payload[1])) || (Main.declaredVariables.containsKey(payload[1]) && testForNumber(payload[0])))
			{
				this.opcode = 0x29;
				this.payload = (Main.declaredVariables.containsKey(payload[0])) ? Integer.parseInt(payload[1]) : Integer.parseInt(payload[0]);
			}
			else throw new CompileException("Illegal cmp instruction "+literal+" on line "+line);
		}
		else if (var[0].equals("jmp"))
		{
			if (var.length < 2) throw new CompileException("Illegal syntax on jmp instruction "+literal+" on line "+line);
			String payload = literal.substring(4).replaceAll(" ", "");
			this.opcode = 0x2A;
			this.var = payload;
		}
		else if (var[0].equals("jle"))
		{
			if (var.length < 2) throw new CompileException("Illegal syntax on jmp instruction "+literal+" on line "+line);
			String payload = literal.substring(4).replaceAll(" ", "");
			this.opcode = 0x2B;
			this.var = payload;
		}
		else if (var[0].equals("jge"))
		{
			if (var.length < 2) throw new CompileException("Illegal syntax on jmp instruction "+literal+" on line "+line);
			String payload = literal.substring(4).replaceAll(" ", "");
			this.opcode = 0x2C;
			this.var = payload;
		}
		else if (var[0].equals("jeq"))
		{
			if (var.length < 2) throw new CompileException("Illegal syntax on jmp instruction "+literal+" on line "+line);
			String payload = literal.substring(4).replaceAll(" ", "");
			this.opcode = 0x2D;
			this.var = payload;
		}
		else if (var[0].equals("def"))
		{
			if (var.length < 2) throw new CompileException("Illegal syntax on function declaration on instruction "+literal+" on line "+line);
			this.writable = false;
			return literal.substring(4).replaceAll(" ", "");
		}
		else if (var[0].equals("ret"))
		{
			if (var.length > 1) throw new CompileException("Illegal ret statement "+literal+" on line "+line);
			this.writable = false;
			return "";
		}
		else if (var[0].equals("call"))
		{
			if (var.length != 2) throw new CompileException("Illegal syntax on statement "+literal+" on line "+line);
			String payload = literal.substring(4).replaceAll(" ", "");
			if (!Main.declaredFunctions.containsKey(payload)) throw new CompileException("Unrecognized function on statement "+literal+" on line "+line);
			for (MCPLInstruction i : Main.declaredFunctions.get(payload))
				Main.instructions.add(i);
			this.writable = false;
		}
		
		else if (var[0].equals("int"))
		{
			String[] payload = literal.substring(4).replaceAll(" ", "").split(",");
			if (payload.length == 1)
			{
				this.writable = false;
				Main.declaredVariables.put(payload[0], null);
			}
			else if (payload.length == 2)
			{
				if (testForNumber(payload[1]))
				{
					this.writable = false;
					Main.declaredVariables.put(payload[0], Integer.parseInt(payload[1]));
				}
				else throw new CompileException("Illegal initialization of variable: "+literal+ "on line "+line);
			}
			else throw new CompileException("Illegal syntax: "+literal+" on line"+line);
		}
		else if (var[0].equals("hlt"))
		{
			this.opcode = 63;
			this.payload = 0;
		}
		else throw new CompileException("Unrecognized instruction: "+literal+" on line "+line);
		return null;
	}
	@Override
	public String toString()
	{
		return "opcode:"+Integer.toHexString(this.opcode)+", payload: "+Integer.toString(this.payload)+", literal:"+this.literal;
	}
}
