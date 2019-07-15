package com.gmail.andre00nogueira.githubreposearch

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity(), RepositoriesAdapter.RepositoriesAdapterOnClickHandler {
    override fun onClick(urlToOpen: String) {
        Toast.makeText(this, urlToOpen, Toast.LENGTH_SHORT).show()
    }

    private lateinit var mRepositoryName: EditText
    private lateinit var mSearch: Button
    private lateinit var mTotalCount: TextView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mRepositoriesAdapter: RepositoriesAdapter
    private lateinit var repoNames: ArrayList<String>
    private lateinit var repoCreatorNames: ArrayList<String>
    private lateinit var mSearchNewRepositories: Button
    private lateinit var githubOwnerProfilePic: Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recyclerViewRepo) // This is the recyclerView that will contain the data

        mSearchNewRepositories = findViewById(R.id.buttonSearchNewRepos)
        mRepositoryName = findViewById(R.id.editTextQuery)
        mSearch = findViewById(R.id.buttonSearch)
        mTotalCount = findViewById(R.id.textViewTotalCount)


        repoCreatorNames = ArrayList() // Init repoCreatorNames with an empty ArrayList
        repoNames = ArrayList()// Init repoNames with an empty ArrayList


        mSearchNewRepositories.visibility = View.GONE // When app is started, make button GONE

        // When search button is pressed...
        mSearch.setOnClickListener {
            githubSearchQuery() // Makes the new search query
        }



        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false) // LinearLayoutManager because the RecyclerView will have vertical LinearLayout
        mRecyclerView.layoutManager = layoutManager // Sets the layout manager os recycler view to the new LinearLayout we just created
        mRecyclerView.setHasFixedSize(true) // All elements will have the same size

        // When user clicks button to make a new repository search
        mSearchNewRepositories.setOnClickListener {
            mSearchNewRepositories.visibility = View.GONE // Makes this button GONE
            mRecyclerView.visibility = View.GONE // Makes the current recycler view GONE
            mRepositoryName.visibility = View.VISIBLE // Makes editText visible again
            mSearch.visibility = View.VISIBLE // Makes button to search visible again
        }

    }


    fun githubSearchQuery() {
        val githubSearchQuery: String = mRepositoryName.text.toString() // Gets the name of the repository
        val url: URL = NetworkUtils.buildURL(githubSearchQuery) // Builds the URL
        GithubSearchAsync().execute(url) // And then executes it in the AsyncTask
    }

    inner class GithubSearchAsync : AsyncTask<URL, Void, String>() {

        override fun doInBackground(vararg p0: URL?): String {
            val url: URL? = p0[0] // The url passed is in the 1st position
            var githubSearchResults: String? = null
            try {
                githubSearchResults = NetworkUtils.getResponseFromHTTPUrl(url!!) // Gets response from HTTP in JSON
            } catch (e: IOException) {
                e.stackTrace
            }
            return githubSearchResults!! // Returns null or the JSON response
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            returnResponseFromJSONObject(result) // After the execution, calls the function that will convert JSON to actual data
        }
    }


    fun returnResponseFromJSONObject(jsonString: String){
        val response = JSONObject(jsonString) // Gets the JSONObject that was received in form of String
        val githubTotalSearchResults = response.get("total_count") // Total count of repositories found
        mTotalCount.text = getString(R.string.total_counts) + githubTotalSearchResults.toString() // Put the total of repositories found in the TextView
        val githubArray: JSONArray = response.getJSONArray("items") //
        for (i in 0..(githubArray.length() - 1)) {
            // For each item 0... (number of elements in github array)
            val githubRepoItem: JSONObject = githubArray.getJSONObject(i)
                // We get the repository name
            val githubRepoFullName = githubRepoItem.get("full_name")

            // Owner of the repository
            val githubOwner: JSONObject = githubRepoItem.getJSONObject("owner")
                // Inside the owner, we get its name
            val githubOwnerName = githubOwner.get("login") // Gets the OwnerName
            githubOwnerProfilePic = githubOwner.get("avatar_url")
            repoNames.add(githubRepoFullName.toString()) // Adds element to ArrayList
            repoCreatorNames.add(githubOwnerName.toString()) // Adds element to ArrayList
        }
        
        mRepositoriesAdapter = RepositoriesAdapter(repoNames, repoCreatorNames, this) // Passes the values to the RecyclerView adapter
        mRecyclerView.adapter = mRepositoriesAdapter // Sets the adapter of the RecyclerView to the new adapter created above
        mRecyclerView.visibility = View.VISIBLE // Sets the visibility of the RecyclerView to visible
        mRepositoryName.visibility = View.GONE // Sets the visibility of the EditText to GONE
        mSearch.visibility = View.GONE // Sets the visibility of the Search button to GONE
        mSearchNewRepositories.visibility = View.VISIBLE // Sets the visibility of the new search button to visible
    }
}
