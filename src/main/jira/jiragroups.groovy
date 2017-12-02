package main.jira

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom

// Try HTTPS
//def sc = SSLContext.getInstance("SSL")
//def trustAll = [
//        getAcceptedIssuers: {},
//        checkClientTrusted: {a, b -> },
//        checkServerTruster: {a, b -> },
//]
//
//sc.init(null, [trustAll as X509TrustManager] as TrustManager[], new SecureRandom())
//HttpsURLConnection.defaultSSLSocketFactory = sc.socketFactory

List groupNames =['jira-software-users','jira-administrators', 'users_1' ]

groupNames.each { groupName ->
    nextPage = "true"
    startAt= 0
    maxResults = 3

    println("********** ${groupName} *************")
    while (nextPage) {
        def connection = new URL("http://192.168.33.10:8080/rest/api/2/group/member" +
                "?groupname=${groupName}" +
                "&includeInactiveUsers=false" +
                "&startAt=${startAt}" +
                "&maxResults=${maxResults}").openConnection() as HttpURLConnection

        // set some headers
        connection.setRequestProperty('User-Agent', 'groovy-2.4')
        connection.setRequestProperty('Accept', 'application/json')
        connection.setRequestProperty('authorization', 'Basic cm5zbHVjYXM6cm5zbHVjYXM=')

        //println connection.responseCode + ": " + JsonOutput.prettyPrint(connection.inputStream.text)


        if (connection.responseCode == 200) {
            // Get the JSON response
            def json = connection.inputStream.withCloseable { inStream ->
                new JsonSlurper().parse(inStream as InputStream)
            }

            nextPage = json?.nextPage
            //println nextPage

            users = json.values.each { v ->
                println("${groupName} ${v.name}")
            }
            startAt += maxResults
        } else {
            nextPage = null
        }

    }
}