package parsers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class cParserSemiLatest {

	File file;
	boolean commentBlock = false;
	String line;
	int countLOC = 0;
	HashMap<String, Integer> methodsLocDecl;
	

	private ArrayList<String> sourceLines = new ArrayList<String>();
	private ArrayList<String> parsedLines = new ArrayList<String>();
	private String[] varTypes = new String[]{
			"int", "unsigned int", "signed int", 
			"short int", "unsigned short int", "signed short int", 
			"long int", "unsigned long int", "signed long int",
			"double", "long double", 
			"char", "unsigned char", "signed char", 
			"string", 
			"bool", 
			"float", 
			"void"};

	public cParserSemiLatest(File file, HashMap<String, Integer> methodsLocDecl) {
		this.file = file;
		this.methodsLocDecl = methodsLocDecl;
	}

	private void getSourceToArray() {
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("#include") || line.contains("using namespace")) {
					sourceLines.add("");
					continue;
				}
				
				String[] split = line.split("//");
				if(split==null || split.length==0) {
					sourceLines.add("");
					continue;
				}
				sourceLines.add(split[0]);
			}
		} catch (IOException ex) {
			Logger.getLogger(cParserSemiLatest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void parse() {
		sourceLines = new ArrayList<String>();
		parsedLines = new ArrayList<String>();
		getSourceToArray();
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			boolean methodDel = false;
			ArrayList<String> methodParam = new ArrayList<>();
			String methodToPrint = "";

			int indx = -1;

			while ((line = br.readLine()) != null) {
				indx++;
				if (line.trim().equals("")) {
					continue;
				}
				if (!isCommentLine(line.trim()) && !commentBlock) {
					countLOC++;
					line = line.trim().replace(" +", " ");

					// Check if is line of Method start
					for (String str : methodsLocDecl.keySet()) {
						if (methodsLocDecl.get(str) == countLOC) {
							if (str.trim().contains(" ")) {
								str = str.trim().replace(" +", " ");
								methodToPrint = "Method:" + str.split(" ")[(str.split(" ").length - 1)].trim() + "(";
							} else {
								methodToPrint = "Method:" + str + "(";
							}
							line = line.replace(str, "").trim().substring(1).trim();

							// System.out.println("-----METHOD LINE "+indx+":"+line);
							methodDel = true;
							break;
						}
					}

					// For everything else Use "usage:"
					if (!methodDel) {
						int possitionOfend = line.trim().indexOf(";");
						if (possitionOfend < line.trim().indexOf("//")) {
							if (possitionOfend == -1) {
								possitionOfend = 0;
							}
							line = line.substring(0, possitionOfend);
						}
						this.lineInterp(indx); // ***TEST

					}
					// For Method declaration
					else {
						// Method declaration get parameters and types
						if (line.length() > 1) {
							line = line.trim().replaceAll(" +", " ");
							if (line.charAt(0) != ')') {
								int index = line.indexOf(")");
								if(index == -1) {
									index = line.length()-1;
								}
								String args = line.substring(0, index);
								String[] var = args.split(",");
								for (String str : var) {
									String[] var1 = str.trim().split(" ");
									if (var1[0].equals("const")) {
										methodToPrint = methodToPrint + "" + var1[1].replace("*", "").replace("&", "")
												+ ",";
									} else {
										methodToPrint = methodToPrint + "" + var1[0].replace("*", "").replace("&", "")
												+ ",";
									}
									int position = 1;
									if (var1.length>position && var1[var1.length - 1].replace("*", "").replace("&", "").equals("{")) {
										position++;
									}
									
									if (var1.length>position && var1[var1.length - position].replace("*", "").replace("&", "").equals(")")) {
										position++;
									}
									methodParam.add(var1[var1.length - 1].replace("*", "").replace("&", ""));
								}
							}
						}
						// Method declaration finish
						if (line.contains("{")) {
							methodDel = false;
							if (methodToPrint.contains(",")) {
								parsedLines.add(methodToPrint.substring(0, methodToPrint.length() - 1) + ");");
							} else {
								parsedLines.add(methodToPrint + ");");
							}
							for (String str : methodParam) {
								if (str.contains(")")) {
									str = str.replace(")", "");
								}
								parsedLines.add("parameter#" + str + ";");
							}
							methodToPrint = "";
							methodParam.clear();
						}
					}
				} else if (commentBlock) { 
					stopCommentBlock(line);
				}

			}

			cleanParsedLines();

			//utils.Utilities.writeCSV("./" + file.getName() + "_parsed.txt", this.parsedLines, false);	//auto xrisimopoiitai gia analiseis, kai se auto enonontai ta mikra IF, an treksei o katallilos kwdikas
			BufferedWriter writer= new BufferedWriter(new FileWriter(file.getName()+"_parsed.txt", false));
                        writer.write("");
			for(String st: this.parsedLines) {
                            writer.append(st+ System.lineSeparator());
			}
                        writer.close();
                        
			//utils.Utilities.writeCSV("./" + file.getName() + "_original_parsed.txt", this.parsedLines, false);	//arxeio pou den allazei to _parsed arxeio. Edw ta IF paramenoun opws itan. Axristo stin periptwsi pou den enosoume ta IF

		} catch (IOException ex) {
			Logger.getLogger(cParserSemiLatest.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void cleanParsedLines() {
		for (int i = 0; i < this.parsedLines.size(); i++) {
			this.parsedLines.set(i, this.parsedLines.get(i).replaceAll(";;", ";"));
		}
	}

	private void splitLine(String line, int id) {	//kaleitai sto telos tis analisis kathe grammis
		String newLine = replaceWithSpaces(line.trim());
		newLine = removeVariableType(newLine.trim());
		
		String[] spl = newLine.split(" ");
		for (String tmp : spl) {

			if (tmp == null || tmp.trim().length() == 0) {
				continue;
			}

			if (tmp.equals("true")) {
				continue;
			} else if (tmp.equals("false")) {
				continue;
			} else {
				try { // in case tmp is a number, we dont need that to be written
					double zzz = Double.parseDouble(tmp);
					continue;
				} catch (NumberFormatException exc) {
				}
			}

			if (tmp.contains(".")) {
				String[] spl2 = tmp.split("\\.");
				if(spl2.length>0) {
					if(!spl2[0].equals(".")) {
						this.parsedLines.add("Usage#" + spl2[0] + "#" + (id + 1) + ";");
					}
					this.parsedLines.add("Invocation#" + tmp + "#" + (id + 1) + ";");
					continue;
				}
			}
			this.parsedLines.add("Usage#" + tmp + "#" + (id + 1) + ";");
		}

	}

	private boolean isGroupCommand(String line) {
		String ln = this.replaceWithSpaces(line).trim();

		if(ln.startsWith("if") || ln.startsWith("else") 
				|| ln.startsWith("for") || ln.startsWith("while") || ln.startsWith("do") 
				|| ln.startsWith("switch") || ln.startsWith("try")) {
			return true;
		}
		return false;
	}
	
	private int findClosingBracket(int id, String command) {	//vriskei pou kleinei kapoia agkili '{'
		int idEnd = id;
		int bal = 0;
		boolean started = false;

		boolean isIf= (command.equals("if"));
		boolean isFor= (command.equals("for"));
		
		for (int i = id; i < this.sourceLines.size(); i++) {
			if(!started && isIf && !isFor) {
				if(!started && !this.sourceLines.get(i).contains("{") && this.sourceLines.get(i).contains(";")) {
					return i;
				}
				if(i>id && isGroupCommand(this.sourceLines.get(i))) {
					return i;
				}
			}
			
			if(isFor) {
				if(i==id) {
					String ln = this.sourceLines.get(i);
					int semicols = 0;
					char ch = 59;
					for(int c=0; c<ln.length(); c++) {
						if(ln.charAt(c)==ch) {
							semicols++;
						}
					}
					if(semicols>3) {
						return i;
					}
				}
				if(i>id && !started && !this.sourceLines.get(i).contains("{") && this.sourceLines.get(i).contains(";")) {
					return i;
				}
			}
			
			
			
			if (this.sourceLines.get(i).contains("{")) {
				String[] split = (this.sourceLines.get(i).split("\\{"));

				if (split.length > 1) {
					bal += split.length - 1;
				} else {
					bal++;
				}
			}


			if (!started && bal > 0) {
				started = true	;
			}

			if (this.sourceLines.get(i).contains("}") && started) {
				String[] split = (this.sourceLines.get(i).split("\\}"));

				if (split.length > 1) {
					bal -= split.length - 1;
				} else {
					bal--;
				}
			}

			if (started && bal <= 0) {
				idEnd = i;
				break;
			}
		}
		if (id == idEnd) {
			return idEnd;
		}


		return idEnd;
	}

	private int hasElse(int id, boolean isElse) {
		int ret = id;
		
		String line = this.sourceLines.get(id);
		
		if(isElse) {
			line = this.sourceLines.get(id+1);
		}
		
		line = line.trim();
		
		int count = 0;
		ArrayList<String> lines = new ArrayList<String>();
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		int i=1;
		while(count < 2) {	//add the next two valid lines to Array, to check for an else statement
			if((id+i)>=this.sourceLines.size()) {
				break;//return id;
			}
			if(!(line.startsWith("//") || line.startsWith("/*") || line.startsWith("*") || line.startsWith("*/"))) {
				lines.add(line);
				ids.add(id+i);
				count++;
			}
			i++;
			if((id+i)>=this.sourceLines.size()) {
				break;
			}
			line = this.sourceLines.get(id+i);
			line = line.trim();
		}
		
		for(int k=0; k<lines.size(); k++) {
			//System.out.println("LINE: "+lines.get(k));
			String tmpLine = this.replaceWithSpaces(lines.get(k)).trim();
			if(tmpLine.startsWith("else")) {
				ret = this.findClosingBracket(ids.get(k), "if");
				ret = hasElse(ret, true);
				return ret;
			}
		}
		
		return ret;
	}
	
	private void ifLine(int id) {
		this.parsedLines.add("BEGIN_IF#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "if");
		
		int newIdEnd = hasElse(idEnd, false);
		if(newIdEnd!=idEnd) {
			idEnd = newIdEnd;
		}

		this.parsedLines.add("END_IF#" + (idEnd + 1) + ";");

		String line = this.sourceLines.get(id).replaceFirst("if", "");

		//line = this.replaceWithSpaces(line);
		splitLine(line, id);
	}

	private void elseLine(int id) { // TODO san ton allo parser

		this.parsedLines.add("BEGIN_ELSE#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "if");

		//this.parsedLines.add("END_IF#" + (idEnd + 1) + ";");

		String line = this.sourceLines.get(id).replaceFirst("else", "");
		line = line.trim();
		line = line.replaceFirst("if", "");

		//line = this.replaceWithSpaces(line).trim();
		splitLine(line, id);
	}

	private void forLine(int id) {
		this.parsedLines.add("BEGIN_FOR#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "for");

		this.parsedLines.add("END_FOR#" + (idEnd + 1) + ";");

		String line = this.sourceLines.get(id).replaceFirst("for", "");

		//line = this.replaceWithSpaces(line);
		splitLine(line, id);
	}

	private void whileLine(int id) {
		this.parsedLines.add("BEGIN_WHILE#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "");

		this.parsedLines.add("END_WHILE#" + (idEnd + 1) + ";");

		String line = this.sourceLines.get(id).replaceFirst("while", "");

		//line = this.replaceWithSpaces(line);
		splitLine(line, id);
	}
	
	String switchVar = "";
	int idWipe=0;
	
	private void switchLine(int id) {
		this.parsedLines.add("BEGIN_SWITCH#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "");
		idWipe = idEnd;
		this.parsedLines.add("END_SWITCH#" + (idEnd + 1) + ";");

		String line = this.sourceLines.get(id).replaceFirst("switch", "");
		line = this.replaceWithSpaces(line).trim();
		switchVar = line.replace("\\{", "");
		
		splitLine(line, id);
	}
	
	private void caseLine(int id) {
		this.parsedLines.add("BEGIN_CASE#" + (id + 1) + ";");
		this.parsedLines.add("Invocation-IF#"+switchVar+"#" + (id + 1) + ";");
		
	}
	
	private void breakLine(int id) {
		this.parsedLines.add("Break#null#" + (id + 1) + ";");
		
	}

	private void tryLine(int id) {
		this.parsedLines.add("BEGIN_TRY#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "");
		idEnd = findClosingBracket(idEnd, "");

		this.parsedLines.add("END_TRY#" + (idEnd + 1) + ";");

		String line = this.sourceLines.get(id).replaceFirst("for", "");

		//line = this.replaceWithSpaces(line);
		splitLine(line, id);
	}	
	
	private void catchLine(int id) {
		this.parsedLines.add("BEGIN_CATCH#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "");

		this.parsedLines.add("END_CATCH#" + (idEnd + 1) + ";");

		String line = this.sourceLines.get(id).replaceFirst("catch", "");

		//line = this.replaceWithSpaces(line);
		splitLine(line, id);
	}
	
	private void doLine(int id) {
		//BEGIN_DO#351;
		//END_DO#353;
		this.parsedLines.add("BEGIN_DO#" + (id + 1) + ";");

		int idEnd = findClosingBracket(id, "");

		this.parsedLines.add("END_DO#" + (idEnd + 1) + ";");

	}
	
	private void endDoLine(int id) {
		String line = this.sourceLines.get(id).replaceFirst("while", "");

		//line = this.replaceWithSpaces(line);
		splitLine(line, id);
	}
	
	private void lineInterp(int id) {	//kathe grammi ksekinaei tin analisi tis edw

		String line = sourceLines.get(id).trim();
		if(line.contains("\"")) {
			//System.out.println("OLD: "+line);
			line = replaceCodeStringsWithType(line);
			sourceLines.set(id, line);
			//System.out.println("NEW: "+line);
		}
		
		if (line.startsWith("//")) {
			return;
		}
		line = this.replaceWithSpaces(line).replaceFirst("\\s*", "");
		if (line.startsWith("if ")) {
			//System.out.println("-------------------------------IF LINE");
			ifLine(id);
		} else if (line.startsWith("else if ") || line.startsWith("else ") || line.equals("else")) {
			//System.out.println("-------------------------------ELSE LINE");
			elseLine(id);
		} else if (line.startsWith("for ")) {
			//System.out.println("-------------------------------FOR LINE");
			forLine(id);
		} else if ((line.startsWith("while ") || sourceLines.get(id).trim().startsWith("} while") || sourceLines.get(id).trim().startsWith("}while")) && sourceLines.get(id).contains(";") && sourceLines.get(id).contains("}")) {
			//System.out.println("-------------------------------FOR LINE");
			endDoLine(id);
		}   else if (line.startsWith("while ")) {
			//System.out.println("-------------------------------WHILE LINE");
			whileLine(id);
		} else if (line.startsWith("switch ")) {
			if(idWipe<=id) {
				idWipe = 0;
				switchVar = "";
			}
			//System.out.println("-------------------------------SWITCH LINE");
			switchLine(id);
		}  else if (line.startsWith("case ") || line.startsWith("default ")) {
			//System.out.println("-------------------------------CASE LINE");
			if(idWipe<=id) {
				idWipe = 0;
				switchVar = "";
			}
			caseLine(id);
		} else if (line.startsWith("break ")) {
			if(idWipe<=id) {
				idWipe = 0;
				switchVar = "";
			}
			breakLine(id);
		}  else if (line.startsWith("try ") || line.equals("try")) {
			//System.out.println("-------------------------------TRY LINE");
			tryLine(id);
		}  else if (line.startsWith("catch ") || line.equals("catch")) {
			//System.out.println("-------------------------------CATCH LINE");
			catchLine(id);
		}  else if (line.startsWith("do ") || line.equals("do")) {
			//System.out.println("-------------------------------DO LINE");
			doLine(id);
		} else {
			line = this.replaceWithSpaces(line);
			splitLine(sourceLines.get(id), id);
		}

	}
	
	public String replaceCodeStringsWithType(String line) {	
		
		line = line.trim();
		
		if(line.startsWith("\\")) {
			line = " "+line;
		}
		String[] split0 = line.split("\\\\");
		line = "";
		 for(String spl0 : split0) {
			 if(spl0.startsWith("\"")) {
				 line += spl0.replaceFirst("\"", "backslash_quote");
			 }else {
				 line += spl0;
			 }
		 }
		
		if(!line.contains("\"")) {
			return line;
		}
		
		boolean flag = false;
		if(line.startsWith("\"")) {
			flag = true;
		}
		String[] split = line.split("\"");
		String ret = "";
		int i=0;
		
		while(i<split.length) {
			if(flag) {
				flag= false;
			}else {
				ret += split[i];
				flag = true;
			}
			i++;
		}
		ret = ret.trim();
		return ret;
	}
	
	private String removeVariableType(String line) {
		line.trim();
		for(String type : varTypes) {
			if(line.startsWith(type+" ")) {
				
				line = line.replaceFirst(type, "");
				return line.trim();
			}
		}
		return line;
	}

	private String replaceWithSpaces(String line) {
		line = line.replaceAll("\\+", " ");
		line = line.replaceAll("-", " ");
		line = line.replaceAll("\\(", " ");
		line = line.replaceAll("\\)", " ");
		line = line.replaceAll("\\*", " ");
		line = line.replaceAll("/", " ");
		line = line.replaceAll("%", " ");
		line = line.replaceAll("=", " ");
		line = line.replaceAll("&", " ");
		line = line.replaceAll("::", " ");
		line = line.replaceAll(":", " ");
		line = line.replaceAll(";", " ");
		line = line.replaceAll("\\[", " ");
		line = line.replaceAll("\\]", " ");
		line = line.replaceAll("\\{", " ");
		line = line.replaceAll("\\}", " ");
		line = line.replaceAll(",", " ");
		line = line.replaceAll(">", " ");
		line = line.replaceAll("<", " ");
		line = line.replaceAll("\"", " ");
		line = line.replaceAll("'", " ");
		line = line.replaceAll("^", " ");
		line = line.replaceAll("!", " ");
		line = line.replaceAll("\\?", " ");
		line = line.replaceAll("\\|\\|", " ");
		line = line.replaceAll("\\|", " ");
		line = line.replaceAll("^", " ");
		
		return line;
	}

	private boolean isCommentLine(String word) {
		if (word.length() > 1) {
			if (word.charAt(0) == '#' || word.charAt(1) == '#') {
				if (word.contains("include")) {
					countLOC++;
				}
				return true;
			}
			if (word.contains("printf(")) {
				countLOC++;
				return true;
			}
			if (word.contains("/*")) {// .substring(0, 2).equals("/*") ){
				String lastTwo = word.substring(word.length() - 2);
				if (lastTwo.equals("*/")) {
					return true;
				} else if (word.contains("*//*")) {
					commentBlock = true;
					return true;
				} else if (word.contains("*/")) {
					String[] newLine = word.split("\\*/");
					this.line = newLine[1];
					return false;
				}
				commentBlock = true;
				return true;
			}
			return (word.substring(0, 2).equals("//"));
		}
		return false;
	}

	private void stopCommentBlock(String line) {
		if (line.contains("*/")) {
			commentBlock = false;
			if (line.contains("*//*")) {
				commentBlock = true;
			}
		}
	}

	public boolean isNumeric(String str) {
		return (// str.chars().allMatch(Character::isDigit) ||
				// Pattern.matches("([0-9]*)\\.([0-9]*)", str) ||
		Pattern.matches("([0-9]*)", str.charAt(0) + ""));
	}
}
