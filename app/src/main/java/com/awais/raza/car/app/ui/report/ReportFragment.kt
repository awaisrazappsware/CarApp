package com.awais.raza.car.app.ui.report

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.awais.raza.car.app.R
import com.awais.raza.car.app.adapter.RecordsAdapter
import com.awais.raza.car.app.databinding.FragmentReportBinding
import com.awais.raza.car.app.listener.OnRecordClickListener
import com.awais.raza.car.app.model.Records
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ReportFragment : Fragment(), OnRecordClickListener {

    private val TAG="ReportFragment"
    private lateinit var binding : FragmentReportBinding

    private var recordsList: ArrayList<Records> = ArrayList()
    private lateinit var recordsAdapter: RecordsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.toolbarReports.title.text = "Reports"

        binding.toolbarReports.backBtn.setOnClickListener {
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


        recordsList.clear()


        binding.searchEditReg.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    val newList =
                        recordsList.filter { it -> it.rRegNO.contains(s.toString()) } as java.util.ArrayList<Records>

                    Log.d(TAG, "onItemSelected: ${newList}")

                    updateRecycler(newList)
                } else {
                    updateRecycler(recordsList)
                }
            }
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

        val firebaseDatabase = FirebaseDatabase.getInstance()
        val reportReference = firebaseDatabase.getReference("Reports")

        reportReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    for (datas in dataSnapshot.children) {

                        val key = datas.key

                        Log.d(TAG, "onDataChange: $key")


                            Log.d(TAG, "onViewCreated: other")
                            recordsList.add(
                                Records(
                                    datas.child("rregNO").value.toString(),
                                    datas.child("rname").value.toString(),
                                    datas.child("rmillage").value.toString(),
                                    datas.key.toString(),
                                    datas.child("rcategory").value.toString(),
                                    datas.child("rstartDate").value.toString(),
                                    datas.child("rendDate").value.toString(),
                                    datas.child("rnote").value.toString()
                                )
                            )


                    }

                    updateRecycler(recordsList)

                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })



    }

    private fun updateRecycler(recordsList: ArrayList<Records>) {
        recordsAdapter = RecordsAdapter(requireContext(), recordsList, this,1)
        binding.recyclerViewAll.adapter = recordsAdapter
    }

    private fun navigateDashboard() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
        }
    }

    override fun onRecordClick(records: Records) {

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
                        val databaseReference = firebaseDatabase.getReference("Reports")


                        databaseReference.child(records.rStatus).removeValue()
                        recordsList.remove(records)


                        recordsAdapter.notifyDataSetChanged()
                        /*val firebaseDatabase = FirebaseDatabase.getInstance()
                        val databaseReference = firebaseDatabase.getReference("Records")

                        databaseReference.orderByChild("rregNO").equalTo(records.rRegNO)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    for (data in dataSnapshot.children) {
                                        data.ref.removeValue()
                                    }

                                    recordsList.remove(records)

                                    binding.txtTotal.text="No of vehicle : ${recordsList.size}"

                                    recordsAdapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {}
                            })*/


//                            }
//                        }
                    })
                .setNegativeButton("cancel",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                .create()

        myQuittingDialogBox.show()


    }
}