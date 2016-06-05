import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class SimpleKMeansMapper extends Mapper<LongWritable,Text,Text,Text>{

    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
    	ArrayList<double[]> centroids = new ArrayList<double[]>();
		FileSystem fs = FileSystem.get(context.getConfiguration());
        Path pt=new Path(ClusteringJob.simpleKMeansCentroids);
        BufferedReader br=new BufferedReader(new InputStreamReader(fs.open(pt)));
        //BufferedReader br=new BufferedReader(new FileReader(ClusteringJob.simpleKMeansCentroids));

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
    	String[] tokens = value.toString().split(":");
    	String[] coorstr = tokens[0].split(",");
    	double[] point = new double[ClusteringJob.DATA_DIMENSION]; 
    	point[0] = Double.parseDouble(coorstr[0]);
    	point[1] = Double.parseDouble(coorstr[1]);
    	KMeans.MutableDouble dummy = new KMeans.MutableDouble(0);
    	int newkey = KMeans.computeCluster(point, centroids, dummy);
    	
    	context.write(new Text("" + newkey), new Text(tokens[0]));
    }

}
