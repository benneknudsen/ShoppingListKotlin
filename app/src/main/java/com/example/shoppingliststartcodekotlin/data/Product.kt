package com.example.shoppingliststartcodekotlin.data

import com.google.firebase.firestore.Exclude

data class Product(var name:String = "", var price: String ="kr", var quantity: Int=0, @get:Exclude var id: String = "" ) {

    override fun toString(): String {
        return "Produkt: $name - $quantity stk- $price kr -\n"
    }
}
