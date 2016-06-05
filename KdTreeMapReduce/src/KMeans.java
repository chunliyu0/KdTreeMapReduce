import java.util.ArrayList;

class Point{
	
	
}

public class KMeans {
	class Point {
		public double[] v;
		public int cluster;
	}
	public static class MutableDouble {

		   private double value;

		   public MutableDouble(double value) {
		     this.value = value;
		   }

		   public double getValue() {
		     return this.value;
		   }

		   public void setValue(double value) {
		     this.value = value;
		   }
	}
	public static double Distance(double[] p1, double[] p2) {
		double sum = 0;
		int len = Integer.min(p1.length, p2.length);
		for(int i = 0; i < len; i++) {
			sum += (p1[i] - p2[i]) * (p1[i] - p2[i]);
		}
		return Math.sqrt(sum);
	}
	public static void increment(double[] v, double[] add)
	{
		int len = Integer.min(v.length, add.length);
		for(int i = 0; i < len; i++)
		{
			v[i] += add[i];
		}
	}
	public static double[] average(ArrayList<double[]> points)
	{
		int len = points.get(0).length;
		double[] ret = new double[len];
		for(int i = 0; i < points.size(); i++)
		{
			increment(ret, points.get(i));
		}
		divide(ret, (double)points.size());
		return ret;
	}
	public static double[] midpoint(double[] p1, double[] p2){
		int len = Integer.min(p1.length, p2.length);
		double[] ret = new double[len];
		for(int i = 0; i < len; i++)
		{
			ret[i] = (p1[i] + p2[i]) / 2.0;
		}
		return ret;
	}
	public static void divide(double[] v, double d)
	{
		int len = v.length;
		for(int i = 0; i < len; i++)
		{
			v[i] /= d;
		}
	}
	public static int computeCluster(double[] v, ArrayList<double[]> cen, MutableDouble retdist)
	{
		int ret = 0;
		double dist = Distance(v, cen.get(0));
		for(int i = 1; i < cen.size(); i++) {
			double ndist = Distance(v, cen.get(i));
			if(ndist < dist) {
				dist = ndist;
				ret = i;
			}
		}
		retdist.setValue(dist);
		return ret;
	}
	public int run() {
		int iteration = 0;
		for(iteration = 0; iteration < maxIteration; iteration++)
		{
			SSE = 0;
			for(int j = 0; j < points.size(); j++)
			{
				MutableDouble error = new MutableDouble(0);
				int nC = computeCluster(points.get(j).v, centroids, error);
				SSE += error.getValue();
				points.get(j).cluster = nC;
			}
			SSE /= (double)points.size();
			double diff = 0;
			for(int j = 0; j < clusterCount; j++)
			{
				double[] newc = new double[dimension];
				int count = 0;
				for(int k = 0; k < points.size(); k++)
				{
					if(points.get(k).cluster == j)
					{
						count++;
						increment(newc, points.get(k).v);
					}
				}
				if(count != 0)
				{
					divide(newc, (double)count);
					diff += Distance(centroids.get(j), newc);
					centroids.set(j, newc);
				}
			}
			if(diff <= epsilon)
				break;
		}
		return iteration;
	}
	public void add(double[] point){
		Point np = new Point();
		np.v = point;
		np.cluster = 0;
		dimension = Integer.min(dimension, point.length);
		points.add(np);
	}
	public void addCentroid(double[] c){
		centroids.add(c);
		clusterCount = centroids.size();
	}
	ArrayList<double[]> getCentroids(){
		return centroids;
	}
	public double getSSE(){
		return SSE;
	}
	private int clusterCount;
	private double epsilon = 0.0000000001;
	private int dimension = Integer.MAX_VALUE;
	private int maxIteration = 200;
	private double SSE = 0;

	ArrayList<double[]> centroids = new ArrayList<double[]>();
	ArrayList<Point> points = new ArrayList<Point>();
}
