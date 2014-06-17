package y0.wavelet;

public class WTNode {
    public int x;
    public int dwtLevel;
    public int y;
    public int absValueInt;
    public boolean sign;
    public WTNode[] offspring;
    public int qMin;
    public int subbandType;
	public int component;

    public static void initOffspring(final WTNode pos, WTNode[] offspring) {
        int yBase = pos.y << 1;
        int xBase = pos.x << 1;
        offspring[0].y = yBase;
        offspring[0].x = xBase;
        offspring[1].y = yBase;
        offspring[1].x = xBase + 1;
        offspring[2].y = yBase + 1;
        offspring[2].x = xBase;
        offspring[3].y = yBase + 1;
        offspring[3].x = xBase + 1;
        
        for (int i = 0; i < 4; i++) {
            offspring[i].subbandType = pos.subbandType;
            offspring[i].dwtLevel = pos.dwtLevel - 1;
            offspring[i].component = pos.component;
            offspring[i].absValueInt = 0;
            offspring[i].sign = true;
        }        
        
    }
}
