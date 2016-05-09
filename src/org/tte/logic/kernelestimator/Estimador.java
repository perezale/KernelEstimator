package org.tte.logic.kernelestimator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

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
				
		//System.out.println(Arrays.toString(formatted).replace(",","").replace("[","").replace("]",""));			
	}
	
	public double getProbability(double x){
		return estimator.getProbability(x);
	}
	
	public static Estimador run(List<Read> obs, double weight){
		Estimador estimator = new Estimador();
		for(Read observation : obs){			
			estimator.addValue(observation.getDistancia(),weight);
		}		
		estimator.printEq();
		return estimator;
	}	

}
