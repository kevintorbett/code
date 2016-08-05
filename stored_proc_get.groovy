 /**
     * Retrieve contetn via stored procedure instead of hibernate
     * @param searchCriteria, int max
     */
    public List<Map<String, Object>> showAcesProc(String searchCriteria, int max) {
        List<Map<String, Object>> list = [];
         int total = 0;
        def sql = null;
    //    int max = 200;
   //     def builder = new groovy.json.JsonBuilder()
        // Make the Stored Procedure call to set the task.
        try {
            sql = new Sql(dataSource)
          sql.call '{call PKG_CONTENT_VIEW.p_get_list(?,?)}',
          //  sql.call '{call P_VW_ACES_CONTENT(?,?,?)}',
                    [searchCriteria, Sql.resultSet(OracleTypes.CURSOR)],
                    { cursorResults ->
                        // Check if there are rows
                        for(int i = max; (cursorResults.next() && i > 0); i--) {
                            total=total+1;
                            Map<String, Object> map = [:];
                            map['id'] = cursorResults.getAt('content_id');
                            map['supplierId'] = cursorResults.getAt('supplier_id');
                            map['supplierName'] = cursorResults.getAt('supplier_txt');
                            map['description'] = cursorResults.getAt('content_desc_txt');
                            map['documentNum'] = cursorResults.getAt('doc_num');
                           

                            list.add(map);
                        }
             //   def  jsonout = '{"items":'.join list + ',"totalCount":'  + total + '}';
                //         def jsonout1 = json { items  list  totalCount total  }
                   //     jsonout1 = [items: list, totalCount: total]
                    }
        } catch (Exception e) {
            throw e
        } finally {
            if (sql != null) {
                try {sql.close();} catch (Exception ignore) {}
            }

        }
        return  new PagedResultList(list, total);
    }