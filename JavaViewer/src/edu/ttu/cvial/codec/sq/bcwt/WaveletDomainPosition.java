package edu.ttu.cvial.codec.sq.bcwt;

public class WaveletDomainPosition {
    public int col;
    public int component;
    public int dwtLevel;
    public int row;
    public int absValueInt;
    public boolean sign;
    public WaveletDomainPosition[] offspring;
    public int qMin;

    public static void getOffspring(final WaveletDomainPosition pos, WaveletDomainPosition[] offspring) {
        int rowBase = pos.row << 1;
        int colBase = pos.col << 1;
        offspring[0].row = rowBase;
        offspring[0].col = colBase;
        offspring[1].row = rowBase;
        offspring[1].col = colBase + 1;
        offspring[2].row = rowBase + 1;
        offspring[2].col = colBase;
        offspring[3].row = rowBase + 1;
        offspring[3].col = colBase + 1;
    }
}
