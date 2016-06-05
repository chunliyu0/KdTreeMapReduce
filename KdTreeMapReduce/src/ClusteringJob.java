import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class ClusteringJob {
	public static enum COUNTERS {
	    LEAF_SIZE,
	    JOB_DONE,
	    ITERATION_COUNT
	}
	public static Random RND_GEN = new Random(System.currentTimeMillis());
	public static String SPLIT_DIM = "SPLIT";
	public static String SPLIT_FLAG = "SPLIT_FLAG";
	public static String MAX_LEAF_CAP = "MAX_LEAF_CAP";
	public static String L_TRUE = "TRUE";
	public static String L_FALSE = "FALSE";
	public static String JOB_DONE = "JOBDONE";
	//public static int KMEANS_PARTITION = 4;
	public static int DATA_DIMENSION = 2;
	public static String KMEANS_ITERATION = "ITERATION";
	//public static int MAX_LEAF_SIZE = 50;
	public static int MAX_LEAF_SIZE = 60;

	public static Job CreateJob(Configuration conf) throws IOException
	{
		Job job = new Job(conf);
		FileSystem fs = FileSystem.get(conf);
		fs.setVerifyChecksum(false);
		job.setInputFormatClass(org.apache.hadoop.mapreduce.lib.input.TextInputFormat.class);
		job.getConfiguration().set("mapred.mapper.new-api", "true");
		job.getConfiguration().set("mapred.map.tasks", "3");
		job.getConfiguration().set("mapred.reducer.new-api", "true");
		job.setPartitionerClass(org.apache.hadoop.mapreduce.lib.partition.HashPartitioner.class);
		job.getConfiguration().set("mapred.reduce.tasks", "2");
		job.setOutputFormatClass(org.apache.hadoop.mapreduce.lib.output.TextOutputFormat.class);
		
		job.setMapOutputKeyClass(org.apache.hadoop.io.Text.class);
		job.setMapOutputValueClass(org.apache.hadoop.io.Text.class);

		job.setOutputKeyClass(org.apache.hadoop.io.Text.class);
		job.setOutputValueClass(org.apache.hadoop.io.Text.class);
		
		job.setJarByClass(ClusteringJob.class);

		return job;
	}
	///---------------
	static String datasetFileName =  "/bigballs4.txt";
	static String centroidsSeed = "./data/bigcentroids.txt";
	static int nCluster = 4;
//	static String datasetFileName =  "/ballkk.txt";
//	static String centroidsSeed = "./data/centroids.txt";
//	static int nCluster = 3;
	/////
	static String originalPoints = "./data/back" + datasetFileName;
	
	static String kmeansInputPath = "./data/kmeans.txt";
	static String kdtreeOutputPath = "./output";
	static String kmeansOutputPath = "./kmeansoutput";
	static String pointsInputPath = "./data" + datasetFileName;
	static String simpleKMeansOutputPath = "./simpleoutput";
	static String simpleKMeansCentroids = "./tmp/tmpcen.txt";

	static ArrayList<double[]> readPoints(String fileName, Configuration conf) throws IOException
	{
    	ArrayList<double[]> centroids = new ArrayList<double[]>();
		FileSystem fs = FileSystem.get(conf);
		fs.setVerifyChecksum(false);
        Path pt=new Path(fileName);
        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
        String line;
        line = br.readLine();	
        String[] pair;
      
        
        while (line != null) {
            //System.out.println(line);            
            pair = line.split(",");
            double[] ncen = new double[ClusteringJob.DATA_DIMENSION];
            ncen[0] = Double.parseDouble(pair[0]);
            ncen[1] = Double.parseDouble(pair[1]);
            centroids.add(ncen);
            line = br.readLine();

        }
        br.close();
        return centroids;
	}
	static double computeCenDiff(String cen1, String cen2, Configuration conf) throws IOException
	{
		ArrayList<double[]> c1 = readPoints(cen1, conf);
		ArrayList<double[]> c2 = readPoints(cen2, conf);
		double ret = 0;
		int len = Integer.min(c1.size(), c2.size());
		for(int i = 0; i < len; i++)
		{
			ret += KMeans.Distance(c1.get(i), c2.get(i));
		}
		return ret;
	}
	public static long GetIO(Job job) throws IOException
	{
		long io = 0;
		io += job.getCounters().findCounter("FileSystemCounters", "FILE_BYTES_READ").getValue();
		io += job.getCounters().findCounter("FileSystemCounters", "FILE_BYTES_WRITTEN").getValue();
		return io;
	}
	static void Reset(Configuration conf) throws IOException
	{
        FileSystem fs = FileSystem.get(conf);
        Path original = new Path(originalPoints);
        Path input = new Path(pointsInputPath);
    	FileUtil.copy(fs, original, fs, input, false, conf);

	}
	public static long StartKDTreeJob() throws IOException, ClassNotFoundException, InterruptedException
	{
		long leafsize = Integer.MAX_VALUE;
		long maxleafsize = MAX_LEAF_SIZE;
		int current_dim = 0;
		int total_dim = DATA_DIMENSION;
		String kdtreeInputPath = pointsInputPath;
		long IOCounter = 0;
		Configuration conf = new Configuration();
		////////
		Reset(conf);
		//////////////
		while(true) {
	        System.out.println("Job:");
	        System.out.println("Current Dim: " + current_dim);
	        System.out.println("Previous Leaf Size:" + leafsize);
	        
	        conf.set(SPLIT_DIM, Integer.toString(current_dim));
	        current_dim = (current_dim + 1) % total_dim;
	        if(leafsize <= maxleafsize)
	        	conf.set(SPLIT_FLAG, L_FALSE);
	        else
	        	conf.set(SPLIT_FLAG, L_TRUE);
	        conf.set(MAX_LEAF_CAP, Long.toString(maxleafsize));
			Job KdTreeJob = CreateJob(conf);
			KdTreeJob.setReducerClass(KdTreeReducer.class);
			KdTreeJob.setMapperClass(KdTreeMapper.class);	
			
			/* This is an example of how to set input and output. */
			Path inputFilePath = new Path(kdtreeInputPath);
			Path outputPath = new Path(kdtreeOutputPath);
			FileInputFormat.setInputPaths(KdTreeJob, inputFilePath);
			FileOutputFormat.setOutputPath(KdTreeJob, outputPath);
	        FileSystem fs = FileSystem.get(conf);
	        FileUtil fu = new FileUtil();
	        Path outputFilePath = new Path("./output/part-r-00000");
	        Path inter_inputPath = new Path(kdtreeInputPath);
	        
	        //KdTreeJob.set
	        if (fs.exists(outputPath)) {
	            fs.delete(outputPath, true);
	        }
	
			KdTreeJob.submit();
	
			KdTreeJob.waitForCompletion(true);
			
			
			IOCounter = GetIO(KdTreeJob);
        	if(KdTreeJob.getCounters().findCounter(COUNTERS.JOB_DONE).getValue() == 1){
        		break;
        	}
	        if(leafsize <= maxleafsize){
	        	Path kmeansinput = new Path(kmeansInputPath);
	        	FileUtil.copy(fs, outputFilePath, fs, kmeansinput, false, conf);
	        	break;
	        }
	        else
	        	FileUtil.copy(fs, outputFilePath, fs, inter_inputPath, false, conf);

			leafsize 
				= KdTreeJob.getCounters().findCounter(COUNTERS.LEAF_SIZE).getValue();
			System.out.println(leafsize);
			//System.in.read();
		}	
		return IOCounter;
	}
	
	public static long StartKMeansJob() throws IOException, ClassNotFoundException, InterruptedException
	{
		Configuration conf = new Configuration();
		
		Job KMeansJob = CreateJob(conf);
		KMeansJob.setReducerClass(KMeansReducer.class);
		KMeansJob.setMapperClass(KMeansMapper.class);

		Path inputFilePath = new Path(kmeansInputPath);
		Path outputPath = new Path(kmeansOutputPath);
		FileInputFormat.setInputPaths(KMeansJob, inputFilePath);
		FileOutputFormat.setOutputPath(KMeansJob, outputPath);
        
		FileSystem fs = FileSystem.get(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }
		KMeansJob.submit();
		
		KMeansJob.waitForCompletion(true);
		
		return GetIO(KMeansJob);
	}
	public static long StartSimpleKMeansJob() throws IOException, ClassNotFoundException, InterruptedException
	{
		Configuration conf = new Configuration();
		FileSystem fs = FileSystem.get(conf);

		int iteration = 0;
		long IOCounter = 0;
		Path seedCen = new Path(centroidsSeed);
		Path tmpCen = new Path(simpleKMeansCentroids);
    	FileUtil.copy(fs, seedCen, fs, tmpCen, false, conf);

		for(iteration = 0; iteration < 200; iteration++)
		{
			//Configuration conf = new Configuration();

			Job SimpleKMeansJob = CreateJob(conf);
			SimpleKMeansJob.setReducerClass(SimpleKMeansReducer.class);
			SimpleKMeansJob.setMapperClass(SimpleKMeansMapper.class);
			
			Path inputFilePath = new Path(pointsInputPath);
			Path outputPath = new Path(simpleKMeansOutputPath);
			FileInputFormat.setInputPaths(SimpleKMeansJob, inputFilePath);
			FileOutputFormat.setOutputPath(SimpleKMeansJob, outputPath);
			
	        if (fs.exists(outputPath)) {
	            fs.delete(outputPath, true);
	        }
	        SimpleKMeansJob.submit();
			
	        SimpleKMeansJob.waitForCompletion(true);
	        IOCounter = GetIO(SimpleKMeansJob);
	        String outputFilePath = simpleKMeansOutputPath + "/part-r-00000";
	        double diff = computeCenDiff(simpleKMeansCentroids, outputFilePath, conf);
	        System.out.println("DIFF: " + diff);
	        //System.in.read();
	        if(diff <= 0.00001)
	        	break;
	        else
	        {
	        	Path src = new Path(outputFilePath);
	        	Path dest = new Path(simpleKMeansCentroids);
	        	FileUtil.copy(fs, src, fs, dest, false, conf);

	        }
		}
		System.out.println("ITR: " + iteration);
		return IOCounter;
	}
	public static ArrayList<double[]> TopN(ArrayList<double[]> array, int nC){
		Comparator<double[]> arrayComparator = new Comparator<double[]>() {
	        @Override
	        public int compare(double[] o1, double[] o2) {
	        	int index = o1.length - 1;
	            if(o1[index] < o2[index])
	            	return -1;
	            else if(o1[index] > o2[index])
	            	return 1;
	            else
	            	return 0;
	        }
	    };
	    array.sort(arrayComparator);
	    ArrayList<double[]> ret = new ArrayList<double[]>();
	    for(int i = 0; i < nC; i++)
	    {
	    	ret.add(array.get(i));
	    }
	    return ret;
	}
	public static ArrayList<double[]> hierarchicalMerging(ArrayList<double[]> array, int nC){
		ArrayList<double[]> points = new ArrayList<double[]>();
		for(int i = 0; i < array.size(); i++)
		{
			double[] ptr = array.get(i);
			double[] nptr = new double[ptr.length - 1];
			for(int j = 0; j < nptr.length; j++)
				nptr[j] = ptr[j];
			points.add(nptr);
		}
		while(points.size() > nC)
		{
			double[] p1 = points.get(0);
			double[] p2 = points.get(1);
			double dist = KMeans.Distance(p1, p2);
			for(int i = 0; i < points.size(); i++)
				for(int j = i + 1; j < points.size(); j++)
				{
					double ndist = KMeans.Distance(points.get(i), points.get(j));
					if(ndist < dist)
					{
						dist = ndist;
						p1 = points.get(i);
						p2 = points.get(j);
					}
				}
			points.remove(p1);
			points.remove(p2);
			points.add(KMeans.midpoint(p1, p2));
		}
		return points;
	}
	public static ArrayList<double[]> MergePoints(String fileName, int nC) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = reader.readLine();
		ArrayList<double[]> points = new ArrayList<double[]>();
		while(line != null)
		{
			String[] pairs = line.split(":");
			String clusterid = pairs[0];
			String pstr = pairs[1];
			String[] pstrtoken = pstr.split(",");
			double[] p = new double[pstrtoken.length];
			for(int i = 0; i < pstrtoken.length; i++)
			{
				p[i] = Double.parseDouble(pstrtoken[i]);
			}
			points.add(p);
			
			line = reader.readLine();
		}
		reader.close();

//		while(points.size() > nC)
//		{
//			double[] p1 = points.get(0);
//			double[] p2 = points.get(1);
//			double dist = KMeans.Distance(p1, p2);
//			for(int i = 0; i < points.size(); i++)
//				for(int j = i + 1; j < points.size(); j++)
//				{
//					double ndist = KMeans.Distance(points.get(i), points.get(j));
//					if(ndist < dist)
//					{
//						dist = ndist;
//						p1 = points.get(i);
//						p2 = points.get(j);
//					}
//				}
//			points.remove(p1);
//			points.remove(p2);
//			points.add(KMeans.midpoint(p1, p2));
//		}
		return TopN(points, nC);
		//return hierarchicalMerging(points, nC);
		//return points;
	}
	public static void WritePoints(ArrayList<double[]> points, String fileName) throws IOException
	{
		PrintWriter writer = new PrintWriter(new FileWriter(fileName));
		for(int i = 0; i < points.size(); i++)
		{
			ArrayList<String> tokens = new ArrayList<String>();
			for(int j = 0; j < points.get(i).length; j++)
			{
				tokens.add(Double.toString(points.get(i)[j]));
			}
			writer.println(String.join(",", tokens));
		}
		writer.close();
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		long start = System.currentTimeMillis();
		
		
		StartKDTreeJob();
		long kmeansio = StartKMeansJob(); // io counter is cumulative!!
		long totalio = kmeansio;
		ArrayList<double[]> points = MergePoints("./kmeansoutput/part-r-00000", nCluster);
		WritePoints(points, "./finaloutput/cen.txt");
		System.out.println("TotalIO: " + totalio);
		
//		long io = StartSimpleKMeansJob();
//		System.out.println("TotalIO: " + io);
		
		
		
		
		long end = System.currentTimeMillis();
		int diff = (int)(end - start);
		System.out.println("Time: " + diff);

	}
}
