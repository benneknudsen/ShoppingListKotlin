package com.example.shoppingliststartcodekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppingliststartcodekotlin.adapters.ProductAdapter
import com.example.shoppingliststartcodekotlin.data.Product
import com.example.shoppingliststartcodekotlin.data.Repository
import com.example.shoppingliststartcodekotlin.data.Repository.addProduct
import com.firebase.ui.auth.AuthUI
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val AUTH_REQUEST_CODE = 7192
    lateinit var firebaseAuth:FirebaseAuth
    lateinit var listener:FirebaseAuth.AuthStateListener
    lateinit var providers:List<AuthUI.IdpConfig>

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        if(listener !=null)
            firebaseAuth.removeAuthStateListener(listener)
        super.onStop()
    }


    lateinit var adapter: ProductAdapter




    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }




    private fun addNewProduct(){
        val newProduct = Product(
            name = editTextTitle.text.toString(),
            quantity = editTextQuantity.text.toString().toInt(),
            price = editTextPrice.text.toString(),
        )
        addProduct(newProduct)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(applicationContext)
        setContentView(R.layout.activity_main)
        init()
        button_add.setOnClickListener{addNewProduct()}
        Repository.getData().observe(this, Observer {
            Log.d("Products","Found ${it.size} products")
            updateUI()
        })

        sortNameButton.setOnClickListener {
            Repository.products.sortBy { it.name }
            adapter.notifyDataSetChanged()
        }

        sortQuantityButton.setOnClickListener {
            Repository.products.sortByDescending { it.quantity }
            adapter.notifyDataSetChanged()
        }

        sortPriceButton.setOnClickListener {
            Repository.products.sortBy { it.price }
            adapter.notifyDataSetChanged()
        }


        //read the values at app startup so we can show this in the UI
        val name = PreferenceHandler.getName(this)
        val notifications = PreferenceHandler.useNotifications(this)
        updateUISettings(name, notifications)



    }





    private fun init() {
        providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.AnonymousBuilder().build()

        )
        firebaseAuth = FirebaseAuth.getInstance()
        listener = object:FirebaseAuth.AuthStateListener{
            override fun onAuthStateChanged(p0: FirebaseAuth) {
                val user = p0.currentUser
                if(user != null)
                {
                    Toast.makeText(this@MainActivity, ""+user.uid,Toast.LENGTH_SHORT).show()
                }
                else
                {
                    startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.mipmap.ic_launcher_round)
                        .build(),AUTH_REQUEST_CODE)
                }
            }


        }
        }



fun updateUISettings(name: String, notifications:Boolean){
    myName.text = name
    if (notifications)
        useNotifications.text = getString(R.string.on)
    else
        useNotifications.text = getString(R.string.off)
}




fun updateUI() {
        val layoutManager = LinearLayoutManager(this)

        recyclerView.layoutManager = layoutManager

        adapter = ProductAdapter(Repository.products)

        recyclerView.adapter = adapter
    }


    private val RESULT_CODE_PREFERENCES = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_CODE_PREFERENCES)

        {
            val name = PreferenceHandler.getName(this)
            val notifications = PreferenceHandler.useNotifications(this)
            val message = "Welcome, $name"
            val toast = Toast.makeText(this, message, Toast.LENGTH_LONG)
            toast.show()
            updateUISettings(name, notifications)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //callback function from yes/no dialog - for yes choice
    fun positiveClicked() {
        val toast = Toast.makeText(
                this,
                "Positive button clicked", Toast.LENGTH_LONG
        )
        toast.show()
        Repository.deleteAllProducts()
    }


    //callback function from yes/no dialog - for no choice
    fun negativeClick() {
        //Here we override the method and can now do something
        val toast = Toast.makeText(
                this,
                "Negative button clicked", Toast.LENGTH_LONG
        )
        toast.show()
    }





    // Options
    fun convertListToString(): String
    {
        var result = ""
        for (product in Repository.products)
        {
            result = result + product.toString()
        }
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        Log.d("icon_pressed", "${item.itemId}")
        when (item.itemId) {
            R.id.item_about -> {
                /* Share content */
                val text = convertListToString() //from EditText
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain" //MIME-TYPE
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Shared Data")
                sharingIntent.putExtra(Intent.EXTRA_TEXT, text)
                startActivity(Intent.createChooser(sharingIntent, "Share Using"))
                return true
            }
            R.id.item_delete -> {
                Toast.makeText(this, "Delete item clicked!", Toast.LENGTH_LONG)
                    .show()
                val dialog = MyDialogFragment(::positiveClicked, ::negativeClick)
                dialog.show(supportFragmentManager, "myFragment")

                return true
            }
            R.id.item_help -> {
                Toast.makeText(this, "Help item clicked!", Toast.LENGTH_LONG)
                    .show()
                return true
            }
            R.id.item_signout -> {
                // [START auth_fui_signout]
                AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        // ...
                    }
                // [END auth_fui_signout]
            }
            R.id.item_settings -> {
                //Start our settingsactivity and listen to result - i.e.
                //when it is finished.
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, RESULT_CODE_PREFERENCES)

            }
        }

        return false //we did not handle the event

    }


    }
