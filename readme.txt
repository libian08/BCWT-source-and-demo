The BCWT demo is provided for test. Because a patent was filed for BCWT (lossless), 
the source code can be published after the patent is granted.  
The source code of the lossy BCWT in JAVA is provided here.


BCWT lossless demo usage

Running parameter settings(in Linux):
Encode:        Mode: 0, inputFile, outputFile, 0(lossy)/1(lossless), qMin(0-7), dwtLevel(2- 6)  
DecodeImage:   Mode: 1, inputFile, outputFile, outputformat(png/jpg)

Example:
Encode:        2dBCWTnew 0 exampleImage.png outputTTC.ttc 1 0 6
DecodeImage:   2dBCWTnew 1 outputTTC.ttc fullDecImage  png
