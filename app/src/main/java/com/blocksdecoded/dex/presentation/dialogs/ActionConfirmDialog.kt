package com.blocksdecoded.dex.presentation.dialogs

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blocksdecoded.dex.R
import com.blocksdecoded.dex.utils.inflate
import kotlinx.android.synthetic.main.dialog_action_confirm.*

class ConfirmActionDialog(
    private val listener: Listener,
    private val action: Int,
    private val confirmItems: List<CheckBoxItem>
) : BaseBottomDialog(R.layout.dialog_action_confirm), ConfirmationsAdapter.Listener {

    interface Listener {
        fun onConfirmationSuccess()
    }

    private var adapter = ConfirmationsAdapter(this, confirmItems)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action_confirm_recycler?.adapter = adapter
        action_confirm_recycler?.layoutManager = LinearLayoutManager(context)

        adapter.notifyDataSetChanged()

        action_confirm_title?.text = getString(action) + " Confirm"
        action_confirm?.setText(action)
        action_confirm?.setOnClickListener {
            listener.onConfirmationSuccess()
            dismiss()
        }
    }

    override fun onItemCheckMarkClick(position: Int, checked: Boolean) {
        confirmItems[position].checked = checked
        checkConfirmations()
    }

    private fun checkConfirmations() {
        val allChecked = confirmItems.all { it.checked }

        action_confirm?.isEnabled = allChecked
    }

    companion object {
        fun show(
            activity: FragmentActivity,
            action: Int,
            checkboxItems: List<Int>,
            listener: Listener
        ) {
            val fragment = ConfirmActionDialog(
                listener,
                action,
                checkboxItems.map { CheckBoxItem(it) }
            )

            activity.supportFragmentManager.beginTransaction().apply {
                add(fragment, "bottom_confirm_alert")
                commitAllowingStateLoss()
            }
        }
    }
}

class ConfirmationsAdapter(private var listener: Listener, private val confirmations: List<CheckBoxItem>)
    : RecyclerView.Adapter<ViewHolderConfirmation>() {

    interface Listener {
        fun onItemCheckMarkClick(position: Int, checked: Boolean)
    }

    override fun getItemCount() = confirmations.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderConfirmation {
        return ViewHolderConfirmation(parent.inflate(R.layout.item_confirm), listener)
    }

    override fun onBindViewHolder(holder: ViewHolderConfirmation, position: Int) {
        holder.bind(confirmations[position].text)
    }
}

class ViewHolderConfirmation(
    view: View,
    private val listener: ConfirmationsAdapter.Listener
) : RecyclerView.ViewHolder(view){

    private val confirmCheckbox = itemView.findViewById<CheckBox>(R.id.confirm_checkbox)

    init {
        confirmCheckbox.setOnCheckedChangeListener { _, isChecked ->
            listener.onItemCheckMarkClick(adapterPosition, isChecked)
        }
    }

    fun bind(text: Int) {
        confirmCheckbox.setText(text)
    }
}

class CheckBoxItem(val text: Int, var checked: Boolean = false)
