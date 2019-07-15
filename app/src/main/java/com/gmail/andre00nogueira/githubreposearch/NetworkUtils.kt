package com.gmail.andre00nogueira.githubreposearch

import android.net.Uri
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.util.*

class NetworkUtils {

    companion object{
        val GITHUB_BASE_URL: String = "https://api.github.com/search/repositories"
        val REPO_NAME_PARAM: String = "q"

        fun buildURL(repoName: String, languageUsed: String): URL{
            var LANGUAGE_WRITTEN_IN_PARAM = "" // Init the language param empty

            // If language used field is not empty, add the query to the Uri
            if (languageUsed != ""){
                LANGUAGE_WRITTEN_IN_PARAM = " language:"
            }
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


        fun getResponseFromHTTPUrl(url: URL): String? {
            val connection: URLConnection? = url.openConnection()
            try{
                val input: InputStream = connection!!.getInputStream()
                val scanner = Scanner(input)
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