package y0.wavelet.bcwt.linebased;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Vector;

import y0.color.transform.linebased.LRGB2YCbCrICT;
import y0.imageio.ROI;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpGraph;
import y0.imageio.linebased.ImageOpImpl;
import y0.imageio.linebased.ImageProp;
import y0.imageio.linebased.LBorderExtender;
import y0.utils.TaskGroup;
import y0.wavelet.DWTProp;
import y0.wavelet.dwt2d.linebased.LDWT2D97Float;

public class LBCWT extends ImageOpImpl implements TaskGroup {

	private ImageOpGraph graph;
	
	private EncodedProperties enProps;

	private DataOutput output;
	
	private ImageProp imgProp;
	private DWTProp dwtParam;
	private BCWTProp bcwtParam;

	public void initialize(ImageOp caller) {
		imgProp = new ImageProp(props);
		dwtParam = new DWTProp(param);
		bcwtParam = new BCWTProp(param);
		
		graph = new ImageOpGraph(props, param, result);

		switch (imgProp.colorSpace) {
		case ImageProp.COLORSPACE_RGB:
			graph.add(new LRGB2YCbCrICT());
			break;
		}
		
		ROI orgROI = new ROI();
		orgROI.width = imgProp.width;
		orgROI.height = imgProp.height;
		ROI extROI = getExtendedROI(orgROI, dwtParam.levelNum, bcwtParam.blockWidth, bcwtParam.blockHeight);
		if (extROI.width != orgROI.width || extROI.height != orgROI.height) {
			ImageProp.setWidth(param, extROI.width);
			ImageProp.setHeight(param, extROI.height);
			graph.add(new LBorderExtender());
		}
		
		graph.add(new LDWT2D97Float());

		graph.add(new LBCWTCore());
		
		LBCWTOutput bcwtOutput = new LBCWTOutput();
        graph.add(bcwtOutput);
        bcwtOutput.setOutput(output);


		try {
			writeHeader();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        graph.initialize();
        
        super.initialize(caller);
	}
	
	public void online(ImageOp caller) {
		graph.online();
		
		super.online(caller);
	}

	public void setOutput(DataOutput output) {
		this.output = output;
	}

	private void writeHeader() throws IOException {
		// Write header data.
		enProps = new EncodedProperties();
		enProps.setCols(ImageProp.getWidth(props));
		enProps.setColsReal(ImageProp.getWidthReal(props));
		enProps.setRows(ImageProp.getHeight(props));
		enProps.setRowsReal(ImageProp.getHeightReal(props));
		enProps.setComponents(componentNum);
		enProps.setDwtLevel(dwtParam.levelNum);
		enProps.setQMin(bcwtParam.qMin);
		enProps.setBlockWidth(bcwtParam.blockWidth);
		enProps.setBlockHeight(bcwtParam.blockHeight);
		enProps.write(output);
	}

	public DataReceiver getInputPins(int tag) {
		return graph.getOpFirst().getInputPins(tag);
	}

	public DataSender getOutputPins(int tag) {
		return null;
	}
	
	public void finish(ImageOp caller) {
		graph.finish();
		
		super.finish(caller);
	}

	public float getQualityIndexAchieved() {
		return BCWTProp.getQMin(props);
	}

	public Vector getTasks() {
		return graph.getTasks();
	}
	
	public static ROI getExtendedROI(ROI orgROI, int dwtLevelNum, int blockWidth, int blockHeight) {
		int widthBase = blockWidth << dwtLevelNum;
		int heightBase = blockHeight << dwtLevelNum;
		
		ROI extROI = new ROI();
		extROI.x = orgROI.x;
		extROI.y = orgROI.y;
		
		extROI.width = orgROI.width / widthBase * widthBase;
		if (extROI.width < orgROI.width)
			extROI.width += widthBase;
		
		extROI.height = orgROI.height / heightBase * heightBase;
		if (extROI.height < orgROI.height)
			extROI.height += heightBase;
		
		return extROI;
	}

}
