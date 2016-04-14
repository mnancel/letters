package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.CharBuffer;

import clean.Cleaner;

public class TestReader {
	
	static final String FilesPath = "TestFiles";
	static final int NbCharacters = 5000;
	static final Cleaner commaCleaner = new Cleaner("([^\\.])([\\s\\.]+\\.)+", "$1.");
	
	public static void main(String[] args) {
		
		File[] files = new File(FilesPath).listFiles(
				new FilenameFilter() {
					
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".txt");
					}
				});
		
		CharBuffer buffer = CharBuffer.allocate(NbCharacters);
		BufferedReader reader;
		
		try {
			for (File f : files) {
				
				reader = new BufferedReader(new FileReader(f));
				
				reader.read(buffer);
				
				System.out.println("Buffer read. " + buffer.remaining());
				
				String content = commaCleaner.test(new String(buffer.array()));
				
				System.out.println(content);
				
				break;	// just checking for now
				
//				buffer.clear();
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
