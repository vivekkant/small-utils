package org.weekendsoft.smallhacks.stockquote;

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

public class StockQuoteUtil {
	
	private Options options = new Options();

	public static void main(String[] args) {
		
		StockQuoteUtil navutil = new StockQuoteUtil();
		navutil.driver(args);
	}
	
	private void driver(String[] args) {
		CommandLine cmd = null;	
		
		try {
			cmd = setAndParseOptions(args);
			
			String[] symbols = parseSymbols(cmd);
			System.out.println("Input symbols... " + Arrays.toString(symbols));
			
			YahooFinanceQuoteDownloader loader = new YahooFinanceQuoteDownloader();
			Map<String, Quote> quotes = loader.downloadQuotes(symbols);
			System.out.println("Total " + symbols.length + " Symbols downloaded... ");
			
			PrintStream out = getOutput(cmd);
			
			for(String symbol : symbols) {
				Quote quote = quotes.get(symbol);
				if (quote == null) {
					System.out.println("No nav available for symbol " + symbol);
				}
				else {
					StringBuilder quoteString = new StringBuilder();
					quoteString.append(quote.getSymbol()).append(',');
					quoteString.append(quote.getLongName()).append(',');
					quoteString.append(quote.getRegularMarketPrice());
					
					out.println(quoteString.toString());
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
	
	private String[] parseSymbols(CommandLine cmd) throws Exception {
		
		String symbolsString = null;
		String[] symbols = null;
		
		symbolsString = cmd.getOptionValue('c');
		if (symbolsString == null || "".equals(symbolsString)) throw new Exception("No valid code passed");
		
		if (symbolsString.indexOf(',') == -1) {
			symbols = new String[1];
			symbols[0] = symbolsString.trim();
		}
		else {
			symbols = symbolsString.split(",");

		}
		
		return symbols;
		
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
		options.addOption("c", "codes", true, "Comma seperated list of Yahoo Finance Stock Symbols");
		options.addOption("o", "outfile", true, "Output file name with path, default will be nav.csv");
		options.addOption("h", "help", false, "Help");		
		
		CommandLineParser 	parser = new DefaultParser();
		CommandLine 		cmd = parser.parse( options, args);
		
		return cmd;

	}
	
	
	private void printHelp() {
	     HelpFormatter formatter = new HelpFormatter();
	      formatter.printHelp("StockQuoteUtil", options);
	}

}
