package org.tte.logic.kernelestimator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class OPDFKernels {

	List<Estimador> estimadores;
	List<Integer> rangos;
	
	
	public OPDFKernels(List<Pair<Integer,Double>> rangosConPeso){
		System.out.println("Construyendo OPDF...");
		
		estimadores = new ArrayList<Estimador>();
		rangos = new ArrayList<Integer>();
		
		String pathObs = this.getClass().getClassLoader().getResource("martin-ida.csv").getPath();
		
		List<Read> obsOriginal = CsvReader.load(pathObs,',');
		
		//List<Read> obs = DescriptiveStats.removeOutliers(obsOriginal, 2);
		List<Read> obs = DescriptiveStats.removeOutliersByDistance(obsOriginal, 2);
		
		//obs = new ArrayList<Read>(obsOriginal);
		
		//Imprime observaciones
		writeFile(obs);
		
		//printReadings(obs);		
		//printMeans(obs);		
		
		
		//Filtro de maxima señal en ventana de 10 elementos				
		obs = filterMax(obs, 3);
		//obs = filterMean(obs, 10);
				
		for(Pair<Integer, Double> rango : rangosConPeso){
			this.rangos.add(rango.getLeft());
			
			List<Read> lecturas = null;
			int indice = rangosConPeso.indexOf(rango);
			double weight = rango.getRight();
			//Primer conjunto, señales mayores al rango minimo
			if(indice == 0){
				lecturas = obs.stream().filter(r -> r.getSignal()>-rango.getLeft()).collect(Collectors.toList());
			}else {//rangos establecidos
				Pair<Integer, Double> rangoPrevio = rangosConPeso.get(rangosConPeso.indexOf(rango)-1);
				weight = rangoPrevio.getRight();
				lecturas = obs.stream().filter(r -> r.getSignal()<=-rangoPrevio.getLeft() && r.getSignal()>-rango.getLeft()).collect(Collectors.toList());				
			}
			estimadores.add(Estimador.run(lecturas, weight));
			 
			//Ultimo conjunto, señales menores al rango maximo
			if(indice == rangosConPeso.size()-1){
				lecturas = obs.stream().filter(r -> r.getSignal()<=-rango.getLeft()).collect(Collectors.toList());
				System.out.println("Cantidad de lecturas ultimo grupo: "+ lecturas.size());
				estimadores.add(Estimador.run(lecturas, rango.getRight()));
			}
						
		}
	}
	
	/**
	 * Agrupa observaciones en ventanas de tamaño windowSize y se queda con el maximo
	 * @param obs List of observations
	 * @param windowSize Window Size for processing
	 * @return List of max observations for each window 
	 */
	public List<Read> filterMax(List<Read> obs, int windowSize){		
		ArrayList<Read> obsMax = new ArrayList<Read>();				
		while(!obs.isEmpty()){			
			Read maxSignal = new Read();
			maxSignal.setSignal(-99);			
			for(int i = 0; i < Math.min(obs.size(), windowSize); i++){
				Read read = obs.get(i);
				if(read.getSignal() > maxSignal.getSignal()){
					maxSignal = read;					
				}
				
			}
			obs.subList(0, Math.min(obs.size(),windowSize)).clear();			
			obsMax.add(maxSignal);
		}	
		return obsMax;
	}
	
	/**
	 * Agrupa observaciones en ventanas de tamaño windowSize y se queda con la media
	 * @param obs List of observations
	 * @param windowSize Window Size for processing
	 * @return List of mean observations for each window 
	 */
	public List<Read> filterMean(List<Read> obs, int windowSize){		
		ArrayList<Read> obsAvg = new ArrayList<Read>();					
		while(!obs.isEmpty()){			
			List<Read> values = new ArrayList<Read>();					
			for(int i = 0; i < Math.min(obs.size(), windowSize); i++){
				values.add(obs.get(i));							
			}
			obs.subList(0, Math.min(obs.size(),windowSize)).clear();			
			OptionalDouble average = values.stream().mapToDouble(o -> new Double(o.getSignal())).average();
			Integer signal = new Double(average.getAsDouble()).intValue();
			List<Double> distancias = values.stream().map(o -> new Double(o.getDistancia())).collect(Collectors.toList());
			Mean mean = new Mean();
			Double mediana = new Double(mean.evaluate(ArrayUtils.toPrimitive(distancias.toArray(new Double[]{}))));
			Read read = new Read(values.get(0).getTiempo(),values.get(0).getLap(),signal,mediana.intValue());
			obsAvg.add(read);
		}	
		return obsAvg;
	}
	
	private void writeFile(List<Read> obs) {
		String filename = "resources/output.csv";			
		CsvReader.write(filename, ',', obs);		
	}

	private void printReadings(List<Read> obs) {
		for(Read r : obs){
			System.out.println(r.getTiempo()+","+r.getSignal()+","+r.getDistancia());
		}
	}
	
	private void printMeans(List<Read> obs) {
		List<Double[]> means = DescriptiveStats.means(obs);
		for(Double[] mean : means){
			System.out.println(mean[0]+","+mean[1]);
		}
		
	}

	public Estimador getEstimador(Integer signal){
		signal = Math.abs(signal);
		for(Integer rango : rangos){
			if(signal > rango) continue;
			return estimadores.get(rangos.indexOf(rango));
		}
		return estimadores.get(estimadores.size()-1);
	}
	
	public int nbEstimadores(){
		return estimadores.size();
	}	
	
	public List<Estimador> getEstimadores() {
		return estimadores;
	}

	public void setEstimadores(List<Estimador> estimadores) {
		this.estimadores = estimadores;
	}

	public static void main(String[] args){
		List<Pair<Integer,Double>> rangosConPeso = new ArrayList<Pair<Integer,Double>>();
		rangosConPeso.add(Pair.of(60, 0.4));
		rangosConPeso.add(Pair.of(65, 0.4));
		rangosConPeso.add(Pair.of(70, 0.4));
		rangosConPeso.add(Pair.of(75, 0.4));	
		
		OPDFKernels opdf = new OPDFKernels(rangosConPeso);
			
		
		//Para dibujar la PDF en excel		
		List<Estimador> lstEstimadores = opdf.getEstimadores();
		for(Estimador e : lstEstimadores){
			List<Double> values = new ArrayList<Double>();
			for(int i=0;i<100; i++){				
				values.add(e.getProbability(i));				
			}
			String arrStr = Arrays.toString(ArrayUtils.toPrimitive(values.toArray(new Double[]{})));
			System.out.println(arrStr.replace('[',' ').replace(']', ' '));
		}
		
	}
}
