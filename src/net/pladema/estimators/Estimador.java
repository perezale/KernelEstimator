package net.pladema.estimators;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import weka.estimators.KernelEstimator;

public class Estimador {
	
	private KernelEstimator estimator;

	public Estimador(){
		estimator = new KernelEstimator(1);		
		
	}
	
	public void addValue(double value, double weight){
		estimator.addValue(value,weight);
	}
	
	public void printEq(){
		//System.out.println("Kernels:"+ estimator.getNumKernels()+","+" StdDev:"+estimator.getStdDev());
		//System.out.println("Kernels "+Arrays.toString(estimator.getMeans())); 	
		
		double[] prob = new double[100];				
		
		String[] formatted = new String[100];
		DecimalFormatSymbols symbol = new DecimalFormatSymbols(Locale.getDefault());
		DecimalFormat df = new DecimalFormat("0.00000000000000000",symbol);		
		
		for(int x = 0; x < 100; x++){
			prob[x] = estimator.getProbability(x);
			formatted[x] = df.format(prob[x]);
		}
				
		System.out.println(Arrays.toString(formatted).replace(",","").replace("[","").replace("]",""));			
	}
	
	public double getProbability(double x){
		return estimator.getProbability(x);
	}
	
	public static Estimador run(List<Read> obs){
		Estimador estimator = new Estimador();
		for(Read observation : obs){			
			estimator.addValue(observation.getDistancia(),1);
		}		
		estimator.printEq();
		return estimator;
	}

	public static void main(String[] args) {
		List<Read> obs = CsvReader.load("martin-ida.csv",',');	
		
		ArrayList<Read> obsMax = new ArrayList<Read>();
		
		while(!obs.isEmpty()){			
			Read maxSignal = new Read();
			maxSignal.setSignal(-99);			
			for(int i = 0; i < Math.min(obs.size(), 10); i++){
				Read read = obs.get(i);
				if(read.getSignal() > maxSignal.getSignal()){
					maxSignal = read;					
				}
				
			}
			obs.subList(0, Math.min(obs.size(),10)).clear();			
			obsMax.add(maxSignal);
		}
		
		obs = obsMax;
		
		//Map<Integer, List<Read>> grouped = obs.stream().collect(Collectors.groupingBy(Read::getDistancia));
		//obs = obs.stream().filter(r -> r.getDistancia()==0).collect(Collectors.toList());
						
		//Filter by LAP
		//obs = obs.stream().filter(r -> r.getLap().contains("349e4a")).collect(Collectors.toList());		
		
		List<Read> g60m = obs.stream().filter(r -> r.getSignal()>-60).collect(Collectors.toList());
		List<Read> g60to65 = obs.stream().filter(r -> r.getSignal()<=-60 && r.getSignal()>-65).collect(Collectors.toList());
		List<Read> g65to70 = obs.stream().filter(r -> r.getSignal()<=-65 && r.getSignal()>-70).collect(Collectors.toList());
		List<Read> g70to75 = obs.stream().filter(r -> r.getSignal()<=-70 && r.getSignal()>-75).collect(Collectors.toList());
		List<Read> g75l = obs.stream().filter(r -> r.getSignal()<=-75).collect(Collectors.toList());
		
		Estimador estimador60oMayor = Estimador.run(g60m);
		Estimador estimador60a65 = Estimador.run(g60to65);
		Estimador estimador65a70 = Estimador.run(g65to70);
		Estimador estimador70a75 = Estimador.run(g70to75);		
		Estimador estimador75oMenor = Estimador.run(g75l);
		
		//EJEMPLO de  un paso de VITERBI para 1 deteccion de 3 monitores m1, m2, m3
		//  en un estado S3 (con distancias 55, 75 y 60 a los Monitores m1, m2 y m3)
		// Selecciono la curva según la señal
		// signal1 = 69;
		// signal2= 73;
				
		double probabilitym1 = estimador65a70.getProbability(55);
		double probabilitym2 = estimador70a75.getProbability(75);		
		double probabilitym3 = 1 - estimador75oMenor.getProbability(60);
				
		System.out.println(probabilitym1+"*"+probabilitym2+"*"+probabilitym3+"="+probabilitym1*probabilitym2*probabilitym3);
		
		
	}

	

}
