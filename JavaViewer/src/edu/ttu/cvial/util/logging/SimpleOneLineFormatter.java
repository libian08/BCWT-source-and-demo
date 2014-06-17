package edu.ttu.cvial.util.logging;

import java.text.*;
import java.util.*;
import java.util.logging.*;
import java.util.logging.Formatter;


public class SimpleOneLineFormatter extends Formatter {

  public static SimpleDateFormat dateFormat = new SimpleDateFormat("kk:mm:ss.SSS");

  public SimpleOneLineFormatter() {
  }

  /**
   * format
   *
   * @param record LogRecord
   * @return String
   */
  public String format(LogRecord record) {
    String msg = dateFormat.format(new Date(record.getMillis())) + " ";

    if (record.getLevel().equals(Level.SEVERE))
        msg += "S";
    else if (record.getLevel().equals(Level.WARNING))
        msg += "W";
    else
        msg += ".";

    msg += " [" + record.getSourceClassName() + "." + record.getSourceMethodName() + "()] " + record.getMessage() + "\n";

    return msg;
  }
}
