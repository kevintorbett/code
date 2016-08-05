class UserMaintUrlMappings {
  static mappings = {
    "/api/im/userMaint/copy"(
            controller: "copyUser",
            action: [PUT: "update"]
    )
      //Show all the users tied to a group
      "/api/im/userMaint/groups/users"(
             controller: "groupUsers",
              action:[GET:"show"]
        )
      //Show all the application tied to a users
      "/api/im/userMaint/users/applications"(
              controller: "userApplications",
              action:[GET:"showApps"]
      )
      //Show all the groups tied to a application and user
      "/api/im/userMaint/users/groups"(
              controller: "userApplications",
              action:[GET:"showGroups"]
      )
  }

}