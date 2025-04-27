package edu.hectorrodriguez.apiphysiocare.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.hectorrodriguez.apiphysiocare.databinding.ItemAppointmentsBinding
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsItem
import edu.hectorrodriguez.apiphysiocare.utils.isPhysio
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AppointementAdapter(private val onAppointementClick:(idAppointement:String)->Unit,
                          private val onDeleteClick:(idAppointement:String)->Unit):
    ListAdapter<AppointmentsItem, AppointementAdapter.AppointementViewHolder>(AppointementDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointementViewHolder {
        return AppointementViewHolder(
            ItemAppointmentsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).root
        )
    }

    override fun onBindViewHolder(holder: AppointementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    inner class AppointementViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val bind = ItemAppointmentsBinding.bind(view)
        private var number = 0
        fun bind(appointement: AppointmentsItem){
            bind.tvAppointement.setText("Appointement ${adapterPosition+1}")
            val date = appointement.date
            val dateFormar = Instant.parse(date).atZone(ZoneOffset.UTC)
            val resultado = dateFormar.format(DateTimeFormatter.ISO_LOCAL_DATE )

            bind.tvDate.setText(resultado)

            if(isPhysio){
                bind.imgButton.setOnClickListener {
                    onDeleteClick(appointement.id.toString())
                }
                bind.imgButton.visibility = View.VISIBLE
            }else{
                bind.imgButton.visibility = View.GONE
            }

            itemView.setOnClickListener{
                onAppointementClick(appointement.id.toString())
            }
        }
    }
}

class AppointementDiffCallback :DiffUtil.ItemCallback<AppointmentsItem>() {
    override fun areItemsTheSame(oldItem: AppointmentsItem, newItem: AppointmentsItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AppointmentsItem, newItem: AppointmentsItem): Boolean {
        return oldItem == newItem
    }

}
