package edu.rosehulman.boutell.moviequotes

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.dialog.view.*

class MovieQuotesAdapter : RecyclerView.Adapter<MovieQuotesAdapter.MovieQuoteViewHolder> {

    private val mContext: Context
    private val mMovieQuotes = ArrayList<MovieQuote>()
    private val mMovieQuotesRef: CollectionReference
    private lateinit var mMovieQuotesSnapshotListener: ListenerRegistration

    constructor(context: Context) {
        mContext = context
        mMovieQuotesRef = FirebaseFirestore.getInstance().collection(Constants.QUOTES_PATH)
    }

    fun addSnapshotListener() {
        mMovieQuotesSnapshotListener = mMovieQuotesRef.orderBy(MovieQuote.CREATED_KEY, Query.Direction.DESCENDING).addSnapshotListener {
            snapshot, error ->
            if (error != null) {
                Log.e(Constants.TAG, "Error in listener: ", error);
            }
            if (snapshot != null) {
                mMovieQuotes.clear();
                for (documentSnapshot in snapshot.documents) {
                    mMovieQuotes.add(MovieQuote(documentSnapshot))
                }
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
            remove(mMovieQuotes[adapterPosition])
            return true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieQuoteViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.movie_quote_row, parent, false)
        return MovieQuoteViewHolder(view)
    }

    override fun getItemCount() = mMovieQuotes.size

    override fun onBindViewHolder(holder: MovieQuoteViewHolder, position: Int) {
        holder.quoteTextView.text = mMovieQuotes[position].quote
        holder.movieTextView.text = mMovieQuotes[position].movie
    }

    fun add(movieQuote: MovieQuote) {
        mMovieQuotesRef.add(movieQuote)
    }

    fun remove(movieQuote: MovieQuote) {

        mMovieQuotesRef.document(movieQuote.id!!).delete()
    }

    fun edit(movieQuote: MovieQuote, quote: String, movie: String) {
        movieQuote.quote = quote
        movieQuote.movie = movie
        mMovieQuotesRef.document(movieQuote.id!!).set(movieQuote)
    }

    fun showAddEditDialog(movieQuote: MovieQuote? = null) {
        val builder = AlertDialog.Builder(mContext);
        builder.setTitle(if (movieQuote == null) "Add a quote" else "Edit the quote")
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog, null, false)
        builder.setView(view)

        view.quote_text_view_dialog.setText(movieQuote?.quote)
        view.movie_text_view_dialog.setText(movieQuote?.movie)

        builder.setPositiveButton(android.R.string.ok, { dialog, whichButton ->
            val quote = view.quote_text_view_dialog.text.toString()
            val movie = view.movie_text_view_dialog.text.toString()
            if (movieQuote == null) {
                add(MovieQuote(quote, movie))
            } else {
                edit(movieQuote, quote, movie)
            }
        })
        builder.show()
    }
}
