package com.example.twitterdemo

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    var ListTweets = ArrayList<Ticket>()
    var adapter:MyTweetAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // dummy data
        ListTweets.add(Ticket("0", "him", "url", "add"))
        ListTweets.add(Ticket("0", "him", "url", "deivit"))


        adapter = MyTweetAdapter(this, ListTweets)
        lvTweets.adapter=adapter

    }

    inner class MyTweetAdapter: BaseAdapter {

        var listNotesAdapter=ArrayList<Ticket>()
        var context: Context?=null
        constructor(context: Context, listNotesAdapter: ArrayList<Ticket>):super(){
            this.context=context
            this.listNotesAdapter=listNotesAdapter
        }
        override fun getView(p0: Int, p1: View?, p2: ViewGroup): View{
            var myTweet=listNotesAdapter[p0]
            if(myTweet.personId.equals("add", true)){
                var myView = layoutInflater.inflate(R.layout.add_ticket, null)
                //load add ticket

                return myView
            }else{
                var myView = layoutInflater.inflate(R.layout.tweets_ticket, null)
                //load tweet ticket

                return myView
            }
        }
        override fun getItem(p0: Int): Any {

            return listNotesAdapter[p0]
        }
        override fun getCount(): Int {
            return listNotesAdapter.size
        }
        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }
    }
}
