package edu.rosehulman.boutell.moviequotes

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class MovieQuote {

    companion object {
        val QUOTE_KEY = "quote"
        val MOVIE_KEY = "movie"
        val CREATED_KEY = "created"
    }

    var quote = ""
    var movie = ""

    @ServerTimestamp
    var created: Date? = null

    @set:Exclude @get:Exclude var id = ""

    constructor() {
    }

    constructor(quote: String, movie: String) {
        this.quote = quote
        this.movie = movie
    }

    constructor(documentSnapshot: DocumentSnapshot) {
        quote = documentSnapshot.getString(QUOTE_KEY)
        movie = documentSnapshot.getString(MOVIE_KEY)
        id = documentSnapshot.id
    }
}

