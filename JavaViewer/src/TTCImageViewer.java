import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.metadata.IIOMetadataNode;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import y0.gui.JDraggableLabel;
import edu.ttu.cvial.imageio.plugins.ttc.v50.TTCImageInfoV50;
import edu.ttu.cvial.imageio.plugins.ttc.v50.TTCImageWriteParamV50;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class TTCImageViewer extends javax.swing.JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1142118121651849179L;
    
    {
            // Set Look & Feel
            try {
                    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager
                                    .getSystemLookAndFeelClassName());
            } catch (Exception e) {
                    e.printStackTrace();
            }
    }

    private JMenuItem aboutMenuItem;
    private JMenu jMenu5;
    private JLabel jlbImageDisplay;
    private JSplitPane jSplitPane2;
    private JButton jbtOpenFile;
    private JToolBar jToolBar1;
    private JScrollPane jScrollPane2;
    private JLayeredPane jpanThumb;
    private JLabel jlbThumbDisplay;
    private JLabel jlbFullSizeROIX;
    private JRadioButton jrbROI;
    private JPanel jPanel3;
    private JDraggableLabel jlbThumbRect;
    private JPanel jPanel1;
    private JSplitPane jSplitPane1;
    private JMenuItem exitMenuItem;
    private JSeparator jSeparator2;
    private JMenuItem closeFileMenuItem;
    private JMenuItem saveAsMenuItem;
    private JMenuItem openFileMenuItem;
    private JRadioButton jrbFullSize;
    private JLabel jLabel7;
    private JLabel jlbFullSizeBitrate;
    private JLabel jlbFileSize;
    private JLabel jLabel5;
    private JTabbedPane jTabbedPane1;
    private JTable jtbProps;
    private JScrollPane jspanInfo;
    private JButton jbtClose;
    private JLabel jLabel11;
    private JLabel jLabel6;
    private JLabel jlbImageStatus;
    private JPanel jpanViewportStatus;
    private JLabel jlbThumbStatus;
    private JPanel jPanel9;
    private JProgressBar jpbImageProgress;
    private JPanel jpanViewport;
    private JProgressBar jpbThumbProgress;
    private JButton jbtAbort;
    private JProgressBar jpbEncodeProgress;
    private JPanel jPanel4;
    private JButton jbtSaveFile;
    private JLabel jlbFullSizeHeight;
    private JLabel jlbFullSizeWidth;
    private JLabel jLabel10;
    private JLabel jLabel9;
    private JPanel jPanel7;
    private JLabel jlbViewportDimension;
    private JLabel jlbViewportZoom;
    private ButtonGroup btgROI;
    private JMenu jMenu3;
    private JMenuBar jMenuBar1;

//	private JFileChooser jFileChooser1;

    TTCImageViewerCore tIVC = null;

    /**
     * Auto-generated main method to display this JFrame
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                    TTCImageViewer inst = new TTCImageViewer();
                    inst.setLocationRelativeTo(null);
                    inst.setVisible(true);

                    inst.reset();
            }
        });
    }//public static void main(String[] args)

    public TTCImageViewer() {
            super();
            initGUI();
    }

    private void initGUI() {
        try {
                {
                    this.setMinimumSize(new java.awt.Dimension(640, 450));
                    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                    this.setPreferredSize(new java.awt.Dimension(800, 560));
                    this.setExtendedState(JFrame.MAXIMIZED_BOTH);
                }
                
                //jSplitPane1 group
                {
                    jSplitPane1 = new JSplitPane();
                    getContentPane().add(jSplitPane1, BorderLayout.CENTER);
                    jSplitPane1.setDividerSize(3);
                    {
                        jSplitPane2 = new JSplitPane();
                        jSplitPane1.add(jSplitPane2, JSplitPane.LEFT);
                        jSplitPane2.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        jSplitPane2.setResizeWeight(1.0);

                        //jTabbedPane1 group
                        {
                            jTabbedPane1 = new JTabbedPane();
                            jSplitPane2.add(jTabbedPane1, JSplitPane.RIGHT);
                            jTabbedPane1.setMinimumSize(new java.awt.Dimension(300, 250));
                            jTabbedPane1.setPreferredSize
                                    (new java.awt.Dimension(300, 252));
                            
                            //jTabbedPane1 group
                            {
                                jPanel3 = new JPanel();
                                jTabbedPane1.addTab
                                        ("Decode", null, jPanel3, null);
                                
                                GroupLayout jPanel3Layout = 
                                        new GroupLayout((JComponent)jPanel3);
                                
                                jPanel3.setLayout(jPanel3Layout);
                                jPanel3.setPreferredSize
                                        (new java.awt.Dimension(295, 230));
                                
                                //jPanel7 group
                                {
                                    jPanel7 = new JPanel();
                                    GridLayout jPanel7Layout = 
                                            new GridLayout(2, 4);
                                    jPanel7Layout.setHgap(5);
                                    jPanel7Layout.setVgap(5);
                                    jPanel7Layout.setColumns(4);
                                    jPanel7Layout.setRows(2);
                                    jPanel7.setBorder
                                        (BorderFactory.createTitledBorder
                                            ("Full Size Image"));
                                    
                                    jPanel7.setLayout(jPanel7Layout);
                                    {
                                        jLabel10 = new JLabel();
                                        jPanel7.add(jLabel10);
                                        jLabel10.setText("file size");
                                        jLabel10.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jLabel7 = new JLabel();
                                        jPanel7.add(jLabel7);
                                        jLabel7.setText("bitrate");
                                        jLabel7.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jLabel9 = new JLabel();
                                        jPanel7.add(jLabel9);
                                        jLabel9.setText("width");
                                        jLabel9.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jLabel5 = new JLabel();
                                        jPanel7.add(jLabel5);
                                        jLabel5.setText("height");
                                        jLabel5.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jlbFileSize = new JLabel();
                                        jPanel7.add(jlbFileSize);
                                        jlbFileSize.setText("N/A");
                                        jlbFileSize.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                            jlbFullSizeBitrate = new JLabel();
                                            jPanel7.add(jlbFullSizeBitrate);
                                            jlbFullSizeBitrate.setText("N/A");
                                            jlbFullSizeBitrate.
                                                setHorizontalAlignment
                                                    (SwingConstants.CENTER);
                                    }
                                    {
                                            jlbFullSizeWidth = new JLabel();
                                            jPanel7.add(jlbFullSizeWidth);
                                            jlbFullSizeWidth.setText("N/A");
                                            jlbFullSizeWidth.
                                                setHorizontalAlignment
                                                    (SwingConstants.CENTER);
                                    }
                                    {
                                            jlbFullSizeHeight = new JLabel();
                                            jPanel7.add(jlbFullSizeHeight);
                                            jlbFullSizeHeight.setText("N/A");
                                            jlbFullSizeHeight.
                                                setHorizontalAlignment
                                                    (SwingConstants.CENTER);
                                    }
                                }////jPanel7 group
                                
                                //jPanel5 group
                                {
                                    jPanel5 = new JPanel();
                                    FlowLayout jPanel5Layout1 = new FlowLayout();
                                    jPanel5Layout1.setAlignment(FlowLayout.LEFT);
                                    jPanel5Layout1.setHgap(0);
                                    jPanel5.setLayout(jPanel5Layout1);
                                    {
                                        jrbROI = new JRadioButton();
                                        jPanel5.add(jrbROI);
                                        jrbROI.setText("ROI");
                                        jrbROI.setPreferredSize
                                            (new java.awt.Dimension(60, 22));
                                        jrbROI.setSelected(true);
                                        jrbROI.addActionListener
                                                (actionROIReset);
                                        jrbROI.addActionListener
                                            (new ActionListener(){
                                                public void actionPerformed(
                                                                ActionEvent e){
                                                decode();
                                            }

                                        });
                                    }
                                    {
                                        jrbFullSize = new JRadioButton();
                                        jPanel5.add(jrbFullSize);
                                        jrbFullSize.setText("Full size");
                                        jrbFullSize.addActionListener
                                                (actionROIReset);
                                        jrbFullSize.addActionListener
                                                (new ActionListener() {
                                                public void actionPerformed(
                                                            ActionEvent e){
                                                        decode();
                                                }
                                        });
                                    }
                                    btgROI = getBtgROI();
                                    btgROI.add(jrbROI);
                                    jrbROI.setToolTipText
                                        ("Region-Of-Interest decoding mode");
                                    btgROI.add(jrbFullSize);
                                    jrbFullSize.setToolTipText
                                        ("Full size decoding mode");

                                }//jPanel5 group
                                
                                //jpanFullSizeROI group
                                {
                                    jpanFullSizeROI = new JPanel();
                                    GridLayout jPanel5Layout = 
                                            new GridLayout(2, 4);
                                    jPanel5Layout.setHgap(5);
                                    jPanel5Layout.setVgap(5);
                                    jPanel5Layout.setColumns(4);
                                    jPanel5Layout.setRows(2);
                                    jpanFullSizeROI.setLayout(jPanel5Layout);
                                    jpanFullSizeROI.setBorder
                                        (BorderFactory.createTitledBorder
                                            ("Region Of Interest"));
                                    
                                    {
                                        jLabel1 = new JLabel();
                                        jpanFullSizeROI.add(jLabel1);
                                        jLabel1.setText("x");
                                        jLabel1.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jLabel2 = new JLabel();
                                        jpanFullSizeROI.add(jLabel2);
                                        jLabel2.setText("y");
                                        jLabel2.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jLabel3 = new JLabel();
                                        jpanFullSizeROI.add(jLabel3);
                                        jLabel3.setText("width");
                                        jLabel3.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jLabel4 = new JLabel();
                                        jpanFullSizeROI.add(jLabel4);
                                        jLabel4.setText("height");
                                        jLabel4.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jlbFullSizeROIX = new JLabel();
                                        jpanFullSizeROI.add(jlbFullSizeROIX);
                                        jlbFullSizeROIX.setText("0");
                                        jlbFullSizeROIX.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jlbFullSizeROIY = new JLabel();
                                        jpanFullSizeROI.add(jlbFullSizeROIY);
                                        jlbFullSizeROIY.setText("0");
                                        jlbFullSizeROIY.setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jlbFullSizeROIWidth = new JLabel();
                                        jpanFullSizeROI.add(jlbFullSizeROIWidth);
                                        jlbFullSizeROIWidth.setText("0");
                                        jlbFullSizeROIWidth.
                                            setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                    {
                                        jlbFullSizeROIHeight = new JLabel();
                                        jpanFullSizeROI.add(jlbFullSizeROIHeight);
                                        jlbFullSizeROIHeight.setText("0");
                                        jlbFullSizeROIHeight.
                                            setHorizontalAlignment
                                                (SwingConstants.CENTER);
                                    }
                                }//jpanFullSizeROI group
                                
                                jPanel3Layout.setHorizontalGroup(
                                    jPanel3Layout.createSequentialGroup()
                                    .addComponent(jPanel5, 
                                        GroupLayout.PREFERRED_SIZE, 73, 
                                        GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.
                                        ComponentPlacement.RELATED)
                                    .addGroup(jPanel3Layout.
                                        createParallelGroup()
                                    .addComponent(jpanFullSizeROI, 
                                        GroupLayout.Alignment.LEADING, 0, 203, 
                                        Short.MAX_VALUE)
                                    .addGroup(jPanel3Layout.
                                        createSequentialGroup()
                                    .addComponent(jPanel7, 0, 203, 
                                        Short.MAX_VALUE)
                                    .addPreferredGap(LayoutStyle.
                                        ComponentPlacement.RELATED)))
                                    .addContainerGap());
                                
                                jPanel3Layout.setVerticalGroup(
                                    jPanel3Layout.createSequentialGroup()
                                        .addContainerGap(25, Short.MAX_VALUE)
                                        .addGroup(
                                            jPanel3Layout.createParallelGroup()
                                        .addComponent(jpanFullSizeROI, 
                                            GroupLayout.Alignment.LEADING, 
                                            GroupLayout.PREFERRED_SIZE, 61, 
                                            GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jPanel5, 
                                            GroupLayout.Alignment.LEADING, 
                                            GroupLayout.PREFERRED_SIZE, 61, 
                                            GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.
                                            ComponentPlacement.RELATED)
                                        .addComponent(jPanel7, 
                                            GroupLayout.PREFERRED_SIZE, 61, 
                                            GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(69, 69));
                            }//jTabbedPane1 group
                            
                            //JspanInfo group
                            {
                                                jspanInfo = new JScrollPane();
                                                jTabbedPane1.addTab("TTC Image Properties", null, jspanInfo, null);
                                                {
                                                        TableModel jtbPropsModel = 
                                                                new DefaultTableModel(
                                                                                null,
                                                                                new String[] { "Property", "Value" });
                                                        jtbProps = new JTable();
                                                        jspanInfo.setViewportView(jtbProps);
                                                        jtbProps.setModel(jtbPropsModel);
                                                        jtbProps.setEnabled(false);
                                                        jtbProps.setAutoCreateRowSorter(true);
                                                }
                            }////JspanInfo group
                            
                        }////jTabbedPane1 group
                        
                        //jPanel1 group
                        {
                            jPanel1 = new JPanel();
                            BorderLayout jPanel1Layout = new BorderLayout();
                            jPanel1.setLayout(jPanel1Layout);
                            jSplitPane2.add(jPanel1, JSplitPane.LEFT);
                            jPanel1.setPreferredSize
                                    (new java.awt.Dimension(300, 300));
                            jPanel1.setSize(300, 300);
                            
                            //jPanel1 group's components
                            {
                                jpanThumb = new JLayeredPane();
                                jPanel1.add(jpanThumb, BorderLayout.CENTER);
                                jpanThumb.setBounds(5, 11, 223, 131);
                                jpanThumb.setPreferredSize
                                    (new java.awt.Dimension(300, 300));
                                jpanThumb.setSize(300, 300);
                                jpanThumb.setBorder(BorderFactory.
                                    createEmptyBorder(0, 0, 0, 0));
                                jpanThumb.addComponentListener(
                                    new ComponentAdapter(){
                                        public void componentResized(
                                                        ComponentEvent evt) {
                                                if (decodeThread.isStopped) {
                                                        tIVC = null;
                                                        decode();
                                                }
                                        }
                                    });//jpanThumb.addComponentListener
                                
                                {
                                    jlbThumbDisplay = new JLabel();
                                    jpanThumb.add(jlbThumbDisplay);
                                    jlbThumbDisplay.setBounds(0, 0, 1, 1);
                                    jlbThumbDisplay.setPreferredSize(
                                        new java.awt.Dimension(0, 0));
                                    jlbThumbDisplay.setSize(200, 30);
                                    jlbThumbDisplay.setVerticalTextPosition
                                        (SwingConstants.TOP);
                                    jlbThumbDisplay.setForeground
                                        (new java.awt.Color(255,0,0));
                                }
                                
                                //jlbThumbRect group
                                {
                                    jlbThumbRect = new JDraggableLabel();
                                    jpanThumb.add(jlbThumbRect);
                                    jlbThumbRect.setResizeDetectRange(10);//set
                                    jlbThumbRect.setResizeMinimumSize(10);
                                    jlbThumbRect.setBorder(new LineBorder(
                                        new java.awt.Color(255, 0, 0), 2, 
                                        false));
                                    jlbThumbRect.setBounds(0, 0, 50, 50);
                                    jlbThumbRect.setForeground(
                                        new java.awt.Color(0, 0, 0));
                                    jlbThumbRect.setJParent(jlbThumbDisplay);
                                    jlbThumbRect.setToolTipText(
                                        "Drag the right-bottom corner to resize.");
                                    jlbThumbRect.addMouseListener(
                                        new MouseAdapter(){
                                            public void mouseReleased(
                                                            MouseEvent evt) {
                                                    decode();
                                            }
                                    });
                                    jlbThumbRect.addMouseMotionListener(
                                        new MouseMotionAdapter() {
                                            public void mouseDragged(
                                                            MouseEvent evt) {
                                                    updateFullSizeROI();
                                            }
                                    });

                                }////jlbThumbRect group
                            }////jPanel1 group's components
                            
                            //jPanel9
                            {
                                jPanel9 = new JPanel();
                                BoxLayout jPanel9Layout = 
                                    new BoxLayout(jPanel9, 
                                    javax.swing.BoxLayout.X_AXIS);
                                jPanel9.setLayout(jPanel9Layout);
                                jPanel1.add(jPanel9, BorderLayout.NORTH);
                                jPanel9.setPreferredSize(new 
                                    java.awt.Dimension(300, 19));
                                {
                                    jLabel11 = new JLabel();
                                    jPanel9.add(jLabel11);
                                    jLabel11.setText("Preview: ");
                                }
                                {
                                    jlbThumbStatus = new JLabel();
                                    jPanel9.add(jlbThumbStatus);
                                    jlbThumbStatus.setText("Ready");
                                }
                                {
                                    jpbThumbProgress = new JProgressBar();
                                    jpbThumbProgress.setLayout(null);
                                    jPanel9.add(jpbThumbProgress);
                                    jpbThumbProgress.setStringPainted(true);
                                    jpbThumbProgress.setValue(60);
                                    jpbThumbProgress.setForeground
                                        (new java.awt.Color(128,255,128));
                                    jpbThumbProgress.setVisible(false);
                                }
                            }////jPanel9
                        }////jPanel1 group
                    }//jSplitPane1.setDividerSize(3);
                    
                    //jSplitPane1.setDividerSize(3);
                    {
                        jpanViewport = new JPanel();
                        BorderLayout jPanel8Layout = new BorderLayout();
                        jpanViewport.setLayout(jPanel8Layout);
                        jSplitPane1.add(jpanViewport, JSplitPane.RIGHT);
                        jpanViewport.setPreferredSize
                            (new java.awt.Dimension(485, 504));
                                
                        {
                            jScrollPane2 = new JScrollPane();
                            jpanViewport.add(jScrollPane2, BorderLayout.CENTER);
                            jScrollPane2.setPreferredSize(new java.awt.Dimension(485,543));
                            jScrollPane2.setAutoscrolls(true);
                            jScrollPane2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
                            
                            {//jlbImageDisplay group
                                jlbImageDisplay = new JLabel();
                                jScrollPane2.setViewportView(jlbImageDisplay);
                                jlbImageDisplay
                                .setVerticalAlignment(SwingConstants.TOP);
                                jlbImageDisplay.addComponentListener
                                        (new ComponentAdapter() {
                                    public void componentResized
                                        (ComponentEvent evt) {
                                        jlbImageDisplayComponentResized(evt);
                                        }
                                });
                            }//jlbImageDisplay
                        }
                        
                        {//jpanViewportStatus group
                            jpanViewportStatus = new JPanel();
                            BoxLayout jPanel10Layout = 
                                new BoxLayout(jpanViewportStatus, 
                                javax.swing.BoxLayout.X_AXIS);
                            jpanViewportStatus.setLayout(jPanel10Layout);
                            jpanViewport.add(jpanViewportStatus, 
                                BorderLayout.NORTH);
                            jpanViewportStatus.setPreferredSize
                                (new java.awt.Dimension(485, 19));
                            {
                                jLabel6 = new JLabel();
                                jpanViewportStatus.add(jLabel6);
                                jLabel6.setText("Viewport: ");
                            }
                            {
                                jlbViewportDimension = new JLabel();
                                jpanViewportStatus.add(jlbViewportDimension);
                                jlbViewportDimension.
                                    setHorizontalAlignment
                                    (SwingConstants.CENTER);
                                jlbViewportDimension.setText("0");
                            }
                            {
                                jlbViewportZoom = new JLabel();
                                jpanViewportStatus.add(jlbViewportZoom);
                                jlbViewportZoom.
                                    setHorizontalAlignment
                                    (SwingConstants.CENTER);
                                jlbViewportZoom.setText("N/A");
                            }
                            {
                                jlbImageStatus = new JLabel();
                                jpanViewportStatus.add(jlbImageStatus);
                                jlbImageStatus.setText("Ready");
                            }
                            {
                                jpbImageProgress = new JProgressBar();
                                jpanViewportStatus.add(jpbImageProgress);
                                jpbImageProgress.setStringPainted(true);
                                jpbImageProgress.setValue(60);
                                jpbImageProgress.
                                    setForeground
                                    (new java.awt.Color(128,255,128));
                                jpbImageProgress.setVisible(false);
                            }
                        
                        }//jpanViewportStatus
                        
                    }//jSplitPane1.setDividerSize(3);
                        
                }//jSplitPane1
                
                {
                    jToolBar1 = new JToolBar();
                    getContentPane().add(jToolBar1, BorderLayout.NORTH);
                    {
                        jbtOpenFile = new JButton();
                        jToolBar1.add(jbtOpenFile);
                        jbtOpenFile.setText("Open...");
                        jbtOpenFile.setToolTipText
                        ("Open any image recognized by Java or a SVS image.");
                        jbtOpenFile.addActionListener(actionOpenFile);
                    }
                    {
                        jbtSaveFile = new JButton();
                        jToolBar1.add(jbtSaveFile);
                        jbtSaveFile.setText("Save...");
                        jbtSaveFile.setToolTipText
                            ("Save the viewport as a new TTC image");
                        jbtSaveFile.addActionListener(actionSaveFile);
                    }
                    {
                        jbtClose = new JButton();
                        jToolBar1.add(jbtClose);
                        jbtClose.setText("Close");
                        jbtClose.setToolTipText
                            ("Close the currently opened image");
                        jbtClose.addActionListener(actionCloseFile);
                    }
                }
                {
                    jPanel4 = new JPanel();
                    BoxLayout jPanel4Layout = 
                        new BoxLayout(jPanel4, javax.swing.BoxLayout.X_AXIS);
                    jPanel4.setLayout(jPanel4Layout);
                    getContentPane().add(jPanel4, BorderLayout.SOUTH);
                    jPanel4.setPreferredSize(new java.awt.Dimension(792, 16));
                    {
                        jlbStatus = new JLabel();
                        jPanel4.add(jlbStatus);
                        jlbStatus.setText("Ready");
                        jlbStatus.setPreferredSize
                            (new java.awt.Dimension(400, 16));
                        jlbStatus.setSize(400, 15);
                    }
                    {
                        jbtAbort = new JButton();
                        jPanel4.add(jbtAbort);
                        jbtAbort.setText("Abort");
                        jbtAbort.setVisible(false);
                        jbtAbort.addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent evt) {
                                    abortCoding();
                            }
                        });
                    }
                    {
                        jpbEncodeProgress = new JProgressBar();
                        jPanel4.add(jpbEncodeProgress);
                        jpbEncodeProgress.setStringPainted(true);
                        jpbEncodeProgress.setValue(60);
                        jpbEncodeProgress.
                            setForeground(new java.awt.Color(0,128,192));
                        jpbEncodeProgress.setVisible(false);
                    }
                }
                this.setSize(800, 560);
                {
                    jMenuBar1 = new JMenuBar();
                    setJMenuBar(jMenuBar1);
                    
                    //jMenuBar1 group
                    {
                        jMenu3 = new JMenu();
                        jMenuBar1.add(jMenu3);
                        jMenu3.setText("File");
                        {
                            openFileMenuItem = new JMenuItem();
                            jMenu3.add(openFileMenuItem);
                            openFileMenuItem.setText("Open");
                            openFileMenuItem.addActionListener(actionOpenFile);
                        }
                        {
                            saveAsMenuItem = new JMenuItem();
                            jMenu3.add(saveAsMenuItem);
                            saveAsMenuItem.setText("Save As ...");
                            saveAsMenuItem.addActionListener(actionSaveFile);
                        }
                        {
                            closeFileMenuItem = new JMenuItem();
                            jMenu3.add(closeFileMenuItem);
                            closeFileMenuItem.setText("Close");
                          closeFileMenuItem.addActionListener(actionCloseFile);
                        }
                        {
                                jSeparator2 = new JSeparator();
                                jMenu3.add(jSeparator2);
                        }
                        {
                            exitMenuItem = new JMenuItem();
                            jMenu3.add(exitMenuItem);
                            exitMenuItem.setText("Exit");
                            exitMenuItem.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent evt) {
                                        System.exit(0);
                                }
                            });
                        }
                    }////jMenuBar1 group
                    
                    //jMenu5 group
                    {
                        jMenu5 = new JMenu();
                        jMenuBar1.add(jMenu5);
                        jMenu5.setText("Help");
                        {
                            aboutMenuItem = new JMenuItem();
                            jMenu5.add(aboutMenuItem);
                            aboutMenuItem.setText("About");
                            aboutMenuItem.addActionListener(new ActionListener()
                            {
                                public void actionPerformed(ActionEvent evt) {
                                    JOptionPane.showMessageDialog(null, 
                                        appTitle + "\n"
                                        + "version: 0.1 (b20080827)\n"
                                        + "Build for: TTCImage v" 
                                        + TTCImageInfoV50.version + "\n"
                                        + "Developed by Y0"
                                        , "About", 
                                        JOptionPane.INFORMATION_MESSAGE);
                                }
                            });
                        }
                    }////jMenu5 group
                }
        } catch (Exception e) {
            e.printStackTrace();
        }//try-catch
    }//private void initGUI()

    private JLabel jlbFullSizeROIY;
    private JPanel jPanel5;
    private JLabel jLabel4;
    private JLabel jLabel3;
    private JLabel jLabel2;
    private JLabel jLabel1;
    private JPanel jpanFullSizeROI;
    private JLabel jlbFullSizeROIHeight;
    private JLabel jlbFullSizeROIWidth;
    private JLabel jlbStatus;

    private File lastDirectory;

    private ActionListener actionOpenFile = new ActionListener() {
        private JFileChooser jFileChooser = new JFileChooser();
            public void actionPerformed(ActionEvent evt) {
                jFileChooser.setCurrentDirectory(lastDirectory);
                
                if (jFileChooser.showOpenDialog(null) == 
                        JFileChooser.APPROVE_OPTION) {
                    
                    reset();

                    lastDirectory = jFileChooser.getCurrentDirectory();
                    selectedImage = jFileChooser.getSelectedFile()
                        .getAbsolutePath();

                    int dotIndex = selectedImage.lastIndexOf(".");
                    String fileExt = 
                      dotIndex < 0 ? "" : selectedImage.substring(dotIndex + 1);	
                    srcType = fileExt ;
                    
                        if (fileExt.equalsIgnoreCase("svs")){ 
                            //if "svs" , then only encode();
                            if (JOptionPane.showConfirmDialog(null, 
                                "SVS image file can not be displayed.\n"
                                + "Do you want to convert " 
                                + selectedImage 
                                + " into a TTC image?", "Confirmation",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) 
                                == JOptionPane.YES_OPTION) {
                                    if (isEncoding) {
                                        JOptionPane.showMessageDialog(
                                            null,
                                            "Please wait until the current encoding process completes.",
                                            "Encode in progress...",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        return;
                                    }
                                    tIVC = new TTCImageViewerCore(selectedImage);
                                    isSavable = true;
                                    actionSaveFile.actionPerformed(null);
                                    selectedImage = null;
                                    isSavable = false;
                            } else {
                                selectedImage = null;
                            }//else of if (fileExt.equalsIgnoreCase("svs"))
                        }else{						
                                decode();		
                        }//else of "if (fileExt.equalsIgnoreCase("svs"))"

            }//if(jFileChooser.showOpenDialog(null)...)
        }//public void actionPerformed(ActionEvent evt)
    };

    private ActionListener actionSaveFile = new ActionListener() {
            private JFileChooser jFileChooser = new JFileChooser();

            public void actionPerformed(ActionEvent evt) {
                    if (!isSavable)
                            return;

//			jFileChooser.setFileFilter(new FileNameExtensionFilter("TTC Image (*.ttc)","ttc"));

                    jFileChooser.setCurrentDirectory(lastDirectory);
                    jFileChooser.setSelectedFile(new File(selectedImage + ".ttc"));
                    if (jFileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                            lastDirectory = jFileChooser.getCurrentDirectory();
                            encodeName = jFileChooser.getSelectedFile().getAbsolutePath();
                            if (!encodeName.toLowerCase().endsWith(".ttc"))
                                    encodeName += ".ttc";

                            encode();
                    }
            }
    };

    private ActionListener actionCloseFile = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                    TTCImageViewer.this.reset();
            }

    };

    private ActionListener actionROIReset = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                    if (jrbROI.isSelected() && selectedImage != null) {
                            jlbThumbRect.setVisible(true);
                            jlbThumbRect.setBounds(0, 0, 50, 50);
                    } else {
                            jlbThumbRect.setVisible(false);
                    }
                    updateFullSizeROI();
            }
    };


    private ButtonGroup getBtgROI() {
            if(btgROI == null) {
                    btgROI = new ButtonGroup();
            }
            return btgROI;
    }

    private void jlbImageDisplayComponentResized(ComponentEvent evt) {
            Icon imageDisplay = jlbImageDisplay.getIcon();
            if (imageDisplay == null) {
                    jlbViewportDimension.setText("");
                    jlbViewportZoom.setText("");
            } else {
                    jlbViewportDimension.setText(" " + imageDisplay.getIconWidth() + " x " + imageDisplay.getIconHeight()); 
                    Rectangle fullSizeROI = getFullSizeROI();
                    if (fullSizeROI == null) {
                            jlbViewportZoom.setText("");
                    } else {
                            int zoomPercent = imageDisplay.getIconWidth() * 100 / fullSizeROI.width;
                            jlbViewportZoom.setText(" Zoom:"+ zoomPercent + "%");
                    }
            }
            updateCloseFile();
    }

    // Codes below this line should not be parsed by Java GUI builder.
    // $hide>>$
    private void updateCloseFile() {
            if (jlbImageDisplay.getIcon() == null) {
                    jbtClose.setEnabled(false);
                    closeFileMenuItem.setEnabled(false);
            } else {
                    jbtClose.setEnabled(true);
                    closeFileMenuItem.setEnabled(true);
            }
    }

    private IIOReadProgressListener actionThumbProgress = new IIOReadProgressListener() {
            private long startTime;

            @Override
            public void imageComplete(ImageReader source) {
                    jpbThumbProgress.setVisible(false);
                    jpbThumbProgress.setValue(100);
                    long endTime = System.currentTimeMillis();
                    jlbThumbStatus.setText(" (decoded in " + (endTime - startTime) + " ms)");
            }

            @Override
            public void imageProgress(ImageReader source, float percentageDone) {
                    jpbThumbProgress.setValue((int)(percentageDone * 100));
            }

            @Override
            public void imageStarted(ImageReader source, int imageIndex) {
                    jpbThumbProgress.setVisible(true);
                    jpbThumbProgress.setMaximum(100);
                    jpbThumbProgress.setMinimum(0);
                    jpbThumbProgress.setValue(0);
                    startTime = System.currentTimeMillis();
                    jlbThumbStatus.setText(" (decoding...)");
            }

            @Override
            public void readAborted(ImageReader source) {
            }

            @Override
            public void sequenceComplete(ImageReader source) {
            }

            @Override
            public void sequenceStarted(ImageReader source, int minIndex) {
            }

            @Override
            public void thumbnailComplete(ImageReader source) {
            }

            @Override
            public void thumbnailProgress(ImageReader source, float percentageDone) {
            }

            @Override
            public void thumbnailStarted(ImageReader source, int imageIndex,
                            int thumbnailIndex) {
            }

    };

    private IIOReadProgressListener actionImageProgress = new IIOReadProgressListener() {
            private long startTime;

            @Override
            public void imageComplete(ImageReader source) {
                    jpbImageProgress.setVisible(false);
                    jpbImageProgress.setValue(100);
                    long endTime = System.currentTimeMillis();
                    jlbImageStatus.setText(" (decoded in " + (endTime - startTime) + " ms)");

                    updateProps();

                    reader = null;
            }

            @Override
            public void imageProgress(ImageReader source, float percentageDone) {
                    jpbImageProgress.setValue((int)(percentageDone * 100));
            }

            @Override
            public void imageStarted(ImageReader source, int imageIndex) {
                    jpbImageProgress.setVisible(true);
                    jpbImageProgress.setMaximum(100);
                    jpbImageProgress.setMinimum(0);
                    jpbImageProgress.setValue(0);
                    startTime = System.currentTimeMillis();
                    jlbImageStatus.setText(" (decoding...)");

                    reader = source;
            }

            @Override
            public void readAborted(ImageReader source) {
            }

            @Override
            public void sequenceComplete(ImageReader source) {
            }

            @Override
            public void sequenceStarted(ImageReader source, int minIndex) {
            }

            @Override
            public void thumbnailComplete(ImageReader source) {
            }

            @Override
            public void thumbnailProgress(ImageReader source, float percentageDone) {
            }

            @Override
            public void thumbnailStarted(ImageReader source, int imageIndex,
                            int thumbnailIndex) {
            }

    };

    private ImageWriter writer;
    private ImageReader reader;

    private IIOWriteProgressListener actionEncodeProgress = new IIOWriteProgressListener(){

            private long startTime;

            @Override
            public void imageComplete(ImageWriter source) {
                    isEncoding = false;
                    writer = null;

                    jbtAbort.setVisible(false);
                    jpbEncodeProgress.setVisible(false);
                    jlbStatus.setText("Ready");
                    jlbStatus.setForeground(Color.black);

                    long spent = System.currentTimeMillis() - startTime;
                    String encodeTime = spent < 10000 ? (spent + " ms") : ((spent / 1000) + " s");

                    JOptionPane.showMessageDialog(
                                    null, 
                                    "Encoding completes in " + encodeTime, 
                                    "Encoded", 
                                    JOptionPane.INFORMATION_MESSAGE);

                    updateSaveFile();
            }

            @Override
            public void imageProgress(ImageWriter source,
                            float percentageDone) {
                    jpbEncodeProgress.setValue((int)(percentageDone * 100));
                    long spent = System.currentTimeMillis() - startTime;
                    if (percentageDone > 0) {
                            long eta = (long) (spent / percentageDone);
                            long remaining = eta - spent;
                            jlbStatus.setText("Encoding... (Estimate: " + (eta / 1000) + " s, remaining " + (remaining / 1000) + " s)");
                    }
            }

            @Override
            public void imageStarted(ImageWriter source, int imageIndex) {
                    isEncoding = true;
                    writer = source;

                    jbtAbort.setVisible(true);
                    jpbEncodeProgress.setVisible(true);
                    jpbEncodeProgress.setMaximum(100);
                    jpbEncodeProgress.setMinimum(0);
                    jpbEncodeProgress.setValue(0);
                    jlbStatus.setText("Encoding...");
                    jlbStatus.setForeground(Color.red);

                    startTime = System.currentTimeMillis();

                    updateSaveFile();
            }

            @Override
            public void thumbnailComplete(ImageWriter source) {
            }

            @Override
            public void thumbnailProgress(ImageWriter source,
                            float percentageDone) {
            }

            @Override
            public void thumbnailStarted(ImageWriter source,
                            int imageIndex, int thumbnailIndex) {
            }

            public void writeAborted(ImageWriter source) {
                    jbtAbort.setVisible(false);
                    jpbEncodeProgress.setVisible(false);
                    jlbStatus.setText("Encoding aborted.");
                    jlbStatus.setForeground(Color.black);

                    isEncoding = false;
                    writer = null;
                    updateSaveFile();
            }

    };

    private void updateSaveFile() {
            if (isEncoding || jlbImageDisplay.getIcon() == null) {
                    isSavable = false;
                    jbtSaveFile.setEnabled(false);
                    saveAsMenuItem.setEnabled(false);
            } else {
                    isSavable = true;
                    jbtSaveFile.setEnabled(true);
                    saveAsMenuItem.setEnabled(true);
            }
    }

    private void updateProps() {
            String[] columnNames = new String[] {"Property", "Value"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0); 
            jtbProps.setModel(tableModel);
            jTabbedPane1.setEnabledAt(jTabbedPane1.indexOfComponent(jspanInfo), false);

            if (reader != null) {
                    Node root = null;
                    try {
                            root = reader.getImageMetadata(0).getAsTree("edu.ttu.cvial.imageio.plugins.ttc.v50.TTCImageV50Metadata");
                    } catch (IOException e) {
                            e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                    }

                    if (root != null) {
                            NodeList children = root.getChildNodes();
                            for (int i = 0; i < children.getLength(); i++) {
                                    IIOMetadataNode node = (IIOMetadataNode) children.item(i);
                                    String keyword = node.getAttribute("keyword");
                                    String value = node.getAttribute("value");
                                    tableModel.addRow(new Object[] {keyword, value});
                            }
                            jTabbedPane1.setEnabledAt(jTabbedPane1.indexOfComponent(jspanInfo), true);
                    }
            }
    }

    private void abortCoding() {
            if (JOptionPane.showConfirmDialog(null,
                            "Are you sure to abort encoding?", "Confirmation",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    writer.abort();
            }
    }

    private void updateFullSizeROI() {
            Rectangle fullSizeROI = getFullSizeROI();
            if (fullSizeROI == null)
                    fullSizeROI = new Rectangle(0, 0, 0, 0);
            jlbFullSizeROIX.setText(String.valueOf(fullSizeROI.x));
            jlbFullSizeROIY.setText(String.valueOf(fullSizeROI.y));
            jlbFullSizeROIWidth.setText(String.valueOf(fullSizeROI.width));
            jlbFullSizeROIHeight.setText(String.valueOf(fullSizeROI.height));
    }

    private String  srcType = "";
    private boolean isEncoding = false;
    private boolean isSavable = false;

    private void encode() {
            File encodeFile = new File(encodeName);
            if (encodeFile.exists()) {
                    if (JOptionPane.showConfirmDialog(null, encodeName
                                    + " already exist. Are you sure?") != JOptionPane.OK_OPTION) {
                            return;
                    }
            }

            String[] possibleValues = { "0 - Best", "1", "2", "3 - Normal", "4",
                            "5", "6 - Worst" };
            String selectedValue = (String) JOptionPane
                            .showInputDialog(
                                            null,
                                            "Please choose the encoding quality. \nNote that the better the quality, the larger the encoded file.",
                                            "Encoding Quality", JOptionPane.INFORMATION_MESSAGE,
                                            null, possibleValues, possibleValues[3]);

            int qMin = Integer.parseInt(selectedValue.substring(0, 1));
            ttcParam = new TTCImageWriteParamV50();
            ttcParam.setQualityIndex(qMin);
            ttcParam.setDwtLevel(6);

            tIVC.writeProgressListener = actionEncodeProgress;

            new Thread(new Runnable() {
                    public void run() {
                            try {
                                    tIVC.encode(encodeName, ttcParam, srcType);
                            } catch (Exception e) {
                                    e.printStackTrace();
                            }
                    }
            }).start();
    }

    private String encodeName;
    private TTCImageWriteParamV50 ttcParam;

    private void decodeThumbnail(String selectedImage) {
            tIVC = new TTCImageViewerCore(selectedImage);
            tIVC.readThumbProgressListener = actionThumbProgress;

            tIVC.decodeThumbnail(jpanThumb.getSize());
            if (tIVC.getDecodedThumbImage() != null) {

                    ImageIcon thumb = new ImageIcon(tIVC.getDecodedThumbImage());
                    jlbThumbDisplay.setText(null);
                    jlbThumbDisplay.setBounds(0, 0, thumb.getIconWidth(), thumb
                                    .getIconHeight());
                    jlbThumbDisplay.setIcon(thumb);

                    jpanThumb.setLayer(jlbThumbRect, JLayeredPane.DRAG_LAYER);

                    long fullSizeMemory = (long) tIVC.getFullSizeImageWidth()
                                    * tIVC.getFullSizeImageHeight() * 3;
                    if (fullSizeMemory > Runtime.getRuntime().maxMemory()) {
                            if (jrbFullSize.isEnabled()) {
                                    jrbROI.setSelected(true);
                                    jrbFullSize.setEnabled(false);
                                    JOptionPane
                                                    .showMessageDialog(
                                                                    null,
                                                                    "Huge image detected!\n"
                                                                                    + "Note that full size decoding is possible but displaying it is not.\n"
                                                                                    + (fullSizeMemory / 1024 / 1024)
                                                                                    + " M-byte is required, whereas only "
                                                                                    + (Runtime.getRuntime().maxMemory() / 1024 / 1024)
                                                                                    + " M-byte memory is available.\n"
                                                                                    + "Full size image decoding will be disabled.",
                                                                    "Warning: Huge image detected!",
                                                                    JOptionPane.INFORMATION_MESSAGE);
                            }
                    } else {
                            jrbFullSize.setEnabled(true);
                    }
                    jrbROI.setEnabled(true);
                    jrbROI.setSelected(true);
            } else {
                    jlbThumbDisplay.setIcon(null);
                    jlbThumbStatus.setText("Not available.");
                    jrbROI.setEnabled(false);
                    jrbFullSize.setSelected(true);
            }
    }

    private String selectedImage;

    private Rectangle getFullSizeROI() {
            Rectangle fullSizeROI = null;
            if (tIVC != null) {
                    if (jrbROI.isSelected()) {
                            double imageHeightRatio = (double) tIVC.getFullSizeImageHeight()
                                            / jlbThumbDisplay.getHeight();
                            double imageWidthRatio = (double) tIVC.getFullSizeImageWidth()
                                            / jlbThumbDisplay.getWidth();

                            fullSizeROI = new Rectangle((int) (jlbThumbRect.getLocation().x * imageWidthRatio),
                                            (int) (jlbThumbRect.getLocation().y * imageHeightRatio),
                                            (int) (jlbThumbRect.getWidth() * imageWidthRatio),
                                            (int) (jlbThumbRect.getHeight() * imageHeightRatio));
                    } else {
                            fullSizeROI = new Rectangle(0, 0, tIVC.getFullSizeImageWidth(), tIVC.getFullSizeImageHeight());
                    }
            }
            return fullSizeROI;
    }

    protected class DecodeThread implements Runnable {
            public boolean isStopped = true;

            public void run() {
                    isStopped = false;

                    jlbThumbRect.setDragEnabled(false);
                    jlbThumbRect.setResizeEnabled(false);

                    // decode the thumbnail first if tIVC is null, after file changed!
                    if (tIVC == null) {
                            if (!selectedImage.equals("")) {
                                    decodeThumbnail(selectedImage);
                                    actionROIReset.actionPerformed(null);
                            }

                    }

                    // decode the image based on thumbnail info.
                    if (tIVC != null) {
                            Rectangle roiRange = null;
                            if (jrbROI.isSelected()) {

                                    roiRange = getFullSizeROI();

                                    // tIVC.setDisplayDimension(jScrollPane2.getSize());
                                    tIVC.setDisplayDimension(new Dimension(jScrollPane2
                                                    .getWidth() - 5, jScrollPane2.getHeight() - 5));
                            }

                            tIVC.readImageProgressListener = actionImageProgress;
                            tIVC.decodeROIImage(roiRange);
                            if (tIVC.getDecodedImage() != null) {
                                    ImageIcon thumb = new ImageIcon(tIVC.getDecodedImage());
                                    jlbImageDisplay.setBounds(0, 0, thumb.getIconWidth(), thumb
                                                    .getIconHeight());
                                    jlbImageDisplay.setIcon(thumb);

                                    TTCImageViewer.this.setTitle(appTitle + " - " + selectedImage);

                                    updateFullSizeInfo();


                            } else {
                                    selectedImage = null;
                                    jlbImageDisplay.setIcon(null);
                            }
                    }

                    isStopped = true;

                    jlbThumbRect.setDragEnabled(true);
                    jlbThumbRect.setResizeEnabled(true);

                    updateSaveFile();
            }
    };

    protected DecodeThread decodeThread = new DecodeThread();

    protected String appTitle = "TTCImageViewer";

    protected void decode() {

            if (selectedImage != null && decodeThread.isStopped) {
                    new Thread(decodeThread).start();
            }
    }

    private void reset() {
            this.setTitle(appTitle);

            tIVC = null;
            jlbThumbDisplay.setIcon(null);
            jlbThumbStatus.setText("Ready");
            jlbImageDisplay.setIcon(null);
            jlbImageDisplayComponentResized(null);
            jlbImageStatus.setText("Ready");
            selectedImage = null;
            isSavable = false;
            actionROIReset.actionPerformed(null);
            updateSaveFile();
            updateCloseFile();
            updateProps();
            updateFullSizeInfo();
    }

    private void updateFullSizeInfo() {
            if (tIVC == null || selectedImage == null) {
                    jlbFileSize.setText("");
                    jlbFullSizeBitrate.setText("");
                    jlbFullSizeWidth.setText("");
                    jlbFullSizeHeight.setText("");
            } else {
                    int width = tIVC.getFullSizeImageWidth();
                    int height = tIVC.getFullSizeImageHeight();
                    long bytes = new File(selectedImage).length();
                    float bitrate = (float) bytes / (width * height);

                    jlbFileSize.setText((String.format("%.3f", new Float(bytes / 1024f / 1024f)) + " M"));
                    jlbFullSizeBitrate.setText(String.format("%.3f", new Float(bitrate)));
                    jlbFullSizeWidth.setText(String.valueOf(width));
                    jlbFullSizeHeight.setText(String.valueOf(height));
            }
    }


    // $hide<<$

}//public class TTCImageViewer extends javax.swing.JFrame
