package com.example.twitterdemo


import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Login : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth


    private var database=FirebaseDatabase.getInstance()
    private var myRef=database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth=FirebaseAuth.getInstance()

        imagePerson.setOnClickListener(View.OnClickListener {
            checkPermission()

        })
    }


    fun loginToFirebase(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("message", "signInWithEmail:success")
                    val user = mAuth.currentUser

                    saveImageInFirebase()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("message", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }

            }

    }

    fun saveImageInFirebase(){
        var currentUser=mAuth!!.currentUser
        val email=currentUser!!.email!!.toString()
        val storage=FirebaseStorage.getInstance()
        val storeageRef=storage.getReferenceFromUrl("gs://twitterdemo-fdd9d.appspot.com")
        val df=SimpleDateFormat("ddMMyyHHmmss")
        val dataobj= Date()
        val imagePath=splitString(email)+df.format(dataobj)+".jpg"
        val imageRef=storeageRef.child("/images"+imagePath)
        imagePerson.isDrawingCacheEnabled=true
        imagePerson.buildDrawingCache()

        val drawable=imagePerson.drawable as BitmapDrawable
        val bitmap=drawable.bitmap
        val baos=ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask=imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Log.d("fail to upload", imagePath)
            Toast.makeText(applicationContext, "fail to upload", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener {taskSnapshot ->
            var DownloadURL = taskSnapshot.storage.downloadUrl!!.toString()
            Log.d("success url", DownloadURL)
            myRef.child("Users").child(currentUser.uid).child("email").setValue(currentUser.email)
            myRef.child("Users").child(currentUser.uid).child("ProfileImage").setValue(DownloadURL)
            loadTweets()
            Log.d("tweets loaded", "true")
        }

    }

    fun splitString(email: String):String{
        val split=email.split("@")
        return split[0]
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.getCurrentUser()

        loadTweets()
    }

    fun loadTweets(){
        var currentUser=mAuth!!.currentUser
        if(currentUser!=null){
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            startActivity(intent)
        }
    }

    val READIMAGE:Int=253
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), READIMAGE)
                return
            }
        }
        Log.d("read storage permission", ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE).toString())
        Log.d("read storage permission", PackageManager.PERMISSION_GRANTED.toString())
        loadImage()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            READIMAGE->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    loadImage()
                }else{
                    Toast.makeText(this,"cannot access your images", Toast.LENGTH_LONG).show()
                }
            }else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }


    }

    val PICK_IMAGE_CODE=123
    fun loadImage(){

        var intent=Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
            imagePerson.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }

    fun checkLogin(view: View){
        Log.d("user", email.text.toString())
        Log.d("password", password.text.toString())
        loginToFirebase(email.text.toString(), email.text.toString())
    }
}
