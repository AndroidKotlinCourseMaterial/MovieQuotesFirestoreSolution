package edu.rosehulman.boutell.moviequotes

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: MovieQuoteAdapter
    private lateinit var settingsRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        settingsRef = FirebaseFirestore
            .getInstance()
            .collection("settings").document("settings")

        fab.setOnClickListener {
            // For testing
            // adapter.add(MovieQuote("Quote", "Movie"))
            adapter.showAddEditDialog()
        }

        settingsRef.addSnapshotListener { document, exception ->
            if (exception != null) {
                Log.w(Constants.TAG, "listen error", exception)
                return@addSnapshotListener
            }
            toolbar.title = (document?.get("author") ?: "") as String
        }

        adapter = MovieQuoteAdapter(this)
        recycler_view.layoutManager =
            LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)
        recycler_view.adapter = adapter
        adapter.addQuoteSnapshotListener()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_increase_font_size -> {
                changeFontSize(4)
                true
            }
            R.id.action_decrease_font_size -> {
                changeFontSize(-4)
                true
            }
            R.id.action_settings -> {
                getWhichSettings()
                true
            }
            R.id.action_set_author -> {
                updateAppTitle()
                true
            }
            R.id.action_clear -> {
                confirmClear()
                true
            }
            R.id.action_show_favorite_movie -> {
                showFavoriteMovie()
                true
            }
            R.id.action_show_favorite_quote -> {
                showFavoriteMovieQuote()
                true
            }
            R.id.action_show_all_quotes -> {
                showAllMovieQuotes()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateAppTitle() {
        settingsRef.get()
            .addOnSuccessListener { document ->
                var author = (document["author"] ?: "") as String
                val builder = AlertDialog.Builder(this)
                builder.setTitle("App Author")
                val authorEditText = EditText(this)
                authorEditText.setText(author)
                authorEditText.hint = "App author's name"
                builder.setView(authorEditText)
                builder.setPositiveButton(android.R.string.ok) { _, _ ->
                    author = authorEditText.text.toString()
                    Log.d(Constants.TAG, "Author: $author")
                    val map = mapOf<String, Any>(Pair("author", author))
                    settingsRef.set(map)

                }
                builder.create().show()
            } .addOnFailureListener {exception ->
                Log.e(Constants.TAG, "Get error: $exception")
            }
    }

    private fun showFavoriteMovie() {
        val favoriteMovieQuoteRef = FirebaseFirestore
            .getInstance()
            .collection("favorites")
            .document("moviequote")

        favoriteMovieQuoteRef.get().addOnSuccessListener {snapshot: DocumentSnapshot ->
            val movie = (snapshot["movie"] ?: "") as String
            Toast.makeText(this, movie, Toast.LENGTH_LONG).show()
        }
    }

    private fun showFavoriteMovieQuote() {
        val favoriteMovieQuoteRef = FirebaseFirestore
            .getInstance()
            .collection("favorites")
            .document("moviequote")

        favoriteMovieQuoteRef.get().addOnSuccessListener { snapshot: DocumentSnapshot ->

            val mq = snapshot.toObject(MovieQuote::class.java)!!
            Log.d(Constants.TAG, mq.toString())
            adapter.add(mq)
        }
    }

    private fun showAllMovieQuotes() {
        val movieQuotesRef = FirebaseFirestore
            .getInstance()
            .collection("quotes")

        movieQuotesRef.get().addOnSuccessListener { snapshot: QuerySnapshot ->
            for (doc in snapshot) {
                val mq = doc.toObject(MovieQuote::class.java)
                Log.d(Constants.TAG, mq.toString())
                adapter.add(mq)
            }
        }
    }




    private fun changeFontSize(delta: Int) {
        // Increase the font size by delta sp
//        var currentSize = quote_text_view.textSize / resources.displayMetrics.scaledDensity
//        currentSize += delta
//        quote_text_view.textSize = currentSize
//        movie_text_view.textSize = currentSize
    }

    private fun confirmClear() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.confirm_delete_title))
        builder.setMessage(getString(R.string.confirm_delete_message))
        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            // updateQuote(defaultMovieQuote)
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()
    }

    private fun getWhichSettings() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_which_settings_title))
        // For others, see https://developer.android.com/reference/android/provider/Settings
        builder.setItems(R.array.settings_types) { _, index ->
            var actionConstant = when (index) {
                0 -> Settings.ACTION_SOUND_SETTINGS
                1 -> Settings.ACTION_SEARCH_SETTINGS
                else -> Settings.ACTION_SETTINGS
            }
            startActivity(Intent(actionConstant))
        }
        builder.create().show()
    }

}
