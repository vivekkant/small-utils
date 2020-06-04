package org.weekendsoft.smallhacks;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;

public class FileNameSorter {
	
	private Options options = new Options();
	
	public static void main(String[] args) {
				
		FileNameSorter sorter = new FileNameSorter();
		sorter.driver(args);
		
	}
	
	private void driver(String[] args) {
		CommandLine cmd = null;	
		
		try {
			cmd = setAndParseOptions(args);
			File dir = validateArguments(cmd);
			String prefix = getPrefix(cmd);
			boolean rename = cmd.hasOption('r');
			
			Map<Integer, File> map = getSortedList(dir, prefix);
			if (rename) {
				System.out.println("Renaming files");
				renameSortedFiles(map);
			}
		} 
		catch (ParseException e) {
			System.out.println("Invalid options");
		} 
		catch (Exception e) {
			System.out.println(e.getMessage());
			printHelp();
		}		
	}
	
	private void renameSortedFiles(Map<Integer, File> map) {
		
		for (int index : map.keySet()) {
			String num = String.format("%04d", index);
			File file = map.get(index);
			String newfilename = num + " " + file.getName();
			System.out.println("Ranaming... " + newfilename);
			file.renameTo(new File(file.getParent(), newfilename));
		}
		
	};
	
	private Map<Integer, File> getSortedList(File dir, String prefix) {
		Map<Integer, File> map = new TreeMap<Integer, File>();
		String[] ext = new String[1];
		ext[0] = "mp3";
		
		@SuppressWarnings("unchecked")
		Iterator<File> iterator = FileUtils.iterateFiles(dir, ext, false);
		
		while(iterator.hasNext()) {
			File file = iterator.next();
			int index = getNumericIndexOfFile(prefix, file.getName());
			System.out.println("Reading... " + index + " "+ file.getName());
			map.put(index, file);
		}
		
		return map;
	}
	
	private int getNumericIndexOfFile(String prefix, String filename) throws NumberFormatException {
		int index = -1;
		
		int i = filename.indexOf(prefix) + prefix.length();
		StringBuffer numeric = new StringBuffer();
		while ("0123456789 ".indexOf(filename.charAt(i)) != -1 && i < filename.length()) {
			numeric.append(filename.charAt(i));
			i++;
		}
		
		index = Integer.parseInt(numeric.toString().trim());
		
		return index;
	}
	
	private  File validateArguments(CommandLine cmd) throws Exception {
		File dir = null;
		
		if (cmd.hasOption('h')) {
			printHelp();
			System.exit(0);
		}
		
		if (!cmd.hasOption('d') || !cmd.hasOption('p')) {
			throw new Exception("Invalid Options");		
		}

		dir = new File(cmd.getOptionValue('d'));
		if (!dir.exists()) {
			throw new Exception("Invalid directory");
		}
		
		return dir;
	}
	
	private String getPrefix(CommandLine cmd) throws Exception {

		String prefix = null;
		
		prefix = cmd.getOptionValue('p');
		if (prefix == null) throw new Exception("Invalid Prefix");
		
		return prefix;

	}
	
	private CommandLine setAndParseOptions(String[] args) throws ParseException {
		options.addOption("d", "dir", true, "Directory of files to be sorted");
		options.addOption("p", "prefix", true, "Prefix before the numeric value in the file");
		options.addOption("r", "rename", false, "If files are to be renamed");
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
