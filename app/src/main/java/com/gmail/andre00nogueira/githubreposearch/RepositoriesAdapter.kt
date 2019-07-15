package com.gmail.andre00nogueira.githubreposearch

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.net.URL

class RepositoriesAdapter(val repoNames:ArrayList<String>, val repoCreatorNames: ArrayList<String>, clickHandler: RepositoriesAdapterOnClickHandler): RecyclerView.Adapter<RepositoriesAdapter.RepositoriesAdapterViewHolder>() {

    private var mClickHandler: RepositoriesAdapterOnClickHandler = clickHandler




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesAdapterViewHolder {
        val context: Context = parent.context // Gets the context of MainActivity
        val repositoriesAdapterViewHolder = RepositoriesAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.repo_list_item, parent, false)) // Creates a new view holder
        return repositoriesAdapterViewHolder
    }

    override fun getItemCount(): Int {
        return repoNames.size // Number os repos in the list
    }

    override fun onBindViewHolder(holder: RepositoriesAdapterViewHolder, position: Int) {
        holder.mTextViewRepoCreatorName.text = repoCreatorNames[position] // Sets the text view w/ the creator name
        holder.mTextViewRepoName.text = repoNames[position] // Sets the text view w/ the repo name

    }

    interface RepositoriesAdapterOnClickHandler{
        fun onClick(urlToOpen: String)

    }

    inner class RepositoriesAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{


        override fun onClick(p0: View?) {
            val adapterPosition = adapterPosition
            mClickHandler.onClick("https://github.com/"+repoNames[adapterPosition])
        }
        val sSetOnItemClickListener = itemView.setOnClickListener(this)
        val mTextViewRepoCreatorName : TextView = itemView.findViewById(R.id.textViewRepoCreatorName)
        val mTextViewRepoName: TextView = itemView.findViewById(R.id.textViewRepoName)
        val mImageViewProfilePic: ImageView = itemView.findViewById(R.id.imageViewProfilePic)
    }





}