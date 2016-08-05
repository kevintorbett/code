/*
  */
package com.comp.imr.dao;

import com.comp.uow.ParUnitOfWork;
import groovy.sql.Sql;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.comp.constants.procedureConstants
import org.springframework.jdbc.core.support.JdbcDaoSupport


class ParQueueSQLDAO  extends JdbcDaoSupport {
  private static final Log logger = LogFactory.getLog(ParQueueSQLDAO.class);
  /**
   * End of Task
   * @param uow the unit of work
   * @param statusText status text
   * @return true if successful
   */
  public static void endTask(ParUnitOfWork uow) {
    def sql = null;
    String procedure = "endTask:";
    String msg = procedure + " task = " + uow.taskId + " content = "+ uow.contentId;

    if (logger.isDebugEnabled()) {
      System.out.println(msg);
      System.out.println();
      logger.debug(msg);
    }
    if (uow.nostopproc==0) {
      try {
       //   logger.debug("procedureStopmain");
        def workTaskStatusIxt = null;
        def date = null;

        sql = new Sql(uow.dataSource)  // dataSource set in *poller-launch.xml
        sql.call '{call PKG_WORK_TASK_QUEUE.p_work_task_stopped(?,?,?,?,?,?,?)}',
                [uow.id,
                 uow.taskStatusId,
                 uow.returnStatus,
                 uow.message,
                 0,
                 Sql.VARCHAR,
                 Sql.DATE],
                {p_work_task_status_txt, p_date_task_stopped ->
                  workTaskStatusIxt = p_work_task_status_txt
                  date = p_date_task_stopped
                }

      } catch (Exception e) {
        if (logger.isDebugEnabled()) {
          logger.debug("SQL Exception: p_work_task_stopped: ${e.getMessage()}");
          System.out.println("SQL Exception: p_work_task_stopped: ${e.getMessage()}");
        }
      } finally {
        try {
          sql?.close();
        } catch (Exception e) {
          if (logger.isDebugEnabled()) {
            logger.debug("SQL Exception Error in p_work_task_stopped: ${e.getMessage()}");
            System.out.println("SQL Exception: p_work_task_stopped: ${e.getMessage()}");
          }
        }
      }
        logger.debug("p_work_task_stopped: " + uow.id);
    }

      uow.nostopproc=0;
    }
  /**
   * Retrieves the next task available for the given task type
   * @param taskType the work task taskType (1=Oracle 2=Java, 3=JAVA/Oracle...)
   * @param taskStatusId the work task StatusId (0=Submitted)
   * @return a unit of work containing the Work Instance Id, Content Id, and Data Source Id.
   */
  public static ParUnitOfWork getNextTaskType(long taskType,long taskStatusId, Object dataSourceIn) {
    String msg = "getNextTaskType ";
    ParUnitOfWork uow = new ParUnitOfWork();
    uow.message = msg;
      java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
    //  logger.debug("Hostname of local machine: " + localMachine.getHostName());
    def hostName = localMachine.getHostName();
    def machineName = hostName.split('\\.')[0];
  //    logger.debug("machineName: " + machineName);
    def workTaskId = null;
    def workTaskType = 16;
  //      taskType =16;
    def taskParams = null;
    def workTaskStatusId = null;
    def workInstanceId = null;
    def contentId = null;
    def taskStatusTxt = null;
    def dateStarted = null;
    def email =null;
    def sql = null;
      System.out.println("p_get_start_work_task");
   //   logger.debug("p_get_start_work_task");
    try {
      sql = new Sql(dataSourceIn);
    } catch (Exception e) {
      processOracleException(e, uow)
    }

    // Execute the get next task Stored Procedure call
    try {
        sql.call '{call PKG_WORK_TASK_QUEUE.p_get_start_work_task(?,?,?,?,?,?,?,?,?,?)}',
                [Sql.NUMERIC,
                        taskStatusId,
                        Sql.NUMERIC,
                        Sql.NUMERIC,
                        Sql.VARCHAR,
                        Sql.DATE,
                        Sql.VARCHAR,
                        97,
                        Sql.VARCHAR,
                        machineName],
                {p_workTask_id,p_work_instance_id, p_content_id,p_work_task_status_txt,p_date_task_started,p_email,p_task_params ->
                    workTaskId = p_workTask_id
                    workInstanceId = p_work_instance_id
                    contentId = p_content_id
                    taskStatusTxt = p_work_task_status_txt
                    dateStarted = p_date_task_started
                    email = p_email
                    taskParams = p_task_params}
    } catch(Exception e) {
      processOracleException(e, uow);
    }
    if (workInstanceId != null || contentId != null) {
      // Set the unit of work parameters
      uow.taskId = workTaskId;
      uow.id = workInstanceId;
      uow.workInstanceId = workInstanceId;
      uow.contentId = contentId;
      uow.email =  email;
      uow.dateStarted = dateStarted;
      uow.taskStatusTxt = taskStatusTxt;
      uow.task_type = taskType;
      uow.task_params = taskParams;
      uow.dataSource = dataSourceIn;

 //       logger.debug("contentId: "+contentId+" workTaskId-:" + workTaskId);
    }
    else {
      // set unit of work to null.  This will cause the queue poller exit out.
      uow = null;
  //      logger.debug("nothing to run: " + machineName);
    }
    return uow;
  }

  /**
   * Process an Oracle Exception from the stored procedure call
   * @param procedure stored procedure call (default)
   * @param e the exception
   * @param uow the Unit of Work
   */
  public static void processOracleException(Exception e, ParUnitOfWork uow) {
    String taskmsg = uow.message+" content = "+uow.contentId+" task = " + uow.taskId + " workInstance = "+uow.workInstanceId+":"+":"+e.message;
    uow.message = uow.message+":"+e.message;
    uow.returnStatus = procedureConstants.FAILURE_CD;
    if (logger.isDebugEnabled()) {
      logger.debug("\n!--Exception Error--! in procedure \n"+ taskmsg);
      System.out.println("\n!--Exception Error--! in procedure \n"+taskmsg);
    }

    String exceptionMsg = e.getMessage();
    if (exceptionMsg.contains("ORA-")) {
      // Parse out the ORA number
      int oraNum = exceptionMsg.indexOf("ORA-");
      String temp = exceptionMsg.substring(oraNum + 4, oraNum + 4 + 5);
      if (logger.isDebugEnabled()) {
        logger.debug(taskmsg);
        logger.debug("SQL OraNum: "+oraNum+" ORA-:" + temp);
        System.out.println(taskmsg);
        System.out.println("SQL OraNum: "+oraNum+" ORA-:" + temp);
      }
    }
    else {
      if (logger.isDebugEnabled()) {
        logger.debug(taskmsg);
        System.out.println(taskmsg);
      }
    }
  }


}