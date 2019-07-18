package com.gmail.andre00nogueira.githubreposearch

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URI
import java.net.URL

class MainActivity : AppCompatActivity(), RepositoriesAdapter.RepositoriesAdapterOnClickHandler, SharedPreferences.OnSharedPreferenceChangeListener {


    // THIS FUNCTION WILL BE USED FOR CHANGING UI BASED ON SHARED PREFERENCES !!!
    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        if  (p1.equals("open_url_when_clicked")){
            // code to execute about shared preferences
        }
    }

    override fun onClick(urlToOpen: String) {
        if(!isOpenURLAllowed()){
            return
        }
       val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlToOpen)) // Open the URL
        // Checks if there are any apps installed that can open the URL
       if (intent.resolveActivity(packageManager)!=null){
            startActivity(intent)
        }
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
    private lateinit var mLanguageUsed: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRecyclerView = findViewById(R.id.recyclerViewRepo) // This is the recyclerView that will contain the data

        mSearchNewRepositories = findViewById(R.id.buttonSearchNewRepos)
        mRepositoryName = findViewById(R.id.editTextQuery)
        mSearch = findViewById(R.id.buttonSearch)
        mTotalCount = findViewById(R.id.textViewTotalCount)
        mLanguageUsed = findViewById(R.id.editTextLanguage)

        repoCreatorNames = ArrayList() // Init repoCreatorNames with an empty ArrayList
        repoNames = ArrayList()// Init repoNames with an empty ArrayList


        mSearchNewRepositories.visibility = View.GONE // When app is started, make button GONE
        mTotalCount.visibility = View.GONE
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
            mRepositoryName.setText("") // Sets empty text
            mSearch.visibility = View.VISIBLE // Makes button to search visible again
            mTotalCount.visibility = View.GONE
            mLanguageUsed.visibility = View.VISIBLE // Makes editText visible again
            mLanguageUsed.setText("") // Sets empty text
            repoNames.clear() // Clear previous data
            repoCreatorNames.clear() // Clear previous data
        }

    }

    fun isOpenURLAllowed() : Boolean{
        val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        return sharedPreferences.getBoolean("open_url_when_clicked", true)
    }

    // Here all starts
        // In this function we start the HTTP request
        // We get the repo name and the language used
        // And we pass it to the buildURL function in NetworkUtils class
            // which returns a URL, and then we pass that url to the AsyncTask
            // This AsyncTask will get the actual data from the internet
    fun githubSearchQuery() {
        val githubSearchQuery: String = mRepositoryName.text.toString() // Gets the name of the repository
        val githubLanguageUsed: String = mLanguageUsed.text.toString() // Gets the language used in the repository
        val url: URL = NetworkUtils.buildURL(githubSearchQuery, githubLanguageUsed) // Builds the URL
        GithubSearchAsync().execute(url) // And then executes it in the AsyncTask
    }


    // This is the AsyncTask
        // Here we make the internet connection, since we can't make it in the main UI
        // Async classes have 4 methods that we can override
            // onPreExecute
                // Here we make changes that we want to happen BEFORE the AsyncTask runs
            // doInBackground
                // Here are the things that the AsyncTask is doing in the background (retrieving the data from HTTP request)
            // onProgressUpdate
                // Here we can update the main UI with the changes that are currently being made in doInBackground
            // onPostExecute
                // Here we can interact with the main UI, AFTER the AsyncTask ended
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
        mTotalCount.visibility = View.VISIBLE
        mLanguageUsed.visibility = View.GONE // Sets the visibility of the EditText to GONE
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.itemRefresh){
            githubSearchQuery()
            Toast.makeText(this, "Refreshed results!", Toast.LENGTH_SHORT).show()
        }
        // When the item menu pressed is the "settings"...
        if (item.itemId == R.id.itemSettings){
            // Opens the settings activity which will display the fragment w/ the preferences
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
