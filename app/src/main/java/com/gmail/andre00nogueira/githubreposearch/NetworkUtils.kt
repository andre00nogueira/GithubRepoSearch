package com.gmail.andre00nogueira.githubreposearch

import android.net.Uri
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.*


// In this class we just need 2 functions
    // One to build de URL (which we will retrieve data from)
    // Another to get the response from HTTP Request (JSON)
class NetworkUtils {

    companion object{
        val GITHUB_BASE_URL: String = "https://api.github.com/search/repositories"
        val REPO_NAME_PARAM: String = "q"


        // In this function we build the URL from a Uri
        fun buildURL(repoName: String, languageUsed: String): URL{
            var LANGUAGE_WRITTEN_IN_PARAM = "" // Init the language param empty

            // If language used field is not empty, add the query to the Uri
            if (languageUsed != ""){
                LANGUAGE_WRITTEN_IN_PARAM = " language:"
            }
            // Building the Uri, which has the base url, and then we append query (the name of the repo plus the language)
            val builtUri: Uri = Uri.parse(GITHUB_BASE_URL).buildUpon()
                .appendQueryParameter(REPO_NAME_PARAM, repoName + LANGUAGE_WRITTEN_IN_PARAM + languageUsed)
                .build()

            var url: URL ?= null
            try {
                url = URL(builtUri.toString())
                println(url)
            }catch (e: MalformedURLException){
                e.stackTrace
            }
            return url!!
        }

        // Here we retrieve the data and get the response which comes in JSON format
        fun getResponseFromHTTPUrl(url: URL): String? {
            // First, we open the connection
            val connection: URLConnection? = url.openConnection()
            try{
                // Reads the input stream
                val input: InputStream = connection!!.getInputStream()
                val scanner = Scanner(input)
                // Delimiter \\A which stands for the START OF A STRING!
                scanner.useDelimiter("\\A")
                val hasInput: Boolean = scanner.hasNext()
                if (hasInput){
                    return scanner.next()
                }else{
                    return null
                }
            }finally {

            }
        }
    }




}