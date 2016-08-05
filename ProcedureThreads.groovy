/*
 */
package com.comp.threads

import com.comp.Sql.ProcedureCalcs
import com.comp.Sql.ProcedureReports
import com.comp.io.imrBatch
import com.comp.Sql.ProcedureUtils
import com.comp.Sql.ProcedureNetAdds

import com.comp.constants.procedureConstants
import com.comp.imr.dao.ParQueueSQLDAO
import com.comp.uow.ParUnitOfWork
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import static com.comp.constants.procedureConstants.*

/**
 * Created by IntelliJ IDEA.
 */
//Creating a Thread
//Instantiating a subclass of thread.java
/*com.comp.threads.ProcedureThreads class implements a thread that */
/*sleeps and calls a stored procedure*/

class ProcedureThreads extends Thread {
  private static final Log logger = LogFactory.getLog(ProcedureThreads.class);

  def dataSource;

  static int Range = 10000;

  ParUnitOfWork uow = new ParUnitOfWork();

  public ProcedureThreads(ParUnitOfWork uowIn) {
    uow = uowIn;
  }

  public void run_procedure() {

    int howLong = (int) (Math.random() * Range);
    ParUnitOfWork uowthread = new ParUnitOfWork();
    uowthread = this.uow;
    try {

      sleep(howLong);

    } catch (InterruptedException e) {
      logger.debug("\npar Poller InterruptedException Task =" + uowthread.taskId + " content = " + uowthread.contentId + "error message =" + e.message + "\n");
      System.out.println("\npar Poller InterruptedException Task =" + uowthread.taskId + " content = " + uowthread.contentId + "error message =" + e.message + "\n");
    };
    switch (uowthread.taskId) {
    // Calculate
      case CALCINVALIDCHAR:
        ProcedureCalcs.startProcCalcInvalidChar(uowthread);
        break;
      case CALCOVERLAPS:
        ProcedureCalcs.startProcCalcOverLaps(uowthread);
        break;
         case REPORTOVERLAPS:
        ProcedureReports.startProcRepOverLaps(uowthread);
        break;
      case REPORTDATAVALIDATION:
        ProcedureReports.startProcRepDataValidation(uowthread);
        break;
         // Utilities
      case UTILEXTRACT:
        ProcedureUtils.startProcUtilExtract(uowthread);
        break;
      case UTILQUEEXTRRELS:
        ProcedureUtils.startProcQueExtrRels(uowthread);
        break;

      default:
        if (logger.isDebugEnabled()) {
          logger.debug("\npar Poller InValid Task =" + uowthread.taskId + " content = " + uowthread.contentId + "\n");
          System.out.println("\npar Poller InValid Task =" + uowthread.taskId + " content = " + uowthread.contentId + "\n");
          uowthread.returnStatus = procedureConstants.FAILURE_CD;
          uowthread.message = "par Poller InValid Task =" + uowthread.taskId + " content = " + uowthread.contentId + "\n";
          System.out.println();
        }
        break;
    }
    ParQueueSQLDAO.endTask(uowthread);
  }

  public void run() {
    run_procedure(); // required by Java
  }
}