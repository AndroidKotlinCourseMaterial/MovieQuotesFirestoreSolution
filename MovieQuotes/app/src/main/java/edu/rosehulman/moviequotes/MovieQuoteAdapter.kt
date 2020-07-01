package edu.rosehulman.moviequotes

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog_add.view.*

class MovieQuoteAdapter(val context: Context) : RecyclerView.Adapter<MovieQuoteViewHolder>() {
    private val movieQuotes = ArrayList<MovieQuote>()
    private val quotesRef = FirebaseFirestore
        .getInstance()
        .collection(Constants.QUOTES_COLLECTION)
    private lateinit var listenerRegistration: ListenerRegistration

    init {
        addBetterQuotesListener()
    }

    private fun addBetterQuotesListener() {
        quotesRef
            .orderBy(MovieQuote.CREATED_KEY, Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.e(Constants.TAG, "Error: $exception")
                    return@addSnapshotListener
                }
                for (docChange in snapshot!!.documentChanges) {
                    val mq = MovieQuote.from(docChange.document)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            movieQuotes.add(0, mq)
                            notifyItemInserted(0)
                        }
                        DocumentChange.Type.REMOVED -> {
                            val pos = movieQuotes.indexOfFirst { it.id == mq.id }
                            movieQuotes.removeAt(pos)
                            notifyItemRemoved(pos)
                        }
                        DocumentChange.Type.MODIFIED -> {
                            val pos = movieQuotes.indexOfFirst { it.id == mq.id }
                            movieQuotes[pos] = mq
                            notifyItemChanged(pos)
                        }
                    }
                }
            }
    }

    fun removeSnapshotListener() {
        Log.d(Constants.TAG, "Removing listener")
        listenerRegistration.remove()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieQuoteViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_view, null, false)
        return MovieQuoteViewHolder(view, this, context)
    }

    override fun getItemCount() = movieQuotes.size

    override fun onBindViewHolder(holder: MovieQuoteViewHolder, position: Int) {
        val movieQuote = movieQuotes[position]
        holder.bind(movieQuote)
    }

    fun showAddEditDialog(position: Int) {
        // pos of -1 means add
        val builder = AlertDialog.Builder(context)
        builder.setTitle(if (position < 0) R.string.add_dialog_title else R.string.edit_dialog_title)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_add, null, false)
        builder.setView(view)
        if (position >= 0) {
            view.quote_edit_text.setText(movieQuotes[position].quote)
            view.movie_edit_text.setText(movieQuotes[position].movie)
        }
        builder.setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
            val quote = view.quote_edit_text.text.toString()
            val movie = view.movie_edit_text.text.toString()
            val movieQuote = MovieQuote(quote, movie)
            if (position < 0) {
                add(movieQuote)
            } else {
                edit(quote, movie, position)
            }
        }
        if (position >= 0) {
            builder.setNeutralButton("Delete") { _, _ ->
                delete(position)

            }
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }

    fun add(movieQuote: MovieQuote) {
        quotesRef.add(movieQuote)
    }

    private fun edit(quote: String, movie: String, position: Int) {
        movieQuotes[position].quote = quote
        movieQuotes[position].movie = movie
        quotesRef.document(movieQuotes[position].id).set(movieQuotes[position])
    }

    private fun delete(position: Int) {
        quotesRef.document(movieQuotes[position].id).delete()
    }

    fun select(position: Int) {
        movieQuotes[position].isSelected = !movieQuotes[position].isSelected
        quotesRef.document(movieQuotes[position].id).set(movieQuotes[position])
    }
}