package edu.hectorrodriguez.apiphysiocare.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import edu.hectorrodriguez.apiphysiocare.databinding.ItemRecordsBinding
import edu.hectorrodriguez.apiphysiocare.model.appointements.AppointmentsItem
import edu.hectorrodriguez.apiphysiocare.model.records.RecordItemWithPatient

class RecordAdapter(private val onRecordClick:(idRecord:String)-> Unit):  ListAdapter<RecordItemWithPatient, RecordAdapter.RecordViewHolder>(RecordDiffCallBack()
) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecordViewHolder {
        return RecordViewHolder(
            ItemRecordsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false,
            ).root
        )
    }

    override fun onBindViewHolder(
        holder: RecordViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class RecordViewHolder(view:View): RecyclerView.ViewHolder(view){
        private val bind = ItemRecordsBinding.bind(view)
        fun bind(record: RecordItemWithPatient){
            bind.tvRecord.text = "Record: $adapterPosition"
            bind.tvNamePatient.text = "Name: ${record.patient?.name} + ${record.patient?.surname}"
            bind.tvInsuranseNumber.text = "Insuranse Number: ${record.patient?.insuranceNumber}"
            itemView.setOnClickListener {
                onRecordClick(record.id.toString())
            }
        }
    }
}

class RecordDiffCallBack: DiffUtil.ItemCallback<RecordItemWithPatient>(){
    override fun areItemsTheSame(
        oldItem: RecordItemWithPatient,
        newItem: RecordItemWithPatient
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: RecordItemWithPatient,
        newItem: RecordItemWithPatient
    ): Boolean {
        return oldItem == newItem
    }

}