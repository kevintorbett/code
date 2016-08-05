package com.project.db.controllers.userMaint

import com.project.db.ReturnCodes
import com.project.db.controllers.ControllerHelper
import com.project.db.controllers.UrlIdMap
import com.project.db.controllers.annotations.Comment
import com.project.db.domain.userMaint.ApplicationGroupView
import com.project.db.domain.userMaint.GroupUserView
import com.project.db.domain.userMaint.UserApplicationView
import com.project.db.services.userMaint.ApplicationsGroupsService
import com.project.db.services.userMaint.GroupUserService
import com.project.db.services.userMaint.UserApplicationsService
import org.springframework.beans.factory.annotation.Required
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONElement

/**
 * Created with IntelliJ IDEA.
 * User: kevin.torbett
 * Date: 7/21/15
 * Time: 11:14 AM
 * To change this template use File | Settings | File Templates.
 */
class UserApplicationsController extends ControllerHelper  {
    def userApplicationsService;

    static allowedMethods = [
            showApps: ['GET'] ,
            showGroups: ['GET']
    ]
     @Comment(comment = """Search against User for applications.",
    Required: 'userId=' """,
  pagination = false, searchCriteria = false)
  def showApps = {
    List<Map<String, Object>> returnValues = null;
    String userId = params.getProperty('userId');  // get the Content Description context string.


    try {
          returnValues = userApplicationsService.getUserApplicationList(userId);
    } catch (Exception e) {
      response.status = ReturnCodes.HTTP_SERVER_ERROR;
      render "Unexpected Exception: ${e.getMessage()}";
      return;
    }

    renderReturn(returnValues);
  }
    @Comment(comment = """Search against applications for user groups.",
    Required: 'userId=' """,
    pagination = false, searchCriteria = false)
    def showGroups = {
        List<Map<String, Object>> returnValues = null;
        String userId = params.getProperty('userId');  // get the Content Description context string.
        String appId = params.getProperty('appId');

        try {
            returnValues = userApplicationsService.getApplicationGroupList(userId,appId);
        } catch (Exception e) {
            response.status = ReturnCodes.HTTP_SERVER_ERROR;
            render "Unexpected Exception: ${e.getMessage()}";
            return;
        }

        renderReturn(returnValues);
    }


}