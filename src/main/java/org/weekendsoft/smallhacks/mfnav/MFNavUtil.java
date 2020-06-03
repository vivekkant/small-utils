package org.weekendsoft.smallhacks.mfnav;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MFNavUtil {
	
	private Options options = new Options();

	public static void main(String[] args) {
		
		MFNavUtil navutil = new MFNavUtil();
		navutil.driver(args);
	}
	
	private void driver(String[] args) {
		CommandLine cmd = null;	
		
		try {
			cmd = setAndParseOptions(args);
			
			int[] codes = parseCodes(cmd);
			System.out.println("Input codes codes... " + Arrays.toString(codes));
			
			AMFINavDownloader loader = new AMFINavDownloader();
			Map<Integer, Nav> navs = loader.downloadNavs();
			System.out.println("Total " + navs.size() + " Navs downloaded... ");
			
			PrintStream out = getOutput(cmd);
			
			for(int code : codes) {
				Nav nav = navs.get(code);
				if (nav == null) {
					System.out.println("No nav available for code " + code);
				}
				else {
					StringBuilder navString = new StringBuilder();
					navString.append(nav.getCode()).append(',');
					navString.append(nav.getName()).append(',');
					navString.append(nav.getNav());
					
					out.println(navString.toString());
				}
			}
			out.close();

		} 
		catch (ParseException e) {
			System.out.println("Invalid options");
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			printHelp();
		}		
	}
	
	private int[] parseCodes(CommandLine cmd) throws Exception {
		
		String codeString = null;
		int[] codes = null;
		
		codeString = cmd.getOptionValue('c');
		if (codeString == null || "".equals(codeString)) throw new Exception("No valid code passed");
		
		if (codeString.indexOf(',') == -1) {
    		try {
				codes = new int[1];
				codes[0] = Integer.parseInt(codeString.trim());
			} catch (NumberFormatException e) {
				throw new Exception("Invalid codes format : " + codeString);
			}
		}
		else {
			String[] codeStringArray = codeString.split(",");
			codes = new int[codeStringArray.length];
			for (int i = 0; i < codeStringArray.length; i++) {
	    		try {
					codes[i] = Integer.parseInt(codeStringArray[i].trim());
				} catch (NumberFormatException e) {
					throw new Exception("Invalid codes format : " + codeStringArray[i].trim());
				}
			}
		}
		
		return codes;
		
	}
	
	private PrintStream getOutput(CommandLine cmd) throws Exception {
		
		PrintStream out = null;
		String outfilepath = cmd.getOptionValue('o');
		
		if (outfilepath == null) {
			out = System.out;
		}
		else {
			File outfile = new File(outfilepath);
			if (outfile.isDirectory()) {
				outfile = new File(outfile, "nav.csv");
			}
			
			System.out.println("Writing to " + outfile.toString());
			out = new PrintStream(new FileOutputStream(outfile));
		}
		
		return out;
	}
	
	private CommandLine setAndParseOptions(String[] args) throws ParseException {
		options.addOption("c", "codes", true, "Comma seperated list of AMFI codes");
		options.addOption("o", "outfile", true, "Output file name with path, default will be nav.csv");
		options.addOption("h", "help", false, "Help");		
		
		CommandLineParser 	parser = new DefaultParser();
		CommandLine 		cmd = parser.parse( options, args);
		
		return cmd;

	}
	
	
	private void printHelp() {
	     HelpFormatter formatter = new HelpFormatter();
	      formatter.printHelp("FileNameSorter", options);
	}

}
