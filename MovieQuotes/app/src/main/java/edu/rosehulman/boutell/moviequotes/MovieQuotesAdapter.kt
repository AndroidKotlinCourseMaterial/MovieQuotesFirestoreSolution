package edu.rosehulman.boutell.moviequotes

import android.app.AlertDialog
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog.view.*

class MovieQuotesAdapter : RecyclerView.Adapter<MovieQuotesAdapter.MovieQuoteViewHolder> {

    private val mContext: Context
    private val mMovieQuotes = ArrayList<MovieQuote>()

    constructor(context: Context) {
        mContext = context;
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
        mMovieQuotes.add(0, movieQuote)
        notifyItemInserted(0)
    }

    fun remove(movieQuote: MovieQuote) {
        mMovieQuotes.remove(movieQuote)
        notifyDataSetChanged()
    }

    fun edit(movieQuote: MovieQuote, quote: String, movie: String) {
        movieQuote.quote = quote
        movieQuote.movie = movie
        notifyItemChanged(mMovieQuotes.indexOf(movieQuote))
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
