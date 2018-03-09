package com.example.chua.pokemon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View.GONE
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var message: TextView? = null
    private var RecyclerView: RecyclerView? = null
    private val pokemon = ArrayList<Pokemon>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findView()

        RecyclerView!!.layoutManager = LinearLayoutManager(this, LinearLayout.VERTICAL, false)
        RecyclerView!!.addItemDecoration(DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL))

        fetch()
    }

    private fun findView() {
        message = findViewById(R.id.txtMessage)
        RecyclerView = findViewById(R.id.recyclerView)
    }

    private fun fetch() {

        for(i in 1..20) {
            doAsync {
                val result = "https://pokeapi.co/api/v2/pokemon/$i"
                val Client = OkHttpClient()
                val Request = Request.Builder().url(result).build()
                Client.newCall(Request).enqueue(object : Callback {
                    override fun onResponse(call: Call?, response: Response?) {
                        val Body = response?.body()?.string()
                        val Gson = GsonBuilder().create()
                        val Feed = Gson.fromJson(Body, Pokemon::class.java)

                        uiThread {
                            pokemon.add(Pokemon(Feed.name, Feed.sprites))
                            val adapter = PokemonAdapter(pokemon)
                            RecyclerView!!.adapter = adapter

                            if(pokemon.size!=0)
                            {
                                message!!.text = "You have "+pokemon.size.toString() + " Pokemons"

                            }
                            if(pokemon.size == 20){
                                progressBar.visibility = GONE
                            }
                        }

                    }
                    override fun onFailure(call: Call?, e: IOException?) {
                        println("Failed to execute object request")
                    }
                })
            }
        }
    }
}