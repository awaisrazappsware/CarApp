package com.awais.raza.car.app.ui.completed

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.awais.raza.car.app.R
import com.awais.raza.car.app.adapter.RecordsAdapter
import com.awais.raza.car.app.database.RecordsDatabase
import com.awais.raza.car.app.databinding.FragmentCompletedBinding
import com.awais.raza.car.app.listener.OnRecordClickListener
import com.awais.raza.car.app.model.Records
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CompletedFragment : Fragment(), OnRecordClickListener {

    private lateinit var binding: FragmentCompletedBinding
    private lateinit var recordsAdapter: RecordsAdapter
    private val recordsList: ArrayList<Records> = ArrayList()

    private val TAG = "CompletedFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCompletedBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbarCompleted.title.text = "Completed Records"

        binding.toolbarCompleted.backBtn.setOnClickListener {
            navigateDashboard()
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                navigateDashboard()
            }
        }
        requireActivity().getOnBackPressedDispatcher()
            .addCallback(requireActivity(), onBackPressedCallback)

        val recordsDatabase: RecordsDatabase =
            RecordsDatabase.getDatabase(requireContext().applicationContext)
        recordsList.clear()

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("Records")


        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (datas in dataSnapshot.children) {

                        val key = datas.key

                        Log.d(TAG, "onDataChange: $key")
                        val rStatus = datas.child("rstatus").value.toString()

                        if (rStatus == "Completed") {
                            recordsList.add(
                                Records(
                                    datas.child("rregNO").value.toString(),
                                    datas.child("rname").value.toString(),
                                    datas.child("rmillage").value.toString(),
                                    datas.child("rstatus").value.toString(),
                                    datas.child("rcategory").value.toString(),
                                    datas.child("rstartDate").value.toString(),
                                    datas.child("rendDate").value.toString(),
                                    datas.child("rnote").value.toString()
                                )
                            )
                        }

                    }

                    updateRecycler()

                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

//        CoroutineScope(Dispatchers.IO).launch {
//            val list: List<Records> = recordsDatabase.recordsDao().readAllData()
//            recordsList.clear()
//
//
//            for (item in list) {
//
//                if (item.rStatus=="Completed") {
//                    recordsList.add(item)
//                }
//            }
//
//            withContext(Dispatchers.Main)
//            {
//                updateRecycler()
//            }
//        }

        //  navigateDashboard()

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
                com.awais.raza.car.app.R.id.action_completedFragment_to_editFragment, bundle
            )
        }
    }


    override fun onRecordClick(records: Records) {
        val bundle = bundleOf(
            "rRegNO" to records.rRegNO,
            "rName" to records.rName,
            "rMillage" to records.rMillage,
            "rCategory" to records.rCategory,
            "rStatus" to records.rStatus,
            "rStartDate" to records.rStartDate,
            "rEndDate" to records.rEndDate,
            "rNote" to records.rNote
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
//                        val recordsDatabase: RecordsDatabase =
//                            RecordsDatabase.getDatabase(requireContext().applicationContext)

//                        CoroutineScope(Dispatchers.IO).launch {
//                            recordsDatabase.recordsDao().deleteRecord(records)
//                            withContext(Dispatchers.Main) {
                        val firebaseDatabase = FirebaseDatabase.getInstance()
                        val databaseReference = firebaseDatabase.getReference("Records")

                        databaseReference.orderByChild("rregNO").equalTo(records.rRegNO)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (data in dataSnapshot.children) {
                                        data.ref.removeValue()
                                    }

                                    recordsList.remove(records)
                                    recordsAdapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })
//                            }
//                        }
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create()

        myQuittingDialogBox.show()


    }
}