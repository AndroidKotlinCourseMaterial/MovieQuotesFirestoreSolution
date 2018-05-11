package edu.rosehulman.boutell.moviequotes

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

class MovieQuote {
    @ServerTimestamp var lastTouched: Date? = null
    @get:Exclude var id = ""
    var quote = ""
    var movie = ""

    constructor() {
    }

    constructor(quote: String, movie: String) {
        this.quote = quote
        this.movie = movie
    }

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(documentSnapshot: DocumentSnapshot): MovieQuote {
            val movieQuote = documentSnapshot.toObject(MovieQuote::class.java)
            movieQuote.id = documentSnapshot.id
            return movieQuote
        }
    }
}

