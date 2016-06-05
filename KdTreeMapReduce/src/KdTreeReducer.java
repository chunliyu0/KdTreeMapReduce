import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class KdTreeReducer extends Reducer<Text,Text,Text,Text>{
	Random r = new Random();
	public class DataVector implements Comparable<DataVector>{
		double magic = Math.random();
		DataVector(int theDim){
			splitDim = theDim;
		}
		void add(double theNumber)
		{
			dataArray.add(theNumber);
		}
	    @Override
		public String toString()
		{
	    	ArrayList<String> arr = new ArrayList<String>();
	    	for(Double num : dataArray) {
	    		arr.add(num.toString());
	    	}
			return String.join(",", arr);
		}
		private int splitDim;
	    @Override
	    public int compareTo(DataVector another){
	    	double a; 
	    	double b; 
	    	if(splitDim >= 0)
	    	{
		    	a = this.dataArray.get(splitDim).doubleValue();
		    	b = another.dataArray.get(splitDim).doubleValue();
	    	}
	    	else
	    	{
//		    	a = this.dataArray.get(splitDim).doubleValue();
//		    	b = another.dataArray.get(splitDim).doubleValue();
	    		a = magic;
	    		b = another.magic;
	    	}
	        if (a > b)
	            return 1;
	        else if (a == b)
	            return 0;
	        else 
	            return -1;
	    }
	    private ArrayList<Double> dataArray = new ArrayList<Double>();
	}
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException{
    	Configuration conf = context.getConfiguration();
    	long maxleafcap = Long.parseLong(conf.get(ClusteringJob.MAX_LEAF_CAP));
    	int splitDim = Integer.parseInt(conf.get(ClusteringJob.SPLIT_DIM));
        //System.out.println(conf.get(ClusteringJob.SPLIT));
		String splitFlag = conf.get(ClusteringJob.SPLIT_FLAG);
		//System.out.println(splitFlag);

		// random in subregion!!!---------------
//		if(splitFlag.equals(ClusteringJob.L_FALSE))
//			splitDim = -1;
       //----------------------------------
		
		ArrayList<DataVector> arrays = new ArrayList<DataVector>();
		for(Text v : values) {
    		String[] nums = v.toString().split(",");
			DataVector dv = new DataVector(splitDim);
    		for(String n : nums) {
    			//System.out.println(n);
    			dv.add(Double.parseDouble(n));
    		}
			arrays.add(dv);
    	}

		System.out.println(arrays.size());
		long maxleafsize = Long.max((arrays.size() + 1) / 2, 
				context.getCounter(ClusteringJob.COUNTERS.LEAF_SIZE).getValue());
		context.getCounter(ClusteringJob.COUNTERS.LEAF_SIZE).setValue(maxleafsize);
		System.out.println("Reducer Type: " + splitFlag);
		if(splitFlag.equals(ClusteringJob.L_TRUE) && arrays.size() > maxleafcap)
		{
			Collections.sort(arrays);
			int split = arrays.size() / 2;
			for(int i = 0; i < arrays.size(); i++)
			{
				Integer node;
				if(i >= split)
					node = 1;
				else 
					node = 0;
				String newkey = key + node.toString();
				context.write(new Text(arrays.get(i).toString()), new Text(":" + newkey));
			}
		}
		else
		{
			Collections.sort(arrays);
			for(int i = 0; i < arrays.size(); i++)
			{
				context.write(new Text(arrays.get(i).toString()), 
						new Text(":" + key + "," + Integer.toString(i)));

				
			}
			//conf.set(ClusteringJob.LOOP_FLAG, ClusteringJob.L_FALSE);
			
		}
		

    }
}
