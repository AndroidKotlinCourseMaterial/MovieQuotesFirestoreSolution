package edu.rosehulman.boutell.moviequotes

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class MovieQuote(var quote: String, var movie: String) {
    @ServerTimestamp var lastTouched: Date? = null
    @get:Exclude var id = ""

    constructor(): this("", "")

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(documentSnapshot: DocumentSnapshot): MovieQuote {
            val movieQuote = documentSnapshot.toObject(MovieQuote::class.java)
            movieQuote.id = documentSnapshot.id
            return movieQuote
        }
    }
}

