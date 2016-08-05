package com.xxx.db.controllers.security

import com.xxx.db.controllers.security.annotations.dbSecurity
import com.xxx.db.controllers.UrlIdMap
import com.xxx.db.controllers.ControllerHelper
import com.xxx.db.domain.security.Application
import com.xxx.db.controllers.annotations.Comment
import com.xxx.db.domain.security.Group

import com.xxx.db.domain.security.Role
import com.xxx.db.domain.security.GroupRoleJoin
import com.xxx.db.domain.security.Permission

class ApplicationController extends ControllerHelper {
  def applicationService

  static allowedMethods = [
          show: "GET", showGroup: "GET", showRole: "GET", showGroupRole: "GET", showPermission: "GET", showMigration: "GET",
          save: "POST", saveGroup: "POST", saveRole: "POST", saveGroupRole: "POST", savePermission: "POST",
          update: "PUT", updateGroup: "PUT", updateRole: "PUT", updateGroupRole: "PUT", updatePermission: "PUT",
          remove: "DELETE", removeGroup: "DELETE", removeRole: "DELETE", removeGroupRole: "DELETE", removePermission: "DELETE"]

  @Comment(comment = "Search against application(s)",
  pagination = true, searchCriteria = true, max = 100, sortable = ['*'],
  returnClazz = Application.class)
  @dbSecurity(allowable = ['everyone'])
  def show = {
    objectShow(
            [
                    new UrlIdMap(service: applicationService, action: 'searchApplications', ids: ['searchCriteria', 'pagination'],
                            otherParms: ['searchCriteria': getSearchCriteria(), 'pagination': getPagination(0, 100, ["*"])])
            ]
    );
  }

  @Comment(comment = "Search against Groupings tied to Applications.",
  pagination = true, searchCriteria = true, max = 100, sortable = ['*'],
  returnClazz = Group.class)
  def showGroup = {
    objectShow(
            [
                    new UrlIdMap(service: applicationService, action: 'searchGroups', ids: ['searchCriteria', 'pagination'],
                            otherParms: ['searchCriteria': getSearchCriteria(), 'pagination': getPagination(0, 100, ["*"])])
            ]
    );
  }

 

  //======================================================

  @Comment(comment = "Create application(s).Required: ['name'] Optional: ['description', 'source', 'icon', 'iconLabel', 'applicationVersion', 'multiInstance', 'openSeparate', 'defaultFlag']")
  def save = {
    objectSave(Application.class,
            new UrlIdMap(service: applicationService, ids: ['name'], action: 'getApplicationByStringName'),
            applicationService, 'saveApplication',
            ['name', 'description', 'source', 'icon', 'iconLabel', 'applicationVersion', 'multiInstance', 'openSeparate', 'defaultFlag']
    );
  }

  @Comment(comment = "Create Role Group(s). Required: ['application.id', 'name'] Optional: ['description']")
  def saveGroup = {
    objectSave(Group.class,
            new UrlIdMap(service: applicationService, ids: ['application.id', 'name'], action: 'getGroupByApplicationAndName'),
            applicationService, 'saveGroup',
            ['application.id', 'name', 'description']
    );
  }

  

  //======================================================

  @Comment(comment = "Update application(s). Required: ['id'] Updatable: ['description', 'source', 'icon', 'iconLabel', 'applicationVersion','multiInstance', 'openSeparate']")
  def update = {
    objectUpdate(Application.class,
            new UrlIdMap(service: applicationService, ids: ['id'], action: 'getApplicationByLong'),
            applicationService, 'updateApplication',
            ['description', 'source', 'icon', 'iconLabel', 'applicationVersion', 'multiInstance', 'openSeparate', 'defaultFlag']);
  }

  @Comment(comment = "Update group(s). Required: [id] Updateable: ['description']")
  def updateGroup = {
    objectUpdate(Group.class,
            new UrlIdMap(service: applicationService, ids: ['id'], action: 'getGroup'),
            applicationService, 'updateGroup',
            ['description']);
  }

 

  //======================================================

  @Comment(comment = "Remove application(s). Required: ['id']")
  def remove = {
    objectRemove(Application.class,
            new UrlIdMap(service: applicationService, ids: ['id'], action: 'getApplicationByLong'),
            applicationService, 'deleteApplication')
  }

  @Comment(comment = "Remove group(s). Required: ['id']")
  def removeGroup = {
    objectRemove(Group.class,
            new UrlIdMap(service: applicationService, ids: ['id'], action: 'getGroup'),
            applicationService, 'deleteGroup')
  }

 
}
