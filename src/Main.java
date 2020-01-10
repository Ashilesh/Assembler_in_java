import java.util.*;
import java.io.*;

public class Main{
	
	static LinkedList<SymbolTable> symTable = new LinkedList<SymbolTable>();
	static int lc = 0;
	static int inc = 0;
	static FileWriter fw;
	
	public static void main(String[] args) throws IOException{
//		
//		FileWriter fw = new FileWriter("input.asm");
//		
//		fw.write("Hello");
//		fw.close();
		
		String str;
		String tokens[];
		Main m = new Main();
		int i = 0;
		
		
		
		
		try{
			FileReader fr = new FileReader("input.asm");
			fw = new FileWriter("output.txt");
			Scanner sc = new Scanner(new BufferedReader(fr));
			boolean check,first_t;
			
			
			while(sc.hasNextLine()){

				// write counter to file
				fw.write(lc + "\t");
				// end
				
				check = false;
				
				inc = 0;
				
				str = sc.nextLine();
				
				tokens = str.split("\t");
				
				
				if(tokens.length != 0) {
					
					// check instruction i.e. tokens[1]
					check = m.checkInstructionPOT(tokens[1]);
					
					if(check) {
						m.POTOperation(tokens[1],tokens[2],tokens[0]);
					}
					else{
						check = m.checkInstructionMOT(tokens[1]);
						
						// if found in mot do operation according to format
						if(check){
							m.motOperation(tokens[1],tokens[2]);
						}
							
					}
					
					
					// end tokens[1]
					
					
					// check label i.e. tokens[0] also some pot operation happening
					if(tokens[0].length() != 0 ){
						
						m.insertLabel(tokens[0], tokens[1], tokens[2]);
					}
					//end tokens[0]
					
					
					// store in file
					System.out.println("location counter : " + lc);
					
					
					fw.write("\n");
					// end store in file
					
					
					
					// increment lc
					lc = lc + inc;
					// end inc lc
					

					
				}
				else
					break;
				
			}
			
			fr.close();
			fw.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("File Not Found!");
			System.exit(1);
		}
		
		// testing symbol table
		System.out.println("\nSymbol Table------------");
		for(SymbolTable s : symTable){
			System.out.println(s.sym_name + " " + s.addr + " " + s.length + " " + s.relocation);
		}
		
	}


	void motOperation(String opString,String param) throws FileNotFoundException,IOException{
		
		// get file contents in tokens[] and check if exist
		FileReader fr = new FileReader("MOT.txt");
		
		int positionOfSymbol = -1;
		
		String tokens[] = null;
		boolean res = false, isSymbolInTable = false;
		
		Scanner sc = new Scanner(new BufferedReader(fr));
		
		while(sc.hasNextLine()){
			String temp = sc.nextLine();
			tokens = temp.split("\t");
			
			if(tokens[0].equalsIgnoreCase(opString)){
				res = true;
				break;
			}
			
		}
		// end check if exist
		

		String params[] = param.split(",");
		
		// performing operation 
		if(res){
			
			if(tokens[3].equalsIgnoreCase("RR")){
				
				inc = Integer.parseInt(tokens[2]);
				
				// write instruction ,label and registers in file
				fw.write("\t" + opString + "\t" + param);
				
			}
			else if(tokens[3].equalsIgnoreCase("RX")){
				
				// write instruction and first register to file
				fw.write("\t" + opString + "\t" + params[0] + "," );
				
				// check if already exist in symbol table
				for(int i = 0 ; i < symTable.size() ; i++){
					if(symTable.get(i).sym_name.equalsIgnoreCase(params[1])){
						positionOfSymbol = i;	
						isSymbolInTable = true;
					}
				}
				// end checking
				
				
				// if not in symbol table create new object
				if(!isSymbolInTable){
					SymbolTable s = new SymbolTable();
					s.sym_name = params[1];
					
					symTable.add(s);
					
					positionOfSymbol = symTable.size() - 1;
					
				}
				// end new object
				
				// set increment counter
				inc = Integer.parseInt(tokens[2]);
				//
				
				// write $ to file
				fw.write("$" + positionOfSymbol);
			}
			
			
		}
	}


	void insertLabel(String string0, String string1,String string2) {
	
		boolean found = false;
		String reloc = null;
		SymbolTable s;
		
		int length = -1;
		// set address of symbol
		int addr = lc;
		
		// set length
		if(string1.equalsIgnoreCase("START"))
			length = 1;
		else if(string1.equalsIgnoreCase("DC")){
			length = dcSymbolLength(string2);
			inc = length;
		}
		else if(string1.equalsIgnoreCase("DS")){
			length = dsSymbolLength(string2);
			inc = length;
		}
			
		
		// set relocation
		reloc = "R";
		
		//find the label in symbol Table
		for(int i = 0 ; i < symTable.size() ; i++){
			s = symTable.get(i);
			
			// if found update the object
			if(s.sym_name.equalsIgnoreCase(string0)){
				s.addr = addr;
				s.length = length;
				s.relocation = reloc;
				
				symTable.remove(i);
				symTable.add(i, s);
				found = true;
				break;
			}	
		}
		
		// if not found create new object
		if(!found){
			SymbolTable st = new SymbolTable(string0, addr, length, reloc);
			symTable.add(st);
		}
		
		
		
	}


	boolean checkInstructionPOT(String ins) throws FileNotFoundException{
		
		FileReader fr = new FileReader("POT.txt");
		boolean res = false;
		
		Scanner sc = new Scanner(new BufferedReader(fr));
		
		while(sc.hasNextLine()){
			String temp = sc.nextLine();
			
			if(temp.length() != 0)
				if(temp.equalsIgnoreCase(ins))
					res = true;
				
		}
		
		sc.close();
		
		return res;
		
	}
	
	boolean checkInstructionMOT(String ins) throws FileNotFoundException{
		
		FileReader fr = new FileReader("MOT.txt");
		
		String tokens[];
		
		boolean res = false;
		
		Scanner sc = new Scanner(new BufferedReader(fr));
		
		while(sc.hasNextLine()){
			String temp = sc.nextLine();
			tokens = temp.split("\t");
			
			if(tokens[0].equalsIgnoreCase(ins)){
				res = true;
				break;
			}
			
		}
		
		return res;
	}
	
	// insert the label when it is in parameter i.e. in tokens[2]
	boolean insertLabelParameter(String label){
		boolean res = false;
		
		for(SymbolTable s : symTable){
			if(label.equalsIgnoreCase(s.sym_name))
				res = true;
		}
			
		if(!res){
			SymbolTable s = new SymbolTable();
			s.sym_name = label;
			
			symTable.add(s);
			
			System.out.println("Added label from parameter.");
		}
		
		return res;
	}
	
	
	void POTOperation(String operation,String parameter,String label)throws IOException{
		
		if(operation.equalsIgnoreCase("START")) {
			startOperation(parameter);
			fw.write(label + "\t" + operation + "\t" + parameter);
		}
		else if(operation.equalsIgnoreCase("USING")) {
			fw.write(label + "\t" + operation + "\t" + parameter); 
		}
	}

	void startOperation(String par){
		int l = Integer.parseInt(par);
		
		lc = l;	
	}
	
	int dcSymbolLength(String param) {
		
		int len = 4;
		
		
		String tokens[] = param.split("'");
		String lengths[] = tokens[0].split("F");
		// check for half word too
		
		if(lengths.length != 0)
			len = Integer.parseInt(lengths[0]);
		
		try {
			fw.write(tokens[1]);
		}catch(IOException e) {
			System.out.println("Unable to print.");
		}
		return len;
		
	}
	
	int dsSymbolLength(String string2) {
		
		String len_s = string2.substring(0, string2.length()-1);
		
		int len = Integer.parseInt(len_s);
		
		String type = string2.substring(string2.length()-1);
		
		if(type.equals("F"))
			len = len * 4;
		
		return len;
	}
}

class SymbolTable{
	String sym_name;
	int addr;
	int length;
	String relocation;
	
	public SymbolTable(){
		this.addr = -1;
	}
	
	public SymbolTable(String s, int a, int l, String r){
		sym_name = s;
		addr = a;
		length = l;
		relocation = r;
	}
	
}
