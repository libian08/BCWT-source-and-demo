package y0.imageio;

public class ROI {
	public int x;
	public int y;
	public int width;
	public int height;
	
	public static void union(ROI src1, ROI src2, ROI dest) {
		int x = Math.min(src1.x, src2.x);
		int y = Math.min(src1.y, src2.y);
		int width = Math.max(src1.x + src1.width, src2.x + src2.width) - x;
		int height = Math.max(src1.y + src1.height, src2.y + src2.height) - y;
		dest.x = x;
		dest.y = y;
		dest.width = width;
		dest.height = height;
	}
}
