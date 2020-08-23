package com.example.firebasestorage.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firebasestorage.R
import com.example.firebasestorage.activities.FullImageActivity
import com.example.firebasestorage.dialog.LoadingDialog
import com.example.firebasestorage.model.Post
import com.example.firebasestorage.utils.FileNameUtils
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

class PostFragment : Fragment() {

    private val mDatabase by lazy { FirebaseDatabase.getInstance().reference }
    private val userId by lazy { FirebaseAuth.getInstance().uid!! }

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    private var mAdapter: FirebaseRecyclerAdapter<Post, PostVH>? = null
    private lateinit var mRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentManager?.let { loadingDialog.show(it, "base_loading_dialog") }

        val rootView: View =
            inflater.inflate(R.layout.fragment_post, container, false)

        mRecycler = rootView.findViewById(R.id.post_recycler_view)
        val mManager = LinearLayoutManager(activity)
        mRecycler.recycledViewPool.setMaxRecycledViews(1, 0)
        mRecycler.layoutManager = mManager
        mRecycler.setHasFixedSize(true)

        fetchPostData()

        return rootView
    }

    private fun fetchPostData() {

        val postsQuery: Query =
            mDatabase.child("user-posts").child(userId)

        val options: FirebaseRecyclerOptions<Post> = FirebaseRecyclerOptions.Builder<Post>()
            .setQuery(postsQuery, Post::class.java)
            .build()

        mAdapter = object : FirebaseRecyclerAdapter<Post, PostVH>(options) {

            override fun onDataChanged() {
                super.onDataChanged()
                loadingDialog.dismiss()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loadingDialog.dismiss()
            }

            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): PostVH {
                val inflater = LayoutInflater.from(viewGroup.context)
                return PostVH(inflater.inflate(R.layout.item_post, viewGroup, false))
            }

            override fun onBindViewHolder(
                holder: PostVH,
                position: Int,
                model: Post
            ) {

                loadingDialog.dismiss()

                holder.description.text = model.description

                holder.downloadBtn.setOnClickListener {
                    FileNameUtils.downloadFile(model, context!!)
                }

                when (model.fileType) {
                    "PDF" -> {
                        FileNameUtils.setUIPost(R.drawable.ic_picture_as_pdf, holder, model, context!!)
                        holder.image.visibility = View.GONE
                    }
                    "DOC" -> {
                        FileNameUtils.setUIPost(R.drawable.ic_attach_file_24, holder, model, context!!)
                        holder.image.visibility = View.GONE
                    }
                    "AUDIO" -> {
                        FileNameUtils.setUIPost(R.drawable.ic_audio_track_24, holder, model, context!!)
                        holder.image.visibility = View.GONE
                    }
                    "VIDEO" -> {
                        FileNameUtils.setUIPost(R.drawable.ic_video_24, holder, model, context!!)
                        holder.image.visibility = View.GONE
                    }
                    "IMAGE" -> {
                        holder.image.visibility = View.VISIBLE
                        Glide.with(context!!)
                            .load(model.file)
                            .placeholder(R.drawable.default_user)
                            .into(holder.image)

                        holder.image.setOnClickListener {
                            val intent = Intent(context, FullImageActivity::class.java)
                            intent.putExtra("profilePhoto", model.file)
                            startActivity(intent)
                        }

                        holder.fileText.visibility = View.GONE
                    }
                }

            }
        }
        mRecycler.adapter = mAdapter

    }

    class PostVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.post_description)
        val downloadBtn: ImageView = itemView.findViewById(R.id.download_post_btn)
        val fileText: TextView = itemView.findViewById(R.id.file_text)
        val image: ImageView = itemView.findViewById(R.id.post_image)
    }

    override fun onStart() {
        super.onStart()
        if (mAdapter != null) {
            mAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mAdapter != null) {
            mAdapter!!.stopListening()
        }
    }

}