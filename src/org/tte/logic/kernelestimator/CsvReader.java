package org.tte.logic.kernelestimator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.MappingIterator;
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
}
