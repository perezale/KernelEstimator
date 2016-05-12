package org.tte.logic.kernelestimator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

public class CsvReader {

	public static List<Read> load(String filename, char c){
		File csvFile = new File(filename);
		
		List<Read> output = new ArrayList<Read>();
		CsvMapper mapper = new CsvMapper();			
		CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(c);
		
		try {
			MappingIterator<Read> it = mapper.readerFor(Read.class).with(schema).readValues(csvFile);
			while(it.hasNext()){
				output.add(it.next());
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		return output;				
	}
	
	public static boolean write(String filename, char c, List<Read> obs){
		File csvFile = new File(filename);
	
		String[] headerRow = new String[]{"Tiempo","LAP","Signal","Distancia"};
		
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(c);
		schema = CsvSchema.builder()
				.addColumn(headerRow[0])
				.addColumn(headerRow[1])
				.addColumn(headerRow[2])
				.addColumn(headerRow[3])
				.build();		
		
		
		try {
			SequenceWriter it = mapper.writer(schema).writeValues(csvFile);
			it.write(headerRow);
			it.writeAll(obs);
			it.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
				
		return true;				
	}
}
