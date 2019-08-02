package com.example.twitterdemo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.core.net.toUri
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.add_ticket.view.*
import kotlinx.android.synthetic.main.tweets_ticket.view.*
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {

    var ListTweets = ArrayList<Ticket>()
    var adapter:MyTweetAdapter?=null

    var myEmail:String?=null
    var userUID:String?=null

    private var database=FirebaseDatabase.getInstance()
    private var myRef=database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var b: Bundle? =intent.extras
        myEmail= b!!.getString("email")
        userUID= b!!.getString("uid")
        // dummy data

        loadPosts()
        adapter= MyTweetAdapter(this, ListTweets)
        lvTweets.adapter=adapter
        loadPosts()
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

                myView.iv_attach.setOnClickListener(View.OnClickListener {
                    loadImage()
                })

                myView.iv_post.setOnClickListener(View.OnClickListener {
                    myRef.child("Posts").push().setValue(PostInfo(userUID.toString(), myView.etPost.text.toString(), DownloadURL.toString()))

                    myView.etPost.setText("")
                })
                return myView
            }else{
                var myView = layoutInflater.inflate(R.layout.tweets_ticket, null)
                //load tweet ticket
                myView.txt_tweet.setText(myTweet.tweetText)
                myView.txtUserName.setText(myTweet.personId)
                Picasso.with(context).load(myTweet.tweetImageURL).into(myView.tweet_picture)

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

    val PICK_IMAGE_CODE=123
    fun loadImage(){

        val intent= Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,PICK_IMAGE_CODE)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==PICK_IMAGE_CODE && data!=null){
            val selectedImage=data.data
            val filePathColumn= arrayOf(MediaStore.Images.Media.DATA)
            val cursor= contentResolver.query(selectedImage!!, filePathColumn, null,null,null)
            cursor!!.moveToFirst()
            val columnIndex=cursor.getColumnIndex(filePathColumn[0])
            val picturePath=cursor.getString(columnIndex)
            cursor.close()
            Log.d("picture path ", picturePath)
            UploadImage(BitmapFactory.decodeFile(picturePath))
        }
    }

    var DownloadURL:String?=null
    fun UploadImage(bitmap: Bitmap){
        val storage= FirebaseStorage.getInstance()
        val storeageRef=storage.getReferenceFromUrl("gs://twitterdemo-fdd9d.appspot.com")
        val df= SimpleDateFormat("ddMMyyHHmmss")
        val dataobj= Date()
        val imagePath=splitString(myEmail!!)+df.format(dataobj)+".jpg"
        val imageRef=storeageRef.child("/imagesPost").child(imagePath)

        val baos= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask=imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.d("fail to upload", imagePath)
            Toast.makeText(applicationContext, "fail to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {taskSnapshot ->
            DownloadURL = taskSnapshot.storage.downloadUrl.toString()

        }
    }
    fun splitString(email: String):String{
        val split=email.split("@")
        return split[0]
    }

    fun loadPosts(){
        myRef.child("Posts")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    try {
                        ListTweets.clear()
                        ListTweets.add(Ticket("0","him","url","add"))

                        var td= dataSnapshot!!.value as HashMap<String,Any>
                        for(key in td.keys){
                            var post= td[key] as HashMap<String,Any>
                            ListTweets.add(Ticket(key,
                                post["text"] as String,
                                post["postImage"] as String
                                ,post["userUID"] as String))
                        }
                        adapter!!.notifyDataSetChanged()
                        Log.d("tweets length", ListTweets.size.toString())
                    }catch (ex:Exception){}
                }
                override fun onCancelled(p0: DatabaseError) {
                }
            })
    }
}

