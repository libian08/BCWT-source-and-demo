The BCWT demo is provided for test. Because a patent was filed for BCWT (lossless), the source code can be published after the patent is granted.  The source code of the lossy BCWT in JAVA is provided.


BCWT lossless demo usage

Running parameter settings(in Linux):
 Encode:                    Mode: 0, inputFile, outputFile, 0(lossy)/1(lossless), qMin(0-7), dwtLevel(2- 6)  
DecodefullImage:    Mode: 1, inputFile, outputFile, outputformat(png/jpg
DecodeROI:              Mode: 2, roi-x, roi-y, roi-width, roi-height, inPutFile, outputFile, outputFormat(png/jpg)
Example:
Encode:                    2dBCWTnew 0 exampleImage.png outputTTC.ttc 0 0 6
DecodefullImage:   2dBCWTnew 1 outputTTC.ttc fullDecImage  png
DecodeROI:             2dBCWTnew 2 10 100 300 600 outputTTC.ttc ROIDecImage  png
