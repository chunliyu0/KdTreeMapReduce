import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class SimpleKMeansReducer extends Reducer<Text,Text,Text,Text>{
	protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		ArrayList<double[]> points = new ArrayList<double[]>();
		for(Text token : values)
		{
			String[] ptoken = token.toString().split(",");
			double[] np = new double[ptoken.length];
			for(int i = 0; i < ptoken.length; i++)
			{
				np[i] = Double.parseDouble(ptoken[i]);
			}
			points.add(np);
		}
		double[] avg = KMeans.average(points);
		ArrayList<String> newcen = new ArrayList<String>();
		for(int i = 0; i < avg.length; i++)
		{
			newcen.add("" + avg[i]);
		}
		String newkey = String.join(",", newcen);
		context.write(new Text(newkey), new Text(""));
	}

}
