package edu.hectorrodriguez.apiphysiocare.ui.main

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.hectorrodriguez.apiphysiocare.databinding.ItemAppointmentsBinding
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsItem
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AppointementAdapter(private val onAppointementClick:(idAppointement:String)->Unit):ListAdapter<AppointmentsItem,AppointementAdapter.AppointementViewHolder>(AppointementDiffCallback()) {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): AppointementViewHolder {
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
