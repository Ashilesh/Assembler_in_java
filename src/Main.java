import java.util.*;
import java.io.*;

public class Main{
	
	static LinkedList<SymbolTable> symTable = new LinkedList<SymbolTable>();
	static int lc = 0;
	static int inc = 0;
	static FileWriter fw;
	static LinkedList<LiteralTable> litTable = new LinkedList<LiteralTable>();
	
	public static void main(String[] args) throws IOException{
		
		String str;
		String tokens[];
		Main m = new Main();

		try{
			FileReader fr = new FileReader("input.asm");
			fw = new FileWriter("output.txt");
			Scanner sc = new Scanner(new BufferedReader(fr));
			boolean check;
			
			
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
						if(tokens.length == 3)
							m.POTOperation(tokens[1],tokens[2],tokens[0]);
						else if(tokens.length == 2)
							m.POTOperation(tokens[1], null, tokens[0]);
					}
					else{
						if(tokens.length == 3)
							check = m.motOperation(tokens[1],tokens[2]);
						
							
							
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
					// end increment lc
					

					
				}
				else
					break;
				
			}
			
			sc.close();
			fr.close();
			fw.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println("File Not Found!");
			System.exit(1);
		}
		
		// inserting symbol table in file
		FileWriter fw = new FileWriter("symobl_table.txt");
		for(SymbolTable s : symTable){
			fw.write(s.sym_name + "\t" + s.addr + "\t" + s.length + "\t" + s.relocation + "\n");
		}
		
		fw.close();
		
		
		// inserting literal table in file
		fw = new FileWriter("literal_table.txt");
		
		for(LiteralTable s : litTable){
			fw.write(s.name + "\t" + s.addr + "\t" + s.length + "\t" + s.relocation + "\n");
		}
		
		fw.close();
	}


	boolean motOperation(String opString,String param) throws FileNotFoundException,IOException{
		
		// get file contents in tokens[] and check if exist
		FileReader fr = new FileReader("MOT.txt");
		
		
		
		String tokens[] = null;
		boolean res = false;
		
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
				
				
				// processing second parameter of RX
				processSecondParam(params[1]);
				
				// increment counter
				inc = Integer.parseInt(tokens[2]);
				
			}
			
			
		}
		sc.close();
		
		return res;
	}


	void insertLabel(String string0, String string1,String string2) throws IOException {
	
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
		
		sc.close();
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
		else if(operation.equalsIgnoreCase("LTORG")){
			processLitreral();
		}
		else if(operation.equalsIgnoreCase("END")){
			int temp_lc = lc;
			processLitreral();
			if(lc != temp_lc)
				fw.write("\n" + lc);
		}
	}

	void startOperation(String par)throws IOException{
		int l = Integer.parseInt(par);
		
		lc = l;	
		
		fw.write("\n" + lc + "\t");
		
	}
	
	
	
	int dcSymbolLength(String param) throws IOException {
		
		// remaining: check for array here
		
		int len = 4;
		
		
		String tokens[] = param.split("'");
		String lengths[] = tokens[0].split("F");
		// remaining: check for half word too
		
		
		
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
		
		// getting length in string '1F' getting 1
		String len_s = string2.substring(0, string2.length()-1);
		
		int len = Integer.parseInt(len_s);
		
		// getting type of storage
		String type = string2.substring(string2.length()-1);
		
		//checking type
		if(type.equals("F"))
			len = len * 4;
		
		return len;
	}
	
	void insertLiteral(String val) throws IOException{
		
		boolean isAlreadyInLiteralTable = false;
		int position = -1;
		
		for(int i = 0 ; i < litTable.size() ; i++){
			if(litTable.get(i).name.equalsIgnoreCase(val)){
				position = i;
				isAlreadyInLiteralTable = true;
				position = position + 1;
				break;
			}
			
		}
		
		// if not in literal table 
		// insert into Linked List
		if(!isAlreadyInLiteralTable){
			LiteralTable temp = new LiteralTable();
			temp.name = val;
			
			litTable.add(temp);
			
			position = litTable.size();
		}
		
		fw.write("#" + position);
		
	}
	
	void processLitreral() throws IOException{
		
		// todo : Handle literals if they are in array
		
		for(int i = 0 ; i < litTable.size() ; i++){
			
			LiteralTable l = litTable.get(i);
			
			// process if addr is -1
			if(l.addr == -1){
				
				
				String tokens[] = l.name.split("'");
				String length;
				int lenInInt = 0;
				
				// size of literal
				// if more values with F ex. '2F' token then
				if(tokens[0].length() != 1)
					length = tokens[0].substring(0, tokens[0].length());
				// if only one token then
				else
					length = "1";
					
				lenInInt = Integer.parseInt(length);
				
				lenInInt = lenInInt * 4;
				l.length = lenInInt;
				// end of size
				
				// check divisibility by 8 of lc
				while(lc % 8 != 0){
					lc++;
					fw.write(" ------\n" + lc +"\t");
				}
				
				
				// put addr in litTable
				l.addr = lc;
				
				// put relocatable
				l.relocation = "R";
				
				
				lc = lc + lenInInt;
				
				fw.write(tokens[1]);
				
				
			}
		}
		
		
	}
	
	// return true if Symbol
	boolean processSecondParam(String arg) throws IOException{
		boolean res = true;
		
		if(arg.charAt(0) == '='){
			// Literal process
			res = false;
			
			insertLiteral(arg.substring(1));
			
		
		}
		else{
			// Symbol process
			processSymbol(arg);
		}
		return res;
		
	}
	
	// returns true if it is already in table
	boolean processSymbol(String arg) throws IOException{
		// Symbol operation start
		
		int positionOfSymbol = -1;
		boolean isSymbolInTable = false;
		// check if already exist in symbol table
		for(int i = 0 ; i < symTable.size() ; i++){
			if(symTable.get(i).sym_name.equalsIgnoreCase(arg)){
				positionOfSymbol = i;	
				isSymbolInTable = true;
			}
		}
		// end checking
		
		
		// if not in symbol table create new object
		if(!isSymbolInTable){
			SymbolTable s = new SymbolTable();
			s.sym_name = arg;
			
			symTable.add(s);
			
			positionOfSymbol = symTable.size() - 1;
			
		}
		// end new object
		
		// write $ to file
		
		fw.write("$" + positionOfSymbol);	// Symbol operation end
	
		return isSymbolInTable;
	}
	
}


// symbol table data
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


// Literal Table

class LiteralTable{
	
	String name;
	int addr;
	int length;
	String relocation;
	
	public LiteralTable(){
		this.addr = -1;
	}
	
	public LiteralTable(String n,int a, int l, String r){
		this.name = n;
		this.addr = a;
		this.length = l;
		this.relocation = r;
	}
}