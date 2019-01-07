package edu.rosehulman.boutell.moviequotes

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.dialog_add_edit_quote.view.*

class MovieQuoteAdapter(var context: Context) : RecyclerView.Adapter<MovieQuoteViewHolder>() {
    private val movieQuotes = ArrayList<MovieQuote>()

    override fun onCreateViewHolder(parent: ViewGroup, index: Int): MovieQuoteViewHolder {
        Log.d(Constants.TAG, "Creating VH")
        val view = LayoutInflater.from(context).inflate(R.layout.row_view, parent, false)
        return MovieQuoteViewHolder(view, this)
    }

    override fun onBindViewHolder(
        viewHolder: MovieQuoteViewHolder,
        index: Int) {
        viewHolder.bind(movieQuotes[index])
    }

    override fun getItemCount() = movieQuotes.size

    fun showAddEditDialog(position: Int = -1) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Add a quote")
        val view = LayoutInflater.from(context).inflate(
            R.layout.dialog_add_edit_quote, null, false
        )
        builder.setView(view)
        builder.setIcon(android.R.drawable.ic_input_add)
        if (position >= 0) {
            view.dialog_edit_text_quote.setText(movieQuotes[position].quote)
            view.dialog_edit_text_movie.setText(movieQuotes[position].movie)
        }

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            val quote = view.dialog_edit_text_quote.text.toString()
            val movie = view.dialog_edit_text_movie.text.toString()
            if (position < 0) {
                add(MovieQuote(quote, movie))
            } else {
                edit(position, quote, movie)
            }

        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.show()
    }

    fun add(movieQuote: MovieQuote) {
        movieQuotes.add(0, movieQuote)
        Log.d(Constants.TAG, "MQ: $movieQuotes")
        notifyItemInserted(0)
        // notifyDataSetChanged()
        Log.d(Constants.TAG, "Done Adding ")
    }

    fun edit(position: Int, quote: String, movie: String) {
        movieQuotes[position].quote = quote
        movieQuotes[position].movie = movie
        notifyItemChanged(position)
    }

    fun selectMovieQuote(position: Int) {
        movieQuotes[position].showDark = !movieQuotes[position].showDark
        notifyItemChanged(position)
    }
}