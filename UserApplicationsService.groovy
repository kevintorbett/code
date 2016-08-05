package com.project.db.services.userMaint

import com.project.db.domain.userMaint.ApplicationGroupView
import com.project.db.domain.userMaint.UserApplicationView
import com.project.db.services.SearchHelper
import groovy.sql.Sql
import oracle.jdbc.driver.OracleTypes

class UserApplicationsService {

    def sessionFactory;
    def dataSource;

    static transactional = true

    SearchHelper helper = new SearchHelper();

    /**
         * Retrieves a list of applications with user application tagged
         * @param scl search criteria
         * @param pagination pagination (field to sort, order, etc.)
         * @return a list of applications.
         */
        public List<Map<String, Object>> getUserApplicationList(String userId) {

            List<Map<String, Object>> list = [];


            def sql = null;
            // Make the Stored Procedure call to set the task.
            try {
                sql = new Sql(dataSource)
                sql.call '{call PKG_USER_MAINT.p_get_user_applications(?,?)}', [userId,
                        Sql.resultSet(OracleTypes.CURSOR)], { cursorResults ->
                    while (cursorResults.next()) {
                        UserApplicationView userApplicationView = new  UserApplicationView();
                        userApplicationView.taggedYN = cursorResults.getAt('TAGGED_YN');
                        userApplicationView.id = cursorResults.getAt('ID');
                        userApplicationView.applicationName = cursorResults.getAt('APPLICATION_NAME');
                        list.add(userApplicationView)
                    }
                }


            } catch (Exception e) {
                throw e
            } finally {
                try {sql?.close();} catch (Exception e) {
                    throw e
                }
            }
            return list;
        }
    /**
     * Retrieves a list of applications with user application tagged
     * @param scl search criteria
     * @param pagination pagination (field to sort, order, etc.)
     * @return a list of applications.
     */
    public List<Map<String, Object>> getApplicationGroupList(String userId,String applicationId) {
        List<Map<String, Object>> list = [];


        def sql = null;
        // Make the Stored Procedure call to set the task.
        try {
            sql = new Sql(dataSource)
            sql.call '{call PKG_USER_MAINT.p_get_user_groups(?,?,?)}', [userId,applicationId,
                    Sql.resultSet(OracleTypes.CURSOR)], { cursorResults ->
                while (cursorResults.next()) {
                    ApplicationGroupView applicationGroupView = new  ApplicationGroupView();
                    applicationGroupView.taggedYN = cursorResults.getAt('TAGGEDYN');
                    applicationGroupView.groupId = cursorResults.getAt('ID');
                    applicationGroupView.description = cursorResults.getAt('DESCRIPTION');
                    applicationGroupView.name = cursorResults.getAt('NAME');
                    applicationGroupView.applicationId = cursorResults.getAt('APPLICATION_ID');
                    applicationGroupView.userId = userId;
                    list.add(applicationGroupView)
                }
            }


        } catch (Exception e) {
            throw e
        } finally {
            try {sql?.close();} catch (Exception e) {
                throw e
            }
        }
        return list;
    }
    }

