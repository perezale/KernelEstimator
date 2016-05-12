package org.tte.logic.kernelestimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.util.Pair;

public class DescriptiveStats {
	
	public static List<Double[]> means(List<Read> readings){
		
		Map<Integer, List<Read>> data = readings.stream().collect(Collectors.groupingBy(Read::getDistancia));
		List<Double[]> means = new ArrayList<Double[]>();
		for(Integer distance : data.keySet().stream().sorted().collect(Collectors.toList())){
			List<Read> lecturas = data.get(distance);
			List<Double> collect = lecturas.stream().map(r -> new Double(r.getSignal())).collect(Collectors.toList());
			double[] arr = ArrayUtils.toPrimitive(collect.toArray(new Double[]{}));
			DescriptiveStatistics stats = new DescriptiveStatistics(arr);				
			means.add(new Double[]{stats.getMean(),stats.getStandardDeviation()});
		}
		
		return means;
	}
	
	public static List<Read> removeOutliersByDistance(List<Read> readings, double percentage){
		//https://gist.github.com/sushain97/6488296
		Map<Integer, List<Read>> data = readings.stream().collect(Collectors.groupingBy(Read::getDistancia));
		Map<Integer, List<Read>> output = new HashMap<Integer, List<Read>>();
		
		for(Integer distance : data.keySet().stream().sorted().collect(Collectors.toList())){
			List<Read> list = data.get(distance);
			Double[] collect = list.stream().map(r -> new Double(r.getSignal())).toArray(size -> new Double[size]);
			DescriptiveStatistics stats = new DescriptiveStatistics(ArrayUtils.toPrimitive(collect));
			double lowerRef = stats.getPercentile(percentage);
			double upperRef = stats.getPercentile(100-percentage);
			List<Read> filtered = list.stream().filter(lectura -> lectura.getSignal()>lowerRef && lectura.getSignal()<upperRef).collect(Collectors.toList());					
			output.put(distance, filtered);
		
		}
		
		return output.values().stream().flatMap(l -> l.stream()).collect(Collectors.toList());
	}
	
	public static List<Read> removeOutliers(List<Read> readings, double percentage){
		Double[] collect = readings.stream().map(r -> new Double(r.getSignal())).toArray(size -> new Double[size]);
		DescriptiveStatistics stats = new DescriptiveStatistics(ArrayUtils.toPrimitive(collect));
		double lowerRef = stats.getPercentile(percentage);
		double upperRef = stats.getPercentile(100-percentage);
		List<Read> filtered = readings.stream().filter(lectura -> lectura.getSignal()>lowerRef && lectura.getSignal()<upperRef).collect(Collectors.toList());					
		
		return filtered;
	}
	
	private static Pair<List<Integer>, List<Double>> calculateStats(List<Double> list)
	{
		double[] data = new double[list.size()];
		for(int i = 0; i < list.size(); i++)
			data[i] = list.get(i);
		DescriptiveStatistics dStats = new DescriptiveStatistics(data);
		
		List<Integer> summary = new ArrayList<Integer>(5);
		summary.add((int) dStats.getMin()); //Minimum
		summary.add((int) dStats.getPercentile(25)); //Lower Quartile (Q1)
		summary.add((int) dStats.getPercentile(50)); //Middle Quartile (Median - Q2)
		summary.add((int) dStats.getPercentile(75)); //High Quartile (Q3)
		summary.add((int) dStats.getMax()); //Maxiumum
		
		List<Double> outliers = new ArrayList<Double>();
		if(list.size() > 5 && dStats.getStandardDeviation() > 0) //Only remove outliers if relatively normal
		{
			double mean = dStats.getMean();
			double stDev = dStats.getStandardDeviation();
			NormalDistribution normalDistribution = new NormalDistribution(mean, stDev);
			
			Iterator<Double> listIterator = list.iterator();
			double significanceLevel = .50 / list.size();
			while(listIterator.hasNext())
			{
				double num = listIterator.next();
				double pValue = normalDistribution.cumulativeProbability(num);
				if(pValue < significanceLevel) //Chauvenet's Criterion for Outliers
				{
					outliers.add(num);
					listIterator.remove();
				}
			}
			
			if(list.size() != dStats.getN()) //If and only if outliers have been removed
			{
				double[] significantData = new double[list.size()];
				for(int i = 0; i < list.size(); i++)
					significantData[i] = list.get(i);
				dStats = new DescriptiveStatistics(significantData);
				summary.set(0, (int) dStats.getMin());
				summary.set(4, (int) dStats.getMax());
			}
		}
		
		return new Pair<List<Integer>,List<Double>>(summary, outliers);
	}
}
