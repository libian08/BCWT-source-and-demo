package y0.wavelet.bcwt.linebased;

import java.io.DataInput;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

import y0.color.transform.linebased.LYCbCrICT2RGB;
import y0.imageio.linebased.DataReceiver;
import y0.imageio.linebased.DataSender;
import y0.imageio.linebased.ImageOp;
import y0.imageio.linebased.ImageOpGraph;
import y0.imageio.linebased.ImageOpImpl;
import y0.imageio.linebased.ImageProp;
import y0.utils.TaskGroup;
import y0.wavelet.DWTProp;
import y0.wavelet.dwt2d.linebased.ILDWT2D97Float;

public class ILBCWT extends ImageOpImpl implements TaskGroup {

	private ImageOpGraph graph;
	
	private DataInput input;
	private ILBCWTInput ibcwtInput;

	private boolean isHeaderRead;
	private EncodedProperties enProps;

	public void setInput(DataInput input) throws IOException {
		this.input = input;
		if (input != null) {
//			try {
				readHeader();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
	}
	
	public void readHeader() throws IOException {
		if (!isHeaderRead) {
			enProps = new EncodedProperties();
			enProps.read(input);
			width = enProps.getCols();
			height = enProps.getRows();
			componentNum = enProps.getComponents();
			
			if (props == null)
				props = new Hashtable();
			ImageProp.setWidth(props, width);
			ImageProp.setWidthReal(props, enProps.getColsReal());
			ImageProp.setHeight(props, height);
			ImageProp.setHeightReal(props, enProps.getRowsReal());
			ImageProp.setComponentNum(props, componentNum);
			ImageProp.setColorSpace(props, componentNum == 3 ? ImageProp.COLORSPACE_YCBCRICT : ImageProp.COLORSPACE_GRAY);
			
			DWTProp.setDwtLevelNum(props, enProps.getDwtLevel());
			
			BCWTProp.setQMin(props, enProps.getQMin());
			BCWTProp.setBlockWidth(props, enProps.getBlockWidth());
			BCWTProp.setBlockHeight(props, enProps.getBlockHeight());

			isHeaderRead = true;
		}
	}

	public void initialize(ImageOp caller) {
		int resolutionIndex = DWTProp.getResolutionIndex(param);
		if (resolutionIndex != DWTProp.NAN) {
			int dwtLevelNum = DWTProp.getDwtLevelNum(props);
			DWTProp.setDwtLevelNum(param, dwtLevelNum - resolutionIndex);
		}
		
		graph = new ImageOpGraph(props, param, result);
		graph.setActionStartPoint(ImageOpGraph.ACTION_FROM_LAST);
		
		ibcwtInput = new ILBCWTInput();
		graph.add(ibcwtInput);
		ibcwtInput.setInput(input);
		
		graph.add(new ILBCWTCore());
		
		graph.add(new ILDWT2D97Float());
		
		switch (ImageProp.getColorSpace(props)) {
		case ImageProp.COLORSPACE_YCBCRICT:
			graph.add(new LYCbCrICT2RGB());
			break;
		}

		graph.initialize();
		
		super.initialize(caller);
	}
	
	public void online(ImageOp caller) {
		graph.online();
		
		super.online(caller);
	}
	
	public void finish(ImageOp caller) {
		graph.finish();
		
		super.finish(caller);
	}

	public DataReceiver getInputPins(int tag) {
		return null;
	}

	public DataSender getOutputPins(int tag) {
		return graph.getOpLast().getOutputPins(tag);
	}

	public Vector getTasks() {
		return graph.getTasks();
	}
	public void setProperties(Hashtable props) {
		this.props = props;
	}

}
