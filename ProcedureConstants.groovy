package com.epicor.constants

/**
 * Created by IntelliJ IDEA.
 */
interface procedureConstants {
  //for the switch statement procedurethreads
  public static final int CALCOVERLAPS=4;
  public static final int CALCDATAVALIDATION = 22;
  public static final int CALCNOTES = 24;
  public static final int REPORTFILEVALIDATE = 9;
  public static final int REPORTLOAD = 10;
  public static final int REPORTCCU = 29;

  public static final int REPORTPARTGUIDE = 1004;

  public static final int UTILEXTRACT = 4000;
  public static final int UTILQUEEXTRRELS = 42;

  public static final int DELIVERREPORTDATA = 223;
  public static final int DELIVERREPORTDATA2 = 251;
  public static final int DELIVERREPORTDATA3 = 287;
  public static final int DELIVERREPORTDATA4 = 299;
  //Generic Failures
  public static final int FAILURE_CD = 1036;

  public static final String ZIPFILE_KEY = " zipfile ";
  // TaskType Oracle = 1
  public static final long TASKTYPE_SQL = 1;
  public static final long RUNNING_STATUS = 2;
  // TaskType Oracle/Java.   = 3
    public static final long LOADACES = 6;
//RETURN REPORT CODES
  public static final long REPORT_SUCCESS = 1;
  public static final long REPORT_NODATA_FOUND = 10;

}
