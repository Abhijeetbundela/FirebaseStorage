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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.firebasestorage.R
import com.example.firebasestorage.activities.FullImageActivity
import com.example.firebasestorage.dialog.LoadingDialog
import com.example.firebasestorage.model.Post
import com.example.firebasestorage.model.User
import com.example.firebasestorage.utils.FileNameUtils
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView


class AllPostFragment : Fragment() {

    private val mDatabase by lazy { FirebaseDatabase.getInstance().reference }
    private val database by lazy { FirebaseFirestore.getInstance() }

    private val loadingDialog by lazy { LoadingDialog().apply { isCancelable = false } }

    private var mAdapter: FirebaseRecyclerAdapter<Post, AllPostVM>? = null
    private lateinit var mRecycler: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentManager?.let { loadingDialog.show(it, "base_loading_dialog") }

        val rootView: View =
            inflater.inflate(R.layout.fragment_all_post, container, false)

        mRecycler = rootView.findViewById(R.id.all_post_recycler_view)
        val mManager = LinearLayoutManager(activity)
        mRecycler.recycledViewPool.setMaxRecycledViews(1, 0)
        mRecycler.layoutManager = mManager
        mRecycler.setHasFixedSize(true)

        fetchAllPostData()

        return rootView
    }

    private fun fetchAllPostData() {

        val postsQuery =
            mDatabase.child("posts").limitToFirst(100)

        val options: FirebaseRecyclerOptions<Post> = FirebaseRecyclerOptions.Builder<Post>()
            .setQuery(postsQuery, Post::class.java)
            .build()

        mAdapter = object : FirebaseRecyclerAdapter<Post, AllPostVM>(options) {

            override fun onDataChanged() {
                super.onDataChanged()
                loadingDialog.dismiss()
            }

            override fun onError(error: DatabaseError) {
                super.onError(error)
                loadingDialog.dismiss()
            }

            override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): AllPostVM {
                val inflater = LayoutInflater.from(viewGroup.context)
                return AllPostVM(
                    inflater.inflate(
                        R.layout.item_all_post,
                        viewGroup,
                        false
                    )
                )
            }

            override fun onBindViewHolder(
                holder: AllPostVM,
                position: Int,
                model: Post
            ) {

                loadingDialog.dismiss()

                holder.description.text = model.description

                when (model.fileType) {
                    "PDF" -> {
                        FileNameUtils.setUIAllPost(
                            R.drawable.ic_picture_as_pdf,
                            holder,
                            model,
                            context!!
                        )
                        holder.image.visibility = View.GONE
                    }
                    "DOC" -> {
                        FileNameUtils.setUIAllPost(
                            R.drawable.ic_attach_file_24,
                            holder,
                            model,
                            context!!
                        )
                        holder.image.visibility = View.GONE
                    }
                    "AUDIO" -> {
                        FileNameUtils.setUIAllPost(
                            R.drawable.ic_audio_track_24,
                            holder,
                            model,
                            context!!
                        )
                        holder.image.visibility = View.GONE
                    }
                    "VIDEO" -> {
                        FileNameUtils.setUIAllPost(R.drawable.ic_video_24, holder, model, context!!)
                        holder.image.visibility = View.GONE
                    }
                    "IMAGE" -> {
                        holder.image.visibility = View.VISIBLE
                        Glide.with(context!!)
                            .load(model.file)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
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

                database.collection("users").document(model.userId).get()
                    .addOnSuccessListener { snapshot ->

                        if (snapshot.exists()) {
                            val user = snapshot.toObject(User::class.java)!!

                            holder.userName.text = user.userName

                            holder.downloadBtn.setOnClickListener {
                                FileNameUtils.downloadFile(model, context!!)
                            }

                            Glide.with(context!!)
                                .load(user.profileUri)
                                .placeholder(R.drawable.default_user)
                                .into(holder.userProfile)
                        }

                    }
            }
        }
        mRecycler.adapter = mAdapter

    }

    class AllPostVM(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.all_user_name)
        val userProfile: CircleImageView = itemView.findViewById(R.id.all_profile_image)
        val fileText: TextView = itemView.findViewById(R.id.all_file_ext)
        val image: ImageView = itemView.findViewById(R.id.all_image)
        val description: TextView = itemView.findViewById(R.id.all_description)
        val downloadBtn: ImageView = itemView.findViewById(R.id.all_post_download_btn)
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