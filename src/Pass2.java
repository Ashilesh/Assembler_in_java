import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;


public class Pass2 {
	
	private static FileWriter fw;
	public static int register_no;
	public static int register_val;
	
	
	public static void main(String[] args){
		
		FileReader fr;
		
		try{
			
			String str = null;
			String tokens[] = null;
			boolean resPotSearch = false;
			boolean resMotSearch = false;
			
			fr = new FileReader("output.txt");
			Scanner sc = new Scanner(new BufferedReader(fr));
			fw = new FileWriter("output_pass2.txt");
			
			while(sc.hasNextLine()){
				
				str = sc.nextLine();
				
				
				
				tokens = str.split("\t");
				
				// if the length of token is 4 then perform operation
				if(tokens.length == 4){
					resPotSearch = potSearch(tokens[2],tokens[3],tokens[0]);
					
					if(!resPotSearch){
						resMotSearch = motSearch(tokens[2],tokens[3],tokens[0]);
					}
				}else {
					
					// writing in file
					fw.write(str + "\n");
				}
			}
			
			sc.close();
			fw.close();
			fr.close();
		}
		catch(FileNotFoundException e){
			
			System.out.println("Error in opening File.");
		}
		catch(Exception e){
			System.out.println("Exception occured");
		}
		
	}


	private static boolean motSearch(String operation, String param, String current_lc)throws FileNotFoundException,IOException {
		boolean res = false;
		String str = null;
		String tokens[] = null;
		
		FileReader fr = new FileReader("MOT.txt");
		Scanner sc = new Scanner(new BufferedReader(fr));
		
		while(sc.hasNextLine()){
			str = sc.nextLine();
			tokens = str.split("\t");
			
			if(tokens[0].equalsIgnoreCase(operation)){
				res = true;
				
				// if match found perform mot operation
				motOperation(param, tokens[1], tokens[3], current_lc);
			}
		}
		
		sc.close();
		
		return res;
	}


	private static void motOperation(String param, String opcode, String format, String current_lc) throws IOException{
		
		if(format.equalsIgnoreCase("RR")){
			// write in file here
			fw.write(current_lc + "\t" + opcode + "\t" + param + "\n");
		}
		else if(format.equalsIgnoreCase("RX")){
			
			
			String tokens[] = param.split(",");
			
			// write in file here
			fw.write(current_lc + "\t" + opcode + "\t" + tokens[0] + ",");
			
			// Literal table
			if(tokens[1].charAt(0) == '#')
				literalProcess(tokens[1].substring(1));
			else if(tokens[1].charAt(0) == '$')
				symbolProcess(tokens[1].substring(1));
				
		}
	}


	private static void symbolProcess(String substring) throws IOException{
		
		Scanner sc = null;
		
		try{
			int row_file = 0;
			int row = Integer.parseInt(substring);
			String tokens[] = null;
			
			FileReader fr = new FileReader("symbol_table.txt");
			sc = new Scanner(new BufferedReader(fr));
			
			while(sc.hasNextLine()){
				if(row_file == row){
					tokens = sc.nextLine().split("\t");
					int off = Integer.parseInt(tokens[1]) - register_val;
					
					// write here
					// remaining arrays condition
					fw.write(off + "(" + register_no + ", 0" + ")" + "\n");
					
				}else {
					sc.nextLine();
				}
				row_file = row_file + 1;
				
			}
			
		}catch(FileNotFoundException e){
			System.out.println("Error in opening file");
		}
		
	}


	private static void literalProcess(String substring) throws IOException{
		
		Scanner sc = null;
		try{
			int row_file = 1;
			int row = Integer.parseInt(substring);
			String tokens[] = null;
			
			FileReader fr = new FileReader("Literal_table.txt");
			sc = new Scanner(new BufferedReader(fr));
			
			while(sc.hasNextLine()){
				if(row_file == row){
					tokens = sc.nextLine().split("\t");
					int off = Integer.parseInt(tokens[1]) - register_val;
					
					// write here
					// remaining arrays condition
					fw.write(off + "(" + register_no + ", 0" + ")" + "\n");
					
				}
				else {
					sc.nextLine();
				}
				row_file = row_file + 1;
				
			}
			
		}catch(FileNotFoundException e){
			System.out.println("Error in opening file");
		}finally {
			sc.close();
		}
	}


	private static boolean potSearch(String operation, String param, String current_lc) throws FileNotFoundException{
		boolean res = false;
		String str = null;
		
		FileReader fr = new FileReader("POT.txt");
		Scanner sc = new Scanner(new BufferedReader(fr));
		
		while(sc.hasNextLine()){
		
			str = sc.nextLine();
			
			if(str.equalsIgnoreCase(operation)){
				potOperation(str,param,current_lc);
			}
			
		}
		
		sc.close();
		return res;
		
	}


	private static void potOperation(String str, String param, String current_lc) {
		
		String tokens[] = param.split(",");
		
		// operation for using
		if(str.equalsIgnoreCase("using")){
			
			if(tokens[0].equals("*")){
				register_no = Integer.parseInt(tokens[1]);
				register_val = Integer.parseInt(current_lc);
			}
			
		}
		
	}

}
