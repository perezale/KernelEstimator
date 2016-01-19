package net.pladema.estimators;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import weka.estimators.KernelEstimator;

public class MainApp {
	
	private KernelEstimator estimator;

	public MainApp(){
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
	
	public static void run(List<Read> obs){
		MainApp estimator = new MainApp();
		for(Read observation : obs){			
			estimator.addValue(observation.getDistancia(),1);
		}		
		estimator.printEq();			
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
		
		List<Read> g50m = obs.stream().filter(r -> r.getSignal()>-60).collect(Collectors.toList());
		List<Read> g50to60 = obs.stream().filter(r -> r.getSignal()<=-60 && r.getSignal()>-65).collect(Collectors.toList());
		List<Read> g60to70 = obs.stream().filter(r -> r.getSignal()<=-65 && r.getSignal()>-70).collect(Collectors.toList());
		List<Read> g70to80 = obs.stream().filter(r -> r.getSignal()<=-70 && r.getSignal()>-75).collect(Collectors.toList());
		List<Read> g80l = obs.stream().filter(r -> r.getSignal()<=-75).collect(Collectors.toList());
		
		MainApp.run(g50m);
		MainApp.run(g50to60);
		MainApp.run(g60to70);
		MainApp.run(g70to80);		
		MainApp.run(g80l);
		
		
		/*
		for(Entry<Integer, List<Read>> x : grouped.entrySet()){
			System.out.println(x.getKey());
			for(Read observation : x.getValue()){
				System.out.println(observation.toString());
			}
		}
		*/		
	}

}
