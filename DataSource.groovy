import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsAnnotationConfiguration

dataSource {
    configClass = GrailsAnnotationConfiguration.class
    loggingSql = false
    //Pool set to false since we will expect the container to handle the connection pool.
    pooled = false
}
hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    environments {
        development {
            cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
            //cache.provider_class = 'com.opensymphony.oscache.hibernate.OSCacheProvider'
        }
        test {
            cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
            //cache.provider_class = 'com.opensymphony.oscache.hibernate.OSCacheProvider'
        }
        production {
            cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
        }
    }
}
// environment specific settings
environments {
    development {
        dataSource {
            //dbCreate = "create-drop" // one of 'create', 'create-drop','update'
            //create: Create the database if it doesn't exist, but don't modify it if it does. Deletes existing data.
            //create-drop: Drop and re-create the database when Grails is run
            //update: Create the database if it doesn't exist, and modify it if it does exist
            //If not set, database is not touched
            if (System.getProperty('os.name', '').contains('Win')) {

/*        dbCreate = "update"
        driverClassName = "org.h2.Driver"
        url="jdbc:h2:tcp://localhost/./data/batchdb;MVCC=TRUE"
        username = "sa"
        password = ""
 /*/
                driverClassName = "oracle.jdbc.OracleDriver"
                url = "jdbc:oracle:thin:@ora1:1521:xxx1"
//        username = "xxx"
//        password = "xxx"
                username = "xxx"
                password = "xxx"
                properties = {
                    validationQuery="SELECT 1 from dual"
                    maxActive = 1
                    maxIdle = 0
                    minIdle = 0
                    initialSize = 1
                    maxWait = 30000
                    testOnBorrow=true
                    testOnReturn=false
                    testWhileIdle=true
                    timeBetweenEvictionRunsMillis=1000 * 60 * 30
                    numTestsPerEvictionRun=3
                    minEvictableIdleTimeMillis=1000 * 60 * 30
                }
                dialect='org.hibernate.dialect.Oracle10gDialect'
                /**/
            } else {
                //For Tomcat
                jndiName = "java:comp/env/jdbc/IMDBMiddleware"
                //For JBoss
                //jndiName = "java:IMDBMiddleware"
                //For WebLogic
                //jndiName = "IMDBMiddleware"
                dialect='org.hibernate.dialect.Oracle10gDialect'
            }
        }
    }
    test {
        dataSource {
            dbCreate = "create-drop"
/**/
            //hsql can handle sequences like Oracle but MySQL cannot
            //driverClassName = "org.hsqldb.jdbcDriver"
            //url = "jdbc:hsqldb:mem:devDB"
            driverClassName = "org.h2.Driver"
            url="jdbc:h2:mem"
            username = "sa"
            password = ""
        }
    }
    production {
        dataSource {
            //For Tomcat
            jndiName = "java:comp/env/jdbc/IMDBMiddleware"
            dialect='org.hibernate.dialect.Oracle10gDialect'
            //For JBoss
            //jndiName = "java:IMDBMiddleware"
            //For WebLogic
            //jndiName = "IMDBMiddleware"
        }
    }
}