import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class KMeansMapper extends Mapper<LongWritable,Text,Text,Text>{

    protected void map(LongWritable key, Text value, Context context) 
    		throws IOException, InterruptedException {
    	String[] pairs = value.toString().split(":");
    	String coor = pairs[0];
    	String node = pairs[1];
    	String[] nodepart = node.split(",");
    	String nodeid = nodepart[0];
    	String kmeans_group = nodepart[1];
// global random  	
//    	int groupid = Math.abs(ClusteringJob.RND_GEN.nextInt()) % ClusteringJob.MAX_LEAF_SIZE;
//    	kmeans_group = "" + groupid;
//    	
    	context.write(new Text(kmeans_group), new Text(coor));
    }

}
