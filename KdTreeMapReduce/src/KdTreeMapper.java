import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

public class KdTreeMapper extends Mapper<LongWritable,Text,Text,Text>{

    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {
    	String[] pairs = value.toString().split(":");
    	if(pairs.length == 1)
    	{
			context.write(new Text("0"), new Text(pairs[0]));
    	}
    	else
    	{
	    	String coor = pairs[0];
	    	String node = pairs[1];
	    	String[] nodepart = node.split(",");
	    	String nodeid = nodepart[0];
	    	//System.out.println(node + " " + coor);
	    	if(nodepart.length != 1) {
	    		context.getCounter(ClusteringJob.COUNTERS.JOB_DONE).setValue(1);
	    	}
	    	else {
	    		context.getCounter(ClusteringJob.COUNTERS.JOB_DONE).setValue(0);
	
	    	}
			context.write(new Text(nodeid), new Text(coor));
    	}

    }
}
