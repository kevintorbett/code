package com.xxx.db.services.security

import grails.orm.PagedResultList

import com.xxx.db.domain.security.Application
import com.xxx.db.domain.security.Setting
import com.xxx.db.domain.security.Role
import com.xxx.db.domain.security.GroupRoleJoin
import com.xxx.db.domain.security.UserGroupJoin
import com.xxx.db.domain.security.Group

import org.hibernate.FetchMode

import com.xxx.db.services.SearchHelper
import com.xxx.db.services.SearchCriteria
import com.xxx.db.domain.security.Permission
import groovy.sql.Sql
import com.xxx.db.Exception


class ApplicationService {
  def dataSource;
  def sessionFactory;

  boolean transactional = true

  SearchHelper helper = new SearchHelper();

  void deleteApplication(Application application) {
    //TODO: Get table and filed names dynamically for below native queries.
    //This sucks but is only way I could get it to work:
    def sql = null;
    try {
      sql = new Sql(dataSource);

      //Delete User to Group Relationships
      sql.executeUpdate("DELETE FROM db_user_groups ug WHERE ug.group_id in (SELECT g.id FROM db_groups g WHERE g.application_id = ${application.id})");

      //Delete Group to Role Relationships
      sql.executeUpdate("""
        DELETE FROM db_group_roles gr WHERE
         gr.group_id in (SELECT g.id FROM db_groups g WHERE g.application_id = ${application.id})
         OR
         gr.role_id in (SELECT r.id FROM db_roles r WHERE r.application_id = ${application.id})"""
      );

      //Delete Groups
      Group.executeUpdate('DELETE FROM Group g WHERE g.application.id = :applicationId', [applicationId: application.id])

     
      //Delete Application
      application?.delete();
    } finally {
      try {sql?.close();} catch (Exception ignore) {}
    }
  }

  void deleteGroup(Group group) {
    //Delete User to Group Relationships
    UserGroupJoin.executeUpdate('DELETE FROM UserGroupJoin ugj WHERE ugj.group.id = :groupId', [groupId: group.id])

    //Delete Group to Role Relationships
    GroupRoleJoin.executeUpdate('DELETE FROM GroupRoleJoin grj WHERE grj.group.id = :groupId', [groupId: group.id])

    //Delete Group
    group?.delete()
  }

  

  Application getApplicationByString(String applicationId) {
    return getApplicationByLong(applicationId?.toLong())
  }

  Application getApplicationByLong(Long applicationId) {
    if (applicationId == null) {
      throw new ImrException('Application ID cannot be null.');
    }

    return Application.get(applicationId)
  }

  Application getApplicationByName(Application application) {
    return getApplicationByStringName(application?.name)
  }

  Application getApplicationByStringName(String name) {
    if (name == null) {
      throw new ImrException('Application Name cannot be null.');
    }

    return Application.findByName(name?.trim()?.toUpperCase())
  }

  PagedResultList getApplications(Map<String, Serializable> pagination = [:]) {
    List list = []

    List count = Application.executeQuery(
            "SELECT count(x) FROM Application x")

    if (pagination['sort'] != null) {
      pagination['sort'] = "x.${pagination['sort']}"
    }

    if (count[0] > 0) {
      list = Application.executeQuery(
              "SELECT x FROM Application x ${prepSort(pagination)}",
              [], pagination)

      list = (list == null) ? new ArrayList<Application>() : list
    }

    return new PagedResultList(list, (int) count[0])
  }

  Group getGroupByApplicationAndName(Group group) {
    getGroupByApplicationAndName(group?.application?.id, group?.name);
  }

  Group getGroupByApplicationAndName(String applicationId, String name) {
    getGroupByApplicationAndName(applicationId?.toLong(), name);
  }

  /**
   * Used to check if an object really exists and belongs to the proper objects
   */
  Group getGroupByApplicationAndName(Long applicationId, String name) {
    List<String> validationError = [];

    if (applicationId == null) {
      validationError.add('Application ID cannot be null.');
    }
    if (!name) {
      validationError.add('Group name cannot be null or blank.');
    }

    //Now if validation had issues, throw an error.
    if (validationError) {
      throw new ImrException(validationError);
    }

    List join = Group.executeQuery("FROM Group WHERE application.id = :applicationId AND name = :name",
            ['applicationId': applicationId, 'name': name])

    return (join == null || join.isEmpty()) ? null : join[0]
  }

  Group getGroup(String groupId) {
    return getGroup(groupId?.toLong());
  }

  Group getGroup(Long groupId) {
    if (groupId == null) {
      throw new ImrException('Group ID cannot be null.');
    }

    return Group.get(groupId)
  }

  Group getGroup(String applicationId, String groupId) {
    return getGroup(applicationId?.toLong(), groupId?.toLong());
  }

  Group getGroup(Long applicationId, Long groupId) {
    List<String> validationError = [];

    if (applicationId == null) {
      validationError.add('Application ID cannot be null.');
    }
    if (groupId == null) {
      validationError.add('Group ID cannot be null.');
    }

    //Now if validation had issues, throw an error.
    if (validationError) {
      throw new ImrException(validationError);
    }

    Group returnMe = null;

    List list = Group.withCriteria {
      fetchMode('application', FetchMode.JOIN)
      //Gota do a little funkyness so this works on Oracle
      eq('id', groupId)
      eq("application.id", applicationId)
    };

    if (list) {
      returnMe = (Group) list[0];
    }

    return returnMe;
  }

  PagedResultList getGroups(String applicationId, Map<String, Serializable> pagination = [:]) {
    getGroups(applicationId?.toLong(), pagination)
  }

  PagedResultList getGroups(Long applicationId, Map<String, Serializable> pagination = [:]) {
    if (applicationId == null) {
      throw new ImrException("Application ID cannot be null.");
    }

    List list = []

    def count = Group.withCriteria {
      projections {
        countDistinct('application')
      }
      eq("application.id", applicationId)
    }

    if (count[0] > 0) {
      list = Group.withCriteria {
        fetchMode('application', FetchMode.JOIN)

        eq("application.id", applicationId)

        if (pagination['offset'] != null) {
          firstResult(pagination['offset'])
        }
        if (pagination['max'] != null) {
          maxResults(pagination['max'])
        }
        if (pagination['sort'] != null) {
          order("${pagination['sort']}", (pagination['order']) ?: 'asc')
        }
      };
    }

    return new PagedResultList(list, count[0])
  }

 
 

  Map<String, List<Map<String, Object>>> getMigrationData() {
    Map<String, List<Map<String, Object>>> map = [:];

    //First get all the applications.
    List applications = Application.executeQuery("SELECT x FROM Application x");
    List<Map<String, Object>> appList = [];
    for (Application temp: applications) {
      Map<String, Object> tempMap = [:];
      tempMap["id"] = temp.id;
      tempMap["name"] = temp.name;
      tempMap["description"] = temp.description;
      tempMap["source"] = temp.source;
      tempMap["icon"] = temp.icon;
      tempMap["iconLabel"] = temp.iconLabel;
      tempMap["applicationVersion"] = temp.applicationVersion;
      tempMap["multiInstance"] = temp.multiInstance;
      tempMap["openSeparate"] = temp.openSeparate;
      tempMap["defaultFlag"] = temp.defaultFlag;

      appList.add(tempMap);
    }
    map['applications'] = appList;

    List groups = Group.executeQuery("SELECT x FROM Group x");
    List<Map<String, Object>> groupList = [];
    for (Group temp: groups) {
      Map<String, Object> tempMap = [:];
      tempMap["id"] = temp.id;
      tempMap["name"] = temp.name;
      tempMap["description"] = temp.description;
      tempMap["applicationName"] = temp.application.name;

      groupList.add(tempMap);
    }
    map['groups'] = groupList;

  

  Application saveApplication(Application application) {
    return application?.save()
  }

  Group saveGroup(Group group) {
    return group?.save();
  }

 

  /**
   * Search for Applications.
   * @param searchCriteria
   * @param pagination cannot be optional or invokeMethod will not work properly since the first argument is a list.
   * @return
   */
  PagedResultList searchApplications(List<SearchCriteria> searchCriteria, Map<String, Serializable> pagination) {
    def results = helper.getPagedResultSet(Application.class, searchCriteria, pagination);

    return results;
  }

  /**
   * Search for Role Groups.
   * @param searchCriteria
   * @param pagination cannot be optional or invokeMethod will not work properly since the first argument is a list.
   * @return
   */
  PagedResultList searchGroups(List<SearchCriteria> searchCriteria, Map<String, Serializable> pagination) {
    def results = helper.getPagedResultSet(Group.class, searchCriteria, pagination);

    return results;
  }

  
  Application updateApplication(Application application) {
    return application?.save()
  }

  Group updateGroup(Group group) {
    return group?.save()
  }

 
  String prepSort(Map<String, Serializable> pagination = [:]) {
    String sort = ""
    if (pagination['sort'] != null) {
      sort = "order by ${pagination['sort']} ${(pagination['order']) ?: 'asc'}"
    }

    return sort
  }
}
