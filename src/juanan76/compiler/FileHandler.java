package juanan76.compiler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class FileHandler {
	
	public BufferedReader in;
	public PrintWriter out;
	
	public FileHandler(String path, boolean flag) throws IOException
	{
		if (flag) this.out = new PrintWriter(new FileWriter(path));
		else this.in = new BufferedReader(new FileReader(path));
	}
	
	public void writeInstruction(int opcode, int payload, String desc)
	{
		String s1 = Integer.toBinaryString(opcode);
		while (s1.length() < 6)
			s1 = "0" + s1;
		
		String s2 = Integer.toBinaryString(payload);
		while (s2.length() < 10)
			s2 = "0" + s2;
		
		out.print(s1 + s2);
		out.println("\t\t " + desc);
	}
}
