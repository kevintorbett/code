class ApplicationUrlMappings {
  static mappings = {
    //Search for application(s).
    "/api/system/applications"(
            controller: "application",
            action: [POST: "save", GET: "show", PUT: "update", DELETE: "remove"]
    )
    //Show all the groups tied to application(s)
    "/api/system/applications/groups"(
            controller: "application",
            action: [GET: "showGroup", POST: "saveGroup", PUT: "updateGroup", DELETE: "removeGroup"]
    )
   
  }
}
