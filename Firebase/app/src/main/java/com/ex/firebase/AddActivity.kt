package com.ex.firebase

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ex.firebase.databinding.ActivityAddBinding
import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class AddActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddBinding
    lateinit var filePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }
    val requestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult())
    {
        if(it.resultCode === android.app.Activity.RESULT_OK){
            Glide
                .with(getApplicationContext())
                .load(it.data?.data)
                .apply(RequestOptions().override(250, 200))
                .centerCrop()
                .into(binding.addImageView)

            val cursor = contentResolver.query(it.data?.data as Uri,
                arrayOf<String>(MediaStore.Images.Media.DATA), null, null, null);
            cursor?.moveToFirst().let {
                filePath=cursor?.getString(0) as String
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_add_gallery){
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            requestLauncher.launch(intent)
        }else if(item.itemId == R.id.menu_add_save){
            if(binding.addImageView.drawable !== null && binding.addEditView.text.isNotEmpty()){
                saveStore()
            }else {
                Toast.makeText(this, "데이터가 모두 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun saveStore(){
        val data = mapOf(
            "email" to MyApplication.email,
            "content" to binding.addEditView.text.toString(),
            "date" to dateToString(Date())
        )
        MyApplication.db.collection("news")
            .add(data)
            .addOnSuccessListener {
                uploadImage(it.id)
            }
            .addOnFailureListener {
                Log.w("kkang", "data save error", it)
            }
    }
    private fun uploadImage(docId: String){
        val storage = MyApplication.storage
        val storageRef: StorageReference = storage.reference
        val imgRef: StorageReference = storageRef.child("images/${docId}.jpg")
        var file = Uri.fromFile(File(filePath))
        imgRef.putFile(file)
            .addOnFailureListener {
                Log.d("kkang"," failure............."+it)
            }.addOnSuccessListener {
                Toast.makeText(this, "데이터가 저장되었습니다.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
    }
}
