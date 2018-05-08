package edu.rosehulman.boutell.moviequotes

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.dialog.view.*
import java.util.*
import kotlin.collections.ArrayList

class MovieQuotesAdapter : RecyclerView.Adapter<MovieQuotesAdapter.MovieQuoteViewHolder> {

    private val mContext: Context
    private var mMovieQuotes: List<DocumentSnapshot>
    private val mMovieQuotesRef: CollectionReference
    private lateinit var mMovieQuotesSnapshotListener: ListenerRegistration

    constructor(context: Context) {
        mContext = context
        mMovieQuotesRef = FirebaseFirestore.getInstance().collection(Constants.QUOTES_PATH)
        mMovieQuotes = ArrayList()
    }

    fun addSnapshotListener() {
        mMovieQuotesSnapshotListener = mMovieQuotesRef.orderBy(Constants.CREATED_KEY, Query.Direction.DESCENDING).addSnapshotListener {
            snapshot, error ->
            if (error != null) {
                Log.e(Constants.TAG, "Error in listener: ", error)
            }
            if (snapshot != null) {
                mMovieQuotes = snapshot.documents
                notifyDataSetChanged()
            }
        }
    }

    fun removeSnapshotListener() {
        mMovieQuotesSnapshotListener.remove()
    }


    inner class MovieQuoteViewHolder : RecyclerView.ViewHolder, View.OnClickListener, View.OnLongClickListener {
        val movieTextView: TextView
        val quoteTextView: TextView

        constructor(itemView: View) : super(itemView) {
            movieTextView = itemView.findViewById(R.id.movie_text_view)
            quoteTextView = itemView.findViewById(R.id.quote_text_view)
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(v: View?) {
            showAddEditDialog(mMovieQuotes[adapterPosition])
        }

        override fun onLongClick(v: View?): Boolean {
            remove(mMovieQuotes[adapterPosition].id)
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieQuoteViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.movie_quote_row, parent, false)
        return MovieQuoteViewHolder(view)
    }

    override fun getItemCount() = mMovieQuotes.size

    override fun onBindViewHolder(holder: MovieQuoteViewHolder, position: Int) {
        holder.quoteTextView.text = mMovieQuotes[position].getString(Constants.QUOTE_KEY)
        holder.movieTextView.text = mMovieQuotes[position].getString(Constants.MOVIE_KEY)
    }

    private fun add(quote: String, movie: String) {
        mMovieQuotesRef.add(mapOf(Constants.QUOTE_KEY to quote, Constants.MOVIE_KEY to movie, Constants.CREATED_KEY to Date()))
    }

    private fun remove(id: String) {
        mMovieQuotesRef.document(id).delete()
    }

    private fun edit(id: String, quote: String, movie: String) {
        mMovieQuotesRef.document(id).update(mapOf(Constants.QUOTE_KEY to quote, Constants.MOVIE_KEY to movie))
    }

    fun showAddEditDialog(movieQuote: DocumentSnapshot? = null) {
        val builder = AlertDialog.Builder(mContext);
        builder.setTitle(if (movieQuote == null) "Add a quote" else "Edit the quote")
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog, null, false)
        builder.setView(view)

        view.quote_text_view_dialog.setText(movieQuote?.getString(Constants.QUOTE_KEY))
        view.movie_text_view_dialog.setText(movieQuote?.getString(Constants.MOVIE_KEY))

        builder.setPositiveButton(android.R.string.ok, { dialog, whichButton ->
            val quote = view.quote_text_view_dialog.text.toString()
            val movie = view.movie_text_view_dialog.text.toString()
            if (movieQuote == null) {
                add(quote, movie)
            } else {
                edit(movieQuote.id, quote, movie)

            }
        })
        builder.show()
    }
}
