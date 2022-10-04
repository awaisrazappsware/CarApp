package com.awais.raza.car.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.awais.raza.car.app.R
import com.awais.raza.car.app.databinding.ItemRocordBinding
import com.awais.raza.car.app.listener.OnRecordClickListener
import com.awais.raza.car.app.model.Records


class RecordsAdapter(
    private val context: Context,
    private val mList: ArrayList<Records>,
    private val onRecordClickListener: OnRecordClickListener,

) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemRocordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ItemViewHolder(val binding: ItemRocordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as ItemViewHolder
        holder.binding.name.text = "Name: "+mList[position].rName
        holder.binding.registration.text = "Reg no: "+mList[position].rRegNO
        holder.binding.millage.text = mList[position].rMillage
        holder.binding.startDate.text = mList[position].rStartDate
        holder.binding.endDate.text = mList[position].rEndDate

        Log.d("DashboardFragment", "onBindViewHolder: ${mList[position].rStatus}")
        if (mList[position].rStatus=="New"){
            holder.binding.mainBg.setBackgroundResource(R.drawable.recordblue)
        }else if (mList[position].rStatus=="Completed"){
            holder.binding.mainBg.setBackgroundResource(R.drawable.recordgreen)
        }else if (mList[position].rStatus=="Renew"){
            holder.binding.mainBg.setBackgroundResource(R.drawable.recordpurple)
        }else if (mList[position].rStatus=="Due"){
            holder.binding.mainBg.setBackgroundResource(R.drawable.recordred)
        }

        if(mList[position].rCategory=="Sedan"){
            holder.binding.image.setImageResource(R.drawable.sedan)
        }else if(mList[position].rCategory=="Crossover"){
            holder.binding.image.setImageResource(R.drawable.kia)
        }else if(mList[position].rCategory=="Hatchback"){
            holder.binding.image.setImageResource(R.drawable.hatchback)
        }else if(mList[position].rCategory=="Suv"){
            holder.binding.image.setImageResource(R.drawable.suv)
        }else if(mList[position].rCategory=="Others"){
            holder.binding.image.setImageResource(R.drawable.other)
        }

        holder.itemView.setOnClickListener{
            onRecordClickListener.onRecordClick(mList[position])
        }
        holder.binding.delete.setOnClickListener{
            onRecordClickListener.onRecordDelete(mList[position])
        }

    }

}