package com.gmail.andre00nogueira.githubreposearch

import android.content.Context
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RepositoriesAdapter(val repoNames:ArrayList<String>, val repoCreatorNames: ArrayList<String>): RecyclerView.Adapter<RepositoriesAdapter.RepositoriesAdapterViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepositoriesAdapterViewHolder {
        val context: Context = parent.context
        val repositoriesAdapterViewHolder = RepositoriesAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.repo_list_item, parent, false))
        return repositoriesAdapterViewHolder
    }

    override fun getItemCount(): Int {
        return repoNames.size // Number os repos in the list
    }

    override fun onBindViewHolder(holder: RepositoriesAdapterViewHolder, position: Int) {
        holder.mTextViewRepoCreatorName.text = repoCreatorNames[position]
        holder.mTextViewRepoName.text = repoNames[position]

    }

    inner class RepositoriesAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mTextViewRepoCreatorName : TextView = itemView.findViewById(R.id.textViewRepoCreatorName)
        val mTextViewRepoName: TextView = itemView.findViewById(R.id.textViewRepoName)
    }





}