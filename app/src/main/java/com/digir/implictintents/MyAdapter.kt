package com.digir.implictintents

import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class MyAdapter : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    class MyViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contact_name)
        val number: TextView = view.findViewById(R.id.contact_number)
        val callBT: Button = view.findViewById(R.id.call_BT)
        var smsBT: Button = view.findViewById(R.id.sms_BT)
    }


    override fun onCreateViewHolder(viewGrop: ViewGroup, viewType: Int): MyViewHolder {
        var layoutInflater = LayoutInflater.from(viewGrop.context)
        val contactRow = layoutInflater.inflate(R.layout.contact_row, viewGrop, false)
        return MyViewHolder(contactRow)
    }

    override fun getItemCount(): Int {
        return MainActivity.contactList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.setOnLongClickListener(object: View.OnLongClickListener{
            override fun onLongClick(v: View?): Boolean {
                val contact_uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,
                    MainActivity.listID[holder.adapterPosition].toLong())
                val edit_intent = Intent()
                edit_intent.action = Intent.ACTION_EDIT
                edit_intent.data = contact_uri
                startActivity(holder.itemView.context, edit_intent, null)
                return false
            }
        })
        
        if(MainActivity.contactList[holder.adapterPosition].length > 13) {
            holder.name.text = ""
            var x = 0

            while(x < 13) {
                var znak = MainActivity.contactList[holder.adapterPosition].get(x)
                if(x >= 10)
                    holder.name.append(".")
                else
                    holder.name.append(znak.toString())
                x++
            }
        } else {
            holder.name.text = MainActivity.contactList[holder.adapterPosition]
        }


        holder.number.text = MainActivity.contactNumbers[position]

        holder.smsBT.setOnClickListener {
            val sms_intent = Intent()

            if(MainActivity.contactNumbers[position] == "Brak numeru") {
                Toast.makeText(holder.itemView.context, "Nieprawidlowy numer lub brak numeru", Toast.LENGTH_SHORT)
                    .show()
            } else {
                sms_intent.data = Uri.parse("sms: " + holder.number.text)
                sms_intent.action = Intent.ACTION_VIEW
                startActivity(holder.itemView.context, sms_intent, null)
            }
        }
        
        holder.callBT.setOnClickListener {
            val call_intent = Intent()

            if(MainActivity.contactNumbers[position] == "Brak numeru") {
                Toast.makeText(holder.itemView.context, "Nieprawidlowy numer lub brak numeru", Toast.LENGTH_SHORT)
                    .show()
            } else {
                call_intent.data = Uri.parse("tel: " + holder.number.text)
                call_intent.action = Intent.ACTION_DIAL
                startActivity(holder.itemView.context, call_intent, null)
            }
        }
    }

}
