/*
  */
package com.comp.io

import com.comp.uow.ParUnitOfWork
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream
import javax.mail.internet.MimeMessage
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import com.comp.imr.dao.FtpServerDAO
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.SftpException

class EmailHandlerSftp extends FtpServerDAO {
    private static final Log logger = LogFactory.getLog(com.comp.io.EmailHandler.class);

    ParUnitOfWork uow = new ParUnitOfWork();

    /**
     * List of methods that will execute to send email
     * containing the Report File.
     * @param uowIn Parallel Poller Unit of Work.
     * @throws Exception
     */
    public EmailHandlerPiesSftp(ParUnitOfWork uowIn) throws Exception {
        uow = uowIn;
   //     String msg = "EmailHandler uow.ftpServer  = " + uow.ftpServer  + " uow.filenames = "+uow.filenames + " uow.ftpUsername = "+uow.ftpUsername + " uow.ftpPassword = "+uow.ftpPassword + " uow.ftpPort = "+uow.ftpPort+ " uow.tempPath = "+uow.tempPath+ " uow.zip_file_name = "+uow.zip_file_name;
    //    logger.debug(msg);

        List<String> srcFiles;
        List<String> destFiles;
        destFiles = stripReportsUserNames();
        srcFiles = parseReports();
        zipReports(srcFiles,destFiles);
        moveReportToFTP();
        sendEmail();
    }

    /**
     * Strips the Report Usernames returning a list of
     * Source Files to build the zip file.
     * @return
     */
    public List<String> stripReportsUserNames() {
        // Check if there are any filenames.
        if (!uow?.filenames || uow?.filenames == "")
            return null;

        List<String> srcFiles = new ArrayList<String>();

        String[] tokens = uow.filenames.split("\\|");
        // Check if there are any filenames
        if (tokens.length > 0) {
            // Iterate through each one.
            for (String filename: tokens) {
                // Check if a filename exist
                if (filename != null && !filename.isEmpty()) {
                    int stripBeginIndx = 0;
                    int stripEndIndx;
                    // Strip the username e.g. RSmith_deaproducts-mounts-11-7-14_Notes_201504071536.tab
                    stripEndIndx = filename.indexOf("_", stripBeginIndx);
                    String srcFile;
                    // Check if username existed.
                    if (stripEndIndx == -1) {
                        srcFile = filename.substring(stripBeginIndx);
                    }
                    else {
                        srcFile = filename.substring(stripEndIndx + 1);
                    }
                    // Add to source filename array to be returned.
                    srcFiles.add(srcFile);
                }
            }
        }

        return srcFiles;
    }

    /**
     * Parses the Report file name
     * @return
     */

    public List<String>  parseReports() {
        if (!uow?.filenames || uow?.filenames == "")
            return null;

        List<String> srcFiles = new ArrayList<String>();

        String[] tokens = uow.filenames.split("\\|");
        // Check if there are any filenames
        if (tokens.length > 0) {
            // Iterate through each one.
            for (String filename: tokens) {
                // Check if a filename exist
                if (filename != null && !filename.isEmpty()) {
                    // Add to source filename array to be returned.
                    srcFiles.add(uow.reportPath + "/" + filename);
                }
            }
        }

        return srcFiles;
    }

    /**
     *  Zip the list of Source Files.
     * @param srcFiles list of report files to zip.
     * @param destFiles the destination file.
     */
    public void zipReports(List<String> srcFiles,List<String> destFiles) {
        if(!srcFiles || srcFiles == "") {
            uow?.zip_file_name = null;
            return;
        }

        // Build the Zip file with one or more report text file
        try {
            byte[] buffer = new byte[1024];  // create a byte buffer

            // Create object of FileOutputStream
            FileOutputStream fout = new FileOutputStream(uow?.tempPath + "/" + uow?.zip_file_name);
            if (logger.isDebugEnabled()) {
                logger.debug("ZipFile = "+uow?.tempPath + "/" + uow?.zip_file_name);
            }
         //   logger.debug("ZipFile = "+uow?.tempPath + "/" + uow?.zip_file_name);
            // Create object of ZipOutputStream from FileOutputSteam



        }
        catch (IOException ioe) {
            if (logger.isDebugEnabled()) {
                System.out.println("zipReports IOException :" + ioe);
                logger.debug("zipReports IOException :" + ioe);
            }
            throw ioe;
        }
        catch (FileNotFoundException fe) {
            if (logger.isDebugEnabled()) {
                System.out.println("zipReports FileNotFoundException :" + fe);
                logger.debug("zipReports FileNotFoundException :" + fe);
            }
            throw fe;
        }
        catch (ZipException ze) {
            if (logger.isDebugEnabled()) {
                System.out.println("zipReports ZipException :" + ze);
                logger.debug("zipReports ZipException :" + ze);
            }
            throw ze;
        }
        catch (Exception e) {
            if (logger.isDebugEnabled()) {
                System.out.println("zipReports Exception :" + e);
                logger.debug("zipReports Exception :" + e);
            }
            throw e;
        }
    }

    /**
     * Moved the Zipped Report Files to the FTP servers
     */
    public void moveReportToFTP() {
        ChannelSftp channelSftp = null;
        String msg = "moveReportToFTP uow.newassessmentConnection  = " + uow.newassessmentConnection  + " uow.newftpUsername = "+uow.newftpUsername + " uow.newftpPassword = "+uow.newftpPassword + " uow.newassessmentPath = "+uow.newassessmentPath + " uow.reportPath = "+uow.reportPath+ " uow.zip_file_name = "+uow.zip_file_name;
        logger.debug(msg);

        // Login into the FTP server
        try {
            channelSftp = ftpLogin(uow.newassessmentConnection, uow.newftpUsername, uow.newftpPassword, uow.ftpPort);
        }
        catch (JSchException je) {
            // TODO: process the exception
            je.printStackTrace();
        }
        // Move Zipped Report File to the FTP server
        try {
            moveReportFile(channelSftp,uow.reportPath+"/transfers", uow.newassessmentPath, uow.zip_file_name);
        }
        catch (SftpException se) {
            // TODO: process the exception
             msg = "FAILED moveReportToFTP uow.newassessmentConnection  = " + uow.newassessmentConnection  + " uow.newftpUsername = "+uow.newftpUsername + " uow.newftpPassword = "+uow.newftpPassword + " uow.newassessmentPath = "+uow.newassessmentPath + " uow.reportPath = "+uow.reportPath+ " uow.zip_file_name = "+uow.zip_file_name;
            logger.debug(msg);
            se.printStackTrace();
        }
        // Logout from the FTP server
        try {
            ftpLogout(channelSftp);
        }
        catch (JSchException je) {
            // TODO: process the exception
             msg = "FAILED moveReportToFTP uow.newassessmentConnection  = " + uow.newassessmentConnection  + " uow.newftpUsername = "+uow.newftpUsername + " uow.newftpPassword = "+uow.newftpPassword + " uow.newassessmentPath = "+uow.newassessmentPath + " uow.reportPath = "+uow.reportPath+ " uow.zip_file_name = "+uow.zip_file_name;
            logger.debug(msg);
            je.printStackTrace();
        }
    }

    /**
     * Build and Send the Email containing the Report File link
     * Note, the email body does not contain the html <a href> with a
     * display label.  The email body did not consume the html command.
     */
    public void sendEmail() {
        // Build the list of email address to send the report to.
        if (uow.email == null) {
            logger.error("!--ERROR--! no email address");
            System.out.println("!--ERROR--! no email address");
            uow.message = "!--ERROR--! no email address";
            uow.returnStatus = 1036;
            return;
        }
        String[] emailAddresses = (uow?.email).split(",");
    //    logger.debug("emailAddresses " + emailAddresses);
        // Create the Java mail sender object.
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(uow?.emailHost);
        MimeMessage message = sender.createMimeMessage();
    //    logger.debug("Download Report = " + uow.piesConnection + "/" + uow?.zip_file_name);
        // Create the MIME style email message helper
        // Use the true flag to indicate you need a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(emailAddresses);
        helper.setSubject(uow?.email_subj);
        helper.setFrom("NoReply@NoSpam.com", uow.email_from_user.trim());
        // Build the email text body.
        String text = uow?.email_body_txt;
        helper.setText(text);

        // Send the Report.
        sender.send(message);
        if (logger.isDebugEnabled()) {
            System.out.println("Sent the Report! file ${uow?.zip_file_name}");
            logger.debug("Sent the Report file ${uow?.zip_file_name}!");
        }
    }
}
