package com.awais.raza.car.app.ui.due

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.awais.raza.car.app.R
import com.awais.raza.car.app.adapter.RecordsAdapter
import com.awais.raza.car.app.database.RecordsDatabase
import com.awais.raza.car.app.databinding.FragmentDueBinding
import com.awais.raza.car.app.listener.OnRecordClickListener
import com.awais.raza.car.app.model.Records
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*


class DueFragment : Fragment(), OnRecordClickListener {

    private lateinit var binding: FragmentDueBinding
    private lateinit var recordsAdapter: RecordsAdapter
    private val recordsList: ArrayList<Records> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDueBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbarDuw.title.text = "Due Records"

        binding.toolbarDuw.backBtn.setOnClickListener {
            navigateDashboard()
        }

        val recordsDatabase: RecordsDatabase =
            RecordsDatabase.getDatabase(requireContext().applicationContext)

        CoroutineScope(Dispatchers.IO).launch {
            val list: List<Records> = recordsDatabase.recordsDao().readAllData()
            recordsList.clear()


            for (item in list) {

                val sdf = SimpleDateFormat("dd-MM-yyyy")
                val strDate = sdf.parse(item.rEndDate)

                if (Date().after(strDate)) {
                    item.rStatus = "Due"
                    recordsList.add(item)
                }
            }

            withContext(Dispatchers.Main)
            {
                updateRecycler()
            }
        }

        // navigateDashboard()

    }

    private fun updateRecycler() {
        recordsAdapter = RecordsAdapter(requireContext(), recordsList, this)
        binding.recyclerViewAll.adapter = recordsAdapter
    }


    private fun navigateDashboard() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
        }
    }

    private fun navigateEdit(bundle: Bundle) {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(
                requireActivity(),
                com.awais.raza.car.app.R.id.fragment_container
            ).navigate(
                com.awais.raza.car.app.R.id.action_dueFragment_to_editFragment, bundle
            )
        }
    }


    override fun onRecordClick(records: Records) {
        val bundle = bundleOf(
            "rRegNO" to records.rRegNO,
            "rName" to records.rName,
            "rMillage" to records.rMillage,
            "rStatus" to records.rStatus,
            "rStartDate" to records.rStartDate,
            "rEndDate" to records.rEndDate
        )

        navigateEdit(bundle)
    }

    override fun onRecordDelete(records: Records) {


        val myQuittingDialogBox: AlertDialog =
            AlertDialog.Builder(requireContext()) // set message, title, and icon
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(com.awais.raza.car.app.R.drawable.ic_delete)
                .setPositiveButton(
                    "Delete",
                    DialogInterface.OnClickListener { dialog, whichButton -> //your deleting code
                        dialog.dismiss()
                        val recordsDatabase: RecordsDatabase =
                            RecordsDatabase.getDatabase(requireContext().applicationContext)

                        CoroutineScope(Dispatchers.IO).launch {
                            recordsDatabase.recordsDao().deleteRecord(records)
                            withContext(Dispatchers.Main) {
                                recordsList.remove(records)
                                recordsAdapter.notifyDataSetChanged()
                            }
                        }
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create()

        myQuittingDialogBox.show()


    }
}