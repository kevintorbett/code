package com.comp.Sql

import com.comp.constants.procedureConstants
import com.comp.imr.dao.ParQueueSQLDAO
import com.comp.io.EmailHandler
import com.comp.io.EmailHandlerPiesSftp
import com.comp.uow.ParUnitOfWork
import groovy.sql.Sql
import java.util.zip.ZipException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * Created by IntelliJ IDEA.
  */

/*calls a stored procedure*/

class ProcedureReports {
    private static final Log logger = LogFactory.getLog(ProcedureReports.class);
    private static final def progname = "ProcedureReports";

    private static EmailHandler emailHandler = null;
    private static EmailHandlerSftp emailHandlerPiesSftp = null;

  public static void startProcRepOverLaps(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcRepOverLaps:PKG_ACES_ASSESS.p_rep_overlap: workInstanceId = "+ uow.workInstanceId;
    try {
        def sql = new Sql(uow.dataSource)
        sql.call '{call PKG_ACES_ASSESS.p_rep_overlap(?,?,?,?,?,?,?,?)}',
                [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
              { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
                uow.filenames = p_txt_file_names
                uow.zip_file_name = p_zip_file_name;
                uow.email = p_email;
                uow.email_subj = p_email_subj;
                uow.email_body_txt = p_email_body_txt;
                uow.email_from_user = p_email_from_user;
                uow.returnStatus = p_return_code
            }
        if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
            emailHandler = new EmailHandler(uow);
         //   procedureStop(uow);
        }
      } catch (Exception e) {
        ParQueueSQLDAO.processOracleException( e, uow)
      }
      catch (IOException ioe) {
        ParQueueSQLDAO.processOracleException( ioe, uow)
      }
      catch (FileNotFoundException fe) {
        ParQueueSQLDAO.processOracleException( fe, uow)
      }
      catch (ZipException ze) {
        ParQueueSQLDAO.processOracleException( ze, uow)
      }

  }
  public static void startProcRepDataValidation(ParUnitOfWork uow) {
    //  logger.debug("startProcRepDataValidation");
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcRepDataValidation:PKG_ACES_ASSESS.p_rep_validation: workInstanceId = "+ uow.workInstanceId;
    try {
        def sql = new Sql(uow.dataSource)
        sql.call '{call PKG_ACES_ASSESS.p_rep_validation(?,?,?,?,?,?,?)}',
                [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.TIMESTAMP,Sql.NUMERIC],
                { o_workbook_key,o_email_subj, o_email_body_txt, o_email_from_user,o_report_date, o_return_code ->
                    uow.workbook_key = o_workbook_key
                    uow.email_subj = o_email_subj
                    uow.email_body_txt = o_email_body_txt
                    uow.email_from_user = o_email_from_user
                    uow.report_date    = o_report_date
                    uow.returnStatus = o_return_code
            }
        if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
            procedureStop(uow);
        }

      } catch (Exception e) {
        ParQueueSQLDAO.processOracleException( e, uow)
      }
      catch (IOException ioe) {
        ParQueueSQLDAO.processOracleException( ioe, uow)
      }
      catch (FileNotFoundException fe) {
        ParQueueSQLDAO.processOracleException( fe, uow)
      }
      catch (ZipException ze) {
        ParQueueSQLDAO.processOracleException( ze, uow)
      }
  }

    public static void startProcRepSupAssesment(ParUnitOfWork uow) {
   //     logger.debug("startProcRepSupAssesment");
        procedureAcesRepParms(uow);
        uow.message = progname+":startProcRepSupAssesment:PKG_ACES_ASSESS.p_rep_assessment: workInstanceId = "+ uow.workInstanceId;
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{call PKG_ACES_ASSESS.p_rep_assessment(?,?,?,?,?,?,?)}',
                    [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.TIMESTAMP,Sql.NUMERIC],
                    { o_workbook_key,o_email_subj, o_email_body_txt, o_email_from_user,o_report_date, o_return_code ->
                        uow.workbook_key = o_workbook_key
                        uow.email_subj = o_email_subj
                        uow.email_body_txt = o_email_body_txt
                        uow.email_from_user = o_email_from_user
                        uow.report_date    = o_report_date
                        uow.returnStatus = o_return_code
                    }
            if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
                procedureStop(uow);
            }

        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        catch (IOException ioe) {
            ParQueueSQLDAO.processOracleException( ioe, uow)
        }
        catch (FileNotFoundException fe) {
            ParQueueSQLDAO.processOracleException( fe, uow)
        }
        catch (ZipException ze) {
            ParQueueSQLDAO.processOracleException( ze, uow)
        }
    }

    public static void startProcRepNetAdds(ParUnitOfWork uow) {
        //     logger.debug("startProcRepNetAdds");
        procedureAcesRepParms(uow);
        uow.message = progname+":startProcRepNetAdds:IMR_ACES.pkg_netadds_report: user_name = "+ uow.user_name;
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{call IMR_ACES.pkg_netadds_report(?,?,?,?,?)}',
                    [uow.user_name,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC,Sql.VARCHAR ],
                    { o_txt_file_names,o_zip_file_name, o_return_code, o_return_msg ->
                        uow.txt_file_names = o_txt_file_names
                        uow.zip_file_name    = o_zip_file_name
                        uow.returnStatus = o_return_code
                        uow.message = uow.message+":"+o_return_msg
                    }
            if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
                procedureStop(uow);
            }

        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        catch (IOException ioe) {
            ParQueueSQLDAO.processOracleException( ioe, uow)
        }
        catch (FileNotFoundException fe) {
            ParQueueSQLDAO.processOracleException( fe, uow)
        }
        catch (ZipException ze) {
            ParQueueSQLDAO.processOracleException( ze, uow)
        }
    }

  public static void startProcRepNotes(ParUnitOfWork uow) {

    procedureAcesRepParms(uow);

    uow.message = progname+":startProcRepNotes:PKG_ACES_ASSESS.p_rep_notes: workInstanceId = "+ uow.workInstanceId;
    try {
        def sql = new Sql(uow.dataSource)
        sql.call '{call PKG_ACES_ASSESS.p_rep_notes(?,?,?,?,?,?,?,?)}',
                [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
              { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
                uow.filenames = p_txt_file_names
                uow.zip_file_name = p_zip_file_name
                uow.email = p_email
                uow.email_subj = p_email_subj
                uow.email_body_txt = p_email_body_txt
                uow.email_from_user = p_email_from_user
                uow.returnStatus = p_return_code
            }
        if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
            emailHandler = new EmailHandler(uow);
        }
      } catch (Exception e) {
        ParQueueSQLDAO.processOracleException( e, uow)
      }
      catch (IOException ioe) {
        ParQueueSQLDAO.processOracleException( ioe, uow)
      }
      catch (FileNotFoundException fe) {
        ParQueueSQLDAO.processOracleException( fe, uow)
      }
      catch (ZipException ze) {
        ParQueueSQLDAO.processOracleException( ze, uow)
      }
  }
  public static void startProcRepCCU(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcRepCCU:PKG_ACES_ASSESS.p_rep_aces_ccu: workInstanceId = "+ uow.workInstanceId;
    try {
        def sql = new Sql(uow.dataSource)
        sql.call '{call PKG_ACES_ASSESS.p_rep_aces_ccu(?,?,?,?,?,?,?,?)}',
                [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
              { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
                uow.filenames = p_txt_file_names
                uow.zip_file_name = p_zip_file_name
                uow.email = p_email
                uow.email_subj = p_email_subj
                uow.email_body_txt = p_email_body_txt
                uow.email_from_user = p_email_from_user
                uow.returnStatus = p_return_code
            }
        if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
          emailHandler = new EmailHandler(uow);
        }
      } catch (Exception e) {
        ParQueueSQLDAO.processOracleException( e, uow)
      }
      catch (IOException ioe) {
        ParQueueSQLDAO.processOracleException( ioe, uow)
      }
      catch (FileNotFoundException fe) {
        ParQueueSQLDAO.processOracleException( fe, uow)
      }
      catch (ZipException ze) {
        ParQueueSQLDAO.processOracleException( ze, uow)
      }
  }

  public static void startProcReportCleanse(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcReportCleanse:PKG_IMR_REPORT.p_rep_cleanse: workInstanceId = "+ uow.workInstanceId;
    try {
      def sql = new Sql(uow.dataSource)
      sql.call '{call PKG_IMR_REPORT.p_rep_cleanse(?,?,?,?,?,?,?,?)}',
              [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
            { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
              uow.filenames = p_txt_file_names
              uow.zip_file_name = p_zip_file_name
              uow.email = p_email
              uow.email_subj = p_email_subj
              uow.email_body_txt = p_email_body_txt
              uow.email_from_user = p_email_from_user
              uow.returnStatus = p_return_code
          }
      if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
        emailHandler = new EmailHandler(uow);
      }
    } catch (Exception e) {
      ParQueueSQLDAO.processOracleException( e, uow)
    }
    catch (IOException ioe) {
      ParQueueSQLDAO.processOracleException( ioe, uow)
    }
    catch (FileNotFoundException fe) {
      ParQueueSQLDAO.processOracleException( fe, uow)
    }
    catch (ZipException ze) {
      ParQueueSQLDAO.processOracleException( ze, uow)
    }
}
  public static void startProcReportFileValidate(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcReportFileValidate:PKG_IMR_REPORT.p_rep_file_validate: workInstanceId = "+ uow.workInstanceId;
    try {
      def sql = new Sql(uow.dataSource)
      sql.call '{call PKG_IMR_REPORT.p_rep_file_validate(?,?,?,?,?,?,?,?)}',
              [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
            { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
              uow.filenames = p_txt_file_names
              uow.zip_file_name = p_zip_file_name
              uow.email = p_email
              uow.email_subj = p_email_subj
              uow.email_body_txt = p_email_body_txt
              uow.email_from_user = p_email_from_user
              uow.returnStatus = p_return_code
          }
      if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
        emailHandler = new EmailHandler(uow);
      }
    } catch (Exception e) {
      ParQueueSQLDAO.processOracleException( e, uow)
    }
    catch (IOException ioe) {
      ParQueueSQLDAO.processOracleException( ioe, uow)
    }
    catch (FileNotFoundException fe) {
      ParQueueSQLDAO.processOracleException( fe, uow)
    }
    catch (ZipException ze) {
      ParQueueSQLDAO.processOracleException( ze, uow)
    }
}
  public static void startProcReportLoad(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcReportLoad:PKG_IMR_REPORT.p_rep_load: workInstanceId = "+ uow.workInstanceId;
    try {
      def sql = new Sql(uow.dataSource)
      sql.call '{call PKG_IMR_REPORT.p_rep_load(?,?,?,?,?,?,?,?,?)}',
              [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC,Sql.VARCHAR],
            { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code, p_message ->
              uow.filenames = p_txt_file_names
              uow.zip_file_name = p_zip_file_name
              uow.email = p_email
              uow.email_subj = p_email_subj
              uow.email_body_txt = p_email_body_txt
              uow.email_from_user = p_email_from_user
              uow.returnStatus = p_return_code
              uow.message = uow.message+":"+p_message
          }
      if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
        emailHandler = new EmailHandler(uow);
      }
    } catch (Exception e) {
      ParQueueSQLDAO.processOracleException( e, uow)
    }
    catch (IOException ioe) {
      ParQueueSQLDAO.processOracleException( ioe, uow)
    }
    catch (FileNotFoundException fe) {
      ParQueueSQLDAO.processOracleException( fe, uow)
    }
    catch (ZipException ze) {
      ParQueueSQLDAO.processOracleException( ze, uow)
    }
}
    public static void startProcRepPartGuide(ParUnitOfWork uow) {
        uow.message = progname+":startProcRepPartGuide:PKG_COMMON.f_get_env_value: PartGuideTransferUser = "+ "PartGuideTransferUser";
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"PartGuideTransferUser"],
                    { p_user ->
                        uow.newftpUsername = p_user
                    }
         } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        uow.message = progname+":startProcRepPartGuide:PKG_COMMON.f_get_env_value: PartGuideTransferPwd = "+ "PartGuideTransferPwd";
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"PartGuideTransferPwd"],
                    { p_pwd ->
                        uow.newftpPassword = p_pwd
                    }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        uow.message = progname+":startProcRepPartGuide:PKG_COMMON.f_get_env_value: PartGuideTransferUrl = "+ "PartGuideTransferUrl";
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"PartGuideTransferUrl"],
                    { p_url ->
                        uow.newassessmentConnection = p_url
                    }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"PartGuideTransferDir"],
                    { p_dir ->
                        uow.newassessmentPath = p_dir
                    }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        String msg = " uow.piesConnection  = " + uow.ftpConnection  + " uow.newftpUsername = "+uow.newftpUsername + " uow.newftpPassword = "+uow.newftpPassword;
     //   logger.debug(msg);
        uow.message = progname+":startProcRepPartGuide:PKG_PIES_ASSESS.P_PARTGUIDE_TRANSFER: workInstanceId = "+ uow.workInstanceId;
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{call PKG_PIES_ASSESS.P_PARTGUIDE_TRANSFER(?,?,?,?,?,?,?,?)}',
                    [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
                    { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
                        uow.filenames = p_txt_file_names
                        uow.zip_file_name = p_txt_file_names
                        uow.email = p_email
                        uow.email_subj = p_email_subj
                        uow.email_body_txt = p_email_body_txt
                        uow.email_from_user = p_email_from_user
                        uow.returnStatus = p_return_code
                    }
             msg = " uow.returnStatus  = " + uow.returnStatus;
            logger.debug(msg);

            if (uow.returnStatus == 0 || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
                emailHandlerPiesSftp = new EmailHandlerPiesSftp(uow);
            }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        catch (IOException ioe) {
            ParQueueSQLDAO.processOracleException( ioe, uow)
        }
        catch (FileNotFoundException fe) {
            ParQueueSQLDAO.processOracleException( fe, uow)
        }
        catch (ZipException ze) {
            ParQueueSQLDAO.processOracleException( ze, uow)
        }
    }
  public static void startProcReportInvalidchar(ParUnitOfWork uow)
  {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcReportInvalidchar:PKG_ACES_ASSESS.p_rep_inv_char: workInstanceId = "+ uow.workInstanceId;
    try {
      def sql = new Sql(uow.dataSource)
      sql.call '{call PKG_ACES_ASSESS.p_rep_inv_char(?,?,?,?,?,?,?,?)}',
              [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
            { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
              uow.filenames = p_txt_file_names
              uow.zip_file_name = p_zip_file_name
              uow.email = p_email
              uow.email_subj = p_email_subj
              uow.email_body_txt = p_email_body_txt
              uow.email_from_user = p_email_from_user
              uow.returnStatus = p_return_code
          }
      if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
        emailHandler = new EmailHandler(uow);
      }
    } catch (Exception e) {
      ParQueueSQLDAO.processOracleException( e, uow)
    }
    catch (IOException ioe) {
      ParQueueSQLDAO.processOracleException( ioe, uow)
    }
    catch (FileNotFoundException fe) {
      ParQueueSQLDAO.processOracleException( fe, uow)
    }
    catch (ZipException ze) {
      ParQueueSQLDAO.processOracleException( ze, uow)
    }

}
  public static void startProcReportMapVeh(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcReportMapVeh:PKG_ACES_NETADDS.p_rep_unmapped_vehicle: workInstanceId = "+ uow.workInstanceId;
    try {
      def sql = new Sql(uow.dataSource)
      sql.call '{call PKG_ACES_NETADDS.p_rep_unmapped_vehicle(?,?,?,?,?,?,?,?,?)}',
              [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC,Sql.VARCHAR],
            { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code,p_message ->
              uow.filenames = p_txt_file_names
              uow.zip_file_name = p_zip_file_name
              uow.email = p_email
              uow.email_subj = p_email_subj
              uow.email_body_txt = p_email_body_txt
              uow.email_from_user = p_email_from_user
              uow.returnStatus = p_return_code
              uow.message = uow.message+":"+p_message
          }
      if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
        emailHandler = new EmailHandler(uow);
      }
    } catch (Exception e) {
      ParQueueSQLDAO.processOracleException( e, uow)
    }
    catch (IOException ioe) {
      ParQueueSQLDAO.processOracleException( ioe, uow)
    }
    catch (FileNotFoundException fe) {
      ParQueueSQLDAO.processOracleException( fe, uow)
    }
    catch (ZipException ze) {
      ParQueueSQLDAO.processOracleException( ze, uow)
    }
}
  public static void startProcReportMapLCPD(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcReportMapLCPD:PKG_ACES_NETADDS.p_rep_lc_pd_map: workInstanceId = "+ uow.workInstanceId;
    try {
      def sql = new Sql(uow.dataSource)
      sql.call '{call PKG_ACES_NETADDS.p_rep_lc_pd_map(?,?,?,?,?,?,?,?,?,?)}',
                  [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.TIMESTAMP,Sql.NUMERIC,Sql.VARCHAR],
                { o_txt_file_names,o_email,o_email_subj, o_email_body_txt, o_from_user,o_workbook_name,o_rpt_date, o_return_code, o_message->
                    uow.filenames= o_txt_file_names
                    uow.email = o_email
                    uow.email_subj = o_email_subj
                    uow.email_body_txt = o_email_body_txt
                    uow.email_from_user = o_from_user
                    uow.workbook_key = o_workbook_name
                    uow.report_date    = o_rpt_date
                    uow.returnStatus = o_return_code
                    uow.message = uow.message+":"+o_message
          }
        if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
            procedureStop(uow);
        }
    } catch (Exception e) {
      ParQueueSQLDAO.processOracleException( e, uow)
    }
    catch (IOException ioe) {
      ParQueueSQLDAO.processOracleException( ioe, uow)
    }
    catch (FileNotFoundException fe) {
      ParQueueSQLDAO.processOracleException( fe, uow)
    }
    catch (ZipException ze) {
      ParQueueSQLDAO.processOracleException( ze, uow)
    }
}
    public static void startProcReportNetaddsWip(ParUnitOfWork uow) {
        procedureAcesRepParms(uow);
        uow.message = progname+":startProcReportNetaddsWip:PKG_ACES_NETADDS.p_rep_netadds: workInstanceId = "+ uow.workInstanceId;
        try {
            def sql = new Sql(uow.dataSource)


            sql.call '{call PKG_ACES_NETADDS.p_rep_netadds(?,?,?,?,?,?,?,?,?)}',
                    [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC,Sql.VARCHAR],
                    { o_txt_file_names, o_zip_file_name, o_email, o_email_subj, o_email_body_txt, o_from_user, o_return_code, o_return_msg ->
                        uow.filenames = o_txt_file_names
                        uow.zip_file_name = o_zip_file_name
                        uow.email = o_email
                        uow.email_subj = o_email_subj
                        uow.email_body_txt = o_email_body_txt
                        uow.email_from_user = o_from_user
                        uow.returnStatus = o_return_code
                        uow.message = uow.message+":"+o_return_msg
                    }
            if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
                emailHandler = new EmailHandler(uow);
            }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        catch (IOException ioe) {
            ParQueueSQLDAO.processOracleException( ioe, uow)
        }
        catch (FileNotFoundException fe) {
            ParQueueSQLDAO.processOracleException( fe, uow)
        }
        catch (ZipException ze) {
            ParQueueSQLDAO.processOracleException( ze, uow)
        }
}
  public static void startProcReportDupePd(ParUnitOfWork uow) {
    procedureAcesRepParms(uow);
    uow.message = progname+":startProcReportDupePd:PKG_ACES_NETADDS.p_rep_dupe_pd: workInstanceId = "+ uow.workInstanceId;
    try {
      def sql = new Sql(uow.dataSource)
      sql.call '{call PKG_ACES_NETADDS.p_rep_dupe_pd(?,?,?,?,?,?,?,?,?)}',
              [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC,Sql.VARCHAR],
            { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code, p_message ->
              uow.filenames = p_txt_file_names
              uow.zip_file_name = p_zip_file_name
              uow.email = p_email
              uow.email_subj = p_email_subj
              uow.email_body_txt = p_email_body_txt
              uow.email_from_user = p_email_from_user
              uow.returnStatus = p_return_code
              uow.message = uow.message+":"+p_message
          }
      if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
        emailHandler = new EmailHandler(uow);
      }
    } catch (Exception e) {
      ParQueueSQLDAO.processOracleException( e, uow)
    }
    catch (IOException ioe) {
      ParQueueSQLDAO.processOracleException( ioe, uow)
    }
    catch (FileNotFoundException fe) {
      ParQueueSQLDAO.processOracleException( fe, uow)
    }
    catch (ZipException ze) {
      ParQueueSQLDAO.processOracleException( ze, uow)
    }
}
    public static void startProcInvCharUnSuppPtype(ParUnitOfWork uow) {
        procedureAcesRepParms(uow);
        uow.message = progname+":startProcInvCharUnSuppPtype:pkg_aces_asses.p_rep_unsupp_ptpos: workInstanceId = "+ uow.workInstanceId;
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{call pkg_aces_assess.p_rep_unsupp_ptpos(?,?,?,?,?,?,?,?)}',
                    [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
                    { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
                        uow.filenames = p_txt_file_names
                        uow.zip_file_name = p_zip_file_name
                        uow.email = p_email
                        uow.email_subj = p_email_subj
                        uow.email_body_txt = p_email_body_txt
                        uow.email_from_user = p_email_from_user
                        uow.returnStatus = p_return_code
                    }
            if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
                emailHandler = new EmailHandler(uow);
            }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        catch (IOException ioe) {
            ParQueueSQLDAO.processOracleException( ioe, uow)
        }
        catch (FileNotFoundException fe) {
            ParQueueSQLDAO.processOracleException( fe, uow)
        }
        catch (ZipException ze) {
            ParQueueSQLDAO.processOracleException( ze, uow)
        }
    }
    public static void startProcInvCharUnMapPtype(ParUnitOfWork uow) {
        procedureAcesRepParms(uow);
        uow.message = progname+":startProcInvCharUnMapPtype:pkg_aces_assess.p_rep_unmap_ptpos: workInstanceId = "+ uow.workInstanceId;
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{call pkg_aces_assess.p_rep_unmap_ptpos(?,?,?,?,?,?,?,?)}',
                    [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
                    { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
                        uow.filenames = p_txt_file_names
                        uow.zip_file_name = p_zip_file_name
                        uow.email = p_email
                        uow.email_subj = p_email_subj
                        uow.email_body_txt = p_email_body_txt
                        uow.email_from_user = p_email_from_user
                        uow.returnStatus = p_return_code
                    }
            if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
                emailHandler = new EmailHandler(uow);
            }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        catch (IOException ioe) {
            ParQueueSQLDAO.processOracleException( ioe, uow)
        }
        catch (FileNotFoundException fe) {
            ParQueueSQLDAO.processOracleException( fe, uow)
        }
        catch (ZipException ze) {
            ParQueueSQLDAO.processOracleException( ze, uow)
        }
    }
    public static void startProcUnmapSupPt(ParUnitOfWork uow) {
        procedureAcesRepParms(uow);
        uow.message = progname+":startProcUnmapSupPt:pkg_aces_assess.p_rep_coverage: workInstanceId = "+ uow.workInstanceId;
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{call pkg_aces_assess.p_rep_coverage(?,?,?,?,?,?,?,?)}',
                    [uow.workInstanceId,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.VARCHAR,Sql.NUMERIC],
                    { p_txt_file_names, p_zip_file_name, p_email, p_email_subj, p_email_body_txt, p_email_from_user, p_return_code ->
                        uow.filenames = p_txt_file_names
                        uow.zip_file_name = p_zip_file_name
                        uow.email = p_email
                        uow.email_subj = p_email_subj
                        uow.email_body_txt = p_email_body_txt
                        uow.email_from_user = p_email_from_user
                        uow.returnStatus = p_return_code
                    }
            if (uow.returnStatus == procedureConstants.REPORT_SUCCESS || uow.returnStatus == procedureConstants.REPORT_NODATA_FOUND) {
                emailHandler = new EmailHandler(uow);
            }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        catch (IOException ioe) {
            ParQueueSQLDAO.processOracleException( ioe, uow)
        }
        catch (FileNotFoundException fe) {
            ParQueueSQLDAO.processOracleException( fe, uow)
        }
        catch (ZipException ze) {
            ParQueueSQLDAO.processOracleException( ze, uow)
        }
    }
    public static void startDeliverReport(ParUnitOfWork uow) {
        procedureAcesRepParms(uow);
        System.out.println("startDelivRepDataValidation");
        logger.debug("startDelivRepDataValidation");
        logger.debug(uow.task_params);
        String[] taskparams2 =  uow.task_params.split("\\|");
        uow.filenames = taskparams2[0]
        uow.zip_file_name = taskparams2[0].replace("xlsx", "zip");
    //    uow.email = taskparams2[1]
        uow.email_subj = taskparams2[2]
        uow.email_body_txt = taskparams2[3]
        uow.email_from_user = 'IMR User'
        logger.debug("\n!--startDelivRepDataValidation--!  \n"+ uow.zip_file_name);
                emailHandler = new EmailHandler(uow);
                uow.returnStatus=0;


    }
    public static void procedureStop(ParUnitOfWork uow) {
   //     logger.debug("procedureStop uow.id= " +uow.id);
        def sql = null;
        def workTaskStatusIxt = null;
        def date = null;
        uow.nostopproc=1;
        uow.report_date=uow.report_date.replace(" ", "");
        uow.report_date=uow.report_date.replace("-", "");
        uow.report_date=uow.report_date.replace(":", "");
        uow.report_date=uow.report_date.substring(0,12);
        String taskparamsPTC=uow.email+'|'+ uow.contentId+'|'+uow.email_subj+'|'+uow.email_body_txt+'|IMR User <NoReply@NoSpam.com>|'+uow.workbook_key+'|'+ uow.report_date;
  //      logger.debug("taskparamsPTC:"+taskparamsPTC);
        uow.message = progname+":stopReportWorktask:PKG_WORK_TASK_QUEUE.p_work_task_stopped: workInstanceId = "+ uow.workInstanceId;
        try {
            sql = new Sql(uow.dataSource)
            sql.call '{call PKG_WORK_TASK_QUEUE.p_work_task_stopped(?,?,?,?,?,?,?,?)}',
                    [uow.id,
                            uow.taskStatusId,
                            1,
                            uow.message,
                            taskparamsPTC,
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
        logger.debug("SQL : p_work_task_stopped:");
    }
    public static void procedureAcesRepParms(ParUnitOfWork uow) {
        uow.message = progname+":procedureAcesRepParms";
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"AcesRepTransferUser"],
                    { p_user ->
                        uow.newftpUsername = p_user
                    }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"AcesRepTransferPwd"],
                    { p_pwd ->
                        uow.newftpPassword = p_pwd
                    }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
       try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"AcesRepTransferUrl"],
                    { p_url ->
                        uow.newassessmentConnection = p_url
                    }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        try {
            def sql = new Sql(uow.dataSource)
            sql.call '{? = call PKG_COMMON.f_get_env_value(?)}',
                    [Sql.VARCHAR,"AcesRepTransferDir"],
                    { p_dir ->
                        uow.newassessmentPath = p_dir
                    }
        } catch (Exception e) {
            ParQueueSQLDAO.processOracleException( e, uow)
        }
        String msg = " uow.ftpConnection  = " + uow.newassessmentConnection  + " uow.newftpUsername = "+uow.newftpUsername + " uow.newftpPassword = "+uow.newftpPassword;
    //    logger.debug(msg);
    }
}