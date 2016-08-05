package com.epicor.io

import com.epicor.uow.ParUnitOfWork
import com.jcraft.jsch.Channel
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.apache.commons.net.ftp.FTP

import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipOutputStream
import javax.mail.internet.MimeMessage
import org.apache.commons.net.ftp.FTPClient
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.mail.javamail.JavaMailSenderImpl
import org.springframework.mail.javamail.MimeMessageHelper
import com.epicor.imr.dao.FtpServerDAO
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.SftpException

/**
 * Created with IntelliJ IDEA.
 * User: kevin.torbett
 * Date: 11/18/15
 * Time: 1:45 PM
 * To change this template use File | Settings | File Templates.
 */
class EmailHandlerPies extends FtpServerDAO {
    private static final Log logger = LogFactory.getLog(com.epicor.io.EmailHandlerPies.class);

    ParUnitOfWork uow = new ParUnitOfWork();

    /**
     * List of methods that will execute to send email
     * containing the Report File.
     * @param uowIn Parallel Poller Unit of Work.
     * @throws Exception
     */
    public EmailHandlerPies(ParUnitOfWork uowIn) throws Exception {

        uow = uowIn;
        uow.ftpServer=uow.piesConnection;
        String msg = "EmailHandlerPies uow.ftpServer  = " + uow.ftpServer  + " uow.filenames = "+uow.filenames + " uow.piesftpUsername = "+uow.piesftpUsername + " uow.piesftpPassword = "+uow.piesftpPassword + " uow.ftpPort = "+uow.ftpPort+ " uow.tempPath = "+uow.tempPath+ " uow.zip_file_name = "+uow.zip_file_name;
        uow.piesPath="c:/test/";
        List<String> srcFiles;
        List<String> destFiles;
        destFiles = stripReportsUserNames();
        srcFiles = parseReports();
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
            for (String filename : tokens) {
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
                    } else {
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

    public List<String> parseReports() {
        if (!uow?.filenames || uow?.filenames == "")
            return null;

        List<String> srcFiles = new ArrayList<String>();

        String[] tokens = uow.filenames.split("\\|");
        // Check if there are any filenames
        if (tokens.length > 0) {
            // Iterate through each one.
            for (String filename : tokens) {
                // Check if a filename exist
                if (filename != null && !filename.isEmpty()) {
                    // Add to source filename array to be returned.
                    srcFiles.add(uow.reportPath+"/transfers" + "/" + filename);
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
    public void zipReports(List<String> srcFiles, List<String> destFiles) {
        if (!srcFiles || srcFiles == "") {
            uow?.zip_file_name = null;
            return;
        }

        // Build the Zip file with one or more report text file
        try {
            byte[] buffer = new byte[1024];  // create a byte buffer

            // Create object of FileOutputStream
            FileOutputStream fout = new FileOutputStream(uow?.tempPath + "/" + uow?.zip_file_name);
            if (logger.isDebugEnabled()) {
                logger.debug("ZipFile = " + uow?.tempPath + "/" + uow?.zip_file_name);
            }
            logger.debug("ZipFile = " + uow?.tempPath + "/" + uow?.zip_file_name);
            // Create object of ZipOutputStream from FileOutputSteam
            ZipOutputStream zout = new ZipOutputStream(fout);

            for (int i = 0; i < srcFiles.size(); i++) {
                if (logger.isDebugEnabled()) {
                    System.out.println("Adding " + srcFiles[i]);
                    logger.debug("Adding " + srcFiles[i]);
                }

                // Create object of FileInputStream for source file
                FileInputStream fin = new FileInputStream(srcFiles[i]);

                /*
                * Writing ZipEntry in the zip file, use
                * This method begins writing a new Zip entry to
                * the zip file and positions the stream to the start
                * of the entry data.
                */
                zout.putNextEntry(new ZipEntry(destFiles[i]));

                /*
                * After creating entry in the zip file, actually
                * write the file.
                */
                int length;
                while ((length = fin.read(buffer)) > 0) {
                    zout.write(buffer, 0, length);
                }

                /*
                * After writing the file to ZipOutputStream, use
                *
                * void closeEntry() method of ZipOutputStream class to
                * close the current entry and position the stream to
                * write the next entry.
                */
                zout.closeEntry();

                // Close the InputStream
                fin.close();

            }

            // Close the ZipOutputStream
            zout.close();
            if (logger.isDebugEnabled()) {
                System.out.println("Zip file has been created!");
                logger.debug("Zip file ${uow.zip_file_name} has been created!");
            }
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
        String msg = "moveReportToFTP uow.ftpServer  = " + uow.ftpServer  + " uow.piesftpUsername = "+uow.piesftpUsername + " uow.piesftpPassword = "+uow.piesftpPassword + " uow.ftpPort = "+uow.ftpPort+ " uow.tempPath = "+uow.tempPath+ " uow.zip_file_name = "+uow.zip_file_name;
        uow.piesPath="/"
        logger.debug(msg);
        InputStream input  = new FileInputStream(new File(uow.reportPath+"/transfers" + "/" + uow.zip_file_name));
        // Login into the FTP server
        FTPClient ftpClient = new FTPClient()
        try {
            // Create a new session

            ftpClient.connect(uow.ftpServer );
            ftpClient.login(uow.piesftpUsername,uow.piesftpPassword );
            ftpClient.changeWorkingDirectory(uow.piesPath);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.storeFile(uow.zip_file_name,input);
         //   ftpClient.disconnect();

          }
       catch (JSchException je) {
           logger.debug('Pies FTp failed');
      //      // TODO: process the exception
          je.printStackTrace();
       }
        finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        logger.debug("emailAddresses " + emailAddresses);
        // Create the Java mail sender object.
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(uow?.emailHost);
        MimeMessage message = sender.createMimeMessage();
        logger.debug("Download Report = " + uow.piesConnection + "/" + uow?.zip_file_name);
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
