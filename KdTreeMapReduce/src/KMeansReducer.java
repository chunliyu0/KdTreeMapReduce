import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class KMeansReducer extends Reducer<Text,Text,Text,Text>{
	int dimension = 2;
	protected void reduce(Text key, Iterable<Text> values, Context context) 
			throws IOException, InterruptedException{
		FileSystem fs = FileSystem.get(context.getConfiguration());
        Path pt=new Path(ClusteringJob.centroidsSeed);
        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
        String line;
        line = br.readLine();	
        String[] pair;
      
        
        KMeans kmeans = new KMeans();
        while (line != null) {
            //System.out.println(line);            
            pair = line.split(",");
            double[] ncen = new double[ClusteringJob.DATA_DIMENSION];
            ncen[0] = Double.parseDouble(pair[0]);
            ncen[1] = Double.parseDouble(pair[1]);
            kmeans.addCentroid(ncen);
            line = br.readLine();

        }
        br.close();
        for(Text v : values)
		{
        	double[] d = new double[ClusteringJob.DATA_DIMENSION];
        	String[] vec = v.toString().split(",");
        	for(int i= 0; i < vec.length; i++)
        	{
        		d[i] = Double.parseDouble(vec[i]);
        	}
        	kmeans.add(d);

		}
        int itrCount = kmeans.run();
        System.out.println("----\nITR: " + itrCount);
        double SSE = kmeans.getSSE();
        System.out.println("SSE: " + SSE);
        Long maxItrCount = Long.max(itrCount, 
        		context.getCounter(ClusteringJob.COUNTERS.ITERATION_COUNT).getValue());
        context.getCounter(ClusteringJob.COUNTERS.ITERATION_COUNT).setValue(maxItrCount);
        ArrayList<double[]> cs = kmeans.getCentroids();
		System.out.println(key + ":" + cs.size());

        for(int i = 0; i < cs.size(); i++)
        {
        	if(Double.isNaN(cs.get(i)[0]) || Double.isNaN(cs.get(i)[1]))
        		continue;
    		System.out.println(key + ":" + cs.get(i)[0] + ", " + cs.get(i)[1]);
    		ArrayList<String> coors = new ArrayList<String>();
    		for(int j = 0; j < cs.get(i).length; j++) {
    			coors.add("" + cs.get(i)[j]);
    		}
    		String coorvalue = String.join(",", coors);
    		String postinfo = "," + SSE;
	    	context.write(new Text(key + ":"), new Text(coorvalue + postinfo));
        }
        //System.in.read();

	}

}
