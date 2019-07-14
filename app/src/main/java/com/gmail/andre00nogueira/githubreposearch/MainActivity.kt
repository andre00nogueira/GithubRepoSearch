package com.gmail.andre00nogueira.githubreposearch

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {

    protected lateinit var editTextQuery: EditText
    protected lateinit var buttonSearch: Button
    protected lateinit var textViewTotalCount: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRepositoriesAdapter: RepositoriesAdapter
    private lateinit var repoNames: ArrayList<String>
    private lateinit var repoCreatorNames: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRecyclerView = findViewById(R.id.recyclerViewRepo) // This is the recyclerView that will contain the data

        editTextQuery = findViewById(R.id.editTextQuery)
        buttonSearch = findViewById(R.id.buttonSearch)
        textViewTotalCount = findViewById(R.id.textViewTotalCount)
        buttonSearch.setOnClickListener {
            githubSearchQuery()
        }

        repoCreatorNames = ArrayList()
        repoNames = ArrayList()

        val layoutManager: LinearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.setHasFixedSize(true)

    }


    fun githubSearchQuery() {
        val githubSearchQuery: String = editTextQuery.text.toString()
        val url: URL = NetworkUtils.buildURL(githubSearchQuery)
        GithubSearchAsync().execute(url)
    }

    inner class GithubSearchAsync : AsyncTask<URL, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            //textViewResponse.text = "Your response will appear here"
        }

        override fun doInBackground(vararg p0: URL?): String {
            val url: URL? = p0[0]
            var githubSearchResults: String? = null
            try {
                githubSearchResults = NetworkUtils.getResponseFromHTTPUrl(url!!)
            } catch (e: IOException) {
                e.stackTrace
            }
            return githubSearchResults!!
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            returnResponseFromJSONObject(result)
        }
    }


    fun returnResponseFromJSONObject(jsonString: String){
        val response = JSONObject(jsonString)
        val githubTotalSearchResults = response.get("total_count")
        textViewTotalCount.text = getString(R.string.total_counts) + githubTotalSearchResults.toString()
        val githubArray: JSONArray = response.getJSONArray("items")

        for (i in 0..(githubArray.length() - 1)) {
            val githubRepoItem: JSONObject = githubArray.getJSONObject(i)
            val githubRepoId = githubRepoItem.get("id")
            val githubRepoFullName = githubRepoItem.get("full_name")
            val githubOwner: JSONObject = githubRepoItem.getJSONObject("owner")
            val githubOwnerName = githubOwner.get("login")
            repoNames.add(githubRepoFullName.toString())
            repoCreatorNames.add(githubOwnerName.toString())
        }
        
        mRepositoriesAdapter = RepositoriesAdapter(repoNames, repoCreatorNames)
        mRecyclerView.adapter = mRepositoriesAdapter
        editTextQuery.visibility = View.GONE
        buttonSearch.visibility = View.GONE
    }
}
