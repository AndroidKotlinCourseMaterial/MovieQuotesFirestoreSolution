package edu.rosehulman.boutell.moviequotes

import com.google.firebase.firestore.Exclude

data class MovieQuote(var quote: String, var movie: String) {

    @Exclude
    private var id: String? = null

//    constructor(documentSnapshot: DocumentSnapshot): this() {
//
//        id = documentSnapshot.id
//    }



}