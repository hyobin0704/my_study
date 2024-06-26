package com.ex.firebase

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ex.firebase.MyApplication
import com.ex.firebase.databinding.ItemMainBinding
import com.ex.firebase.ItemData


class MyViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

class MyAdapter(val context: Context, val itemList: MutableList<ItemData>): RecyclerView.Adapter<MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MyViewHolder(ItemMainBinding.inflate(layoutInflater))
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = itemList.get(position)

        holder.binding.run {
            itemEmailView.text=data.email
            itemDateView.text=data.date
            itemContentView.text=data.content
        }

        //스토리지 이미지 다운로드........................
        val imgRef= MyApplication.storage
            .reference
            .child("images/${data.docId}.jpg")
        imgRef.getDownloadUrl().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Glide.with(context )
                    .load(task.result)
                    .into(holder.binding.itemImageView)
            }
        }
        holder.binding.deleteButton.setOnClickListener {
            data.docId?.let { docId ->
                MyApplication.db.collection("news").document(docId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        itemList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, itemList.size)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "삭제 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}