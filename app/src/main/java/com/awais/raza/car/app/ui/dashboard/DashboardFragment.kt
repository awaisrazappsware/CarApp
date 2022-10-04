package com.awais.raza.car.app.ui.dashboard

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.awais.raza.car.app.adapter.RecordsAdapter
import com.awais.raza.car.app.databinding.FragmentDashboardBinding
import com.awais.raza.car.app.listener.OnRecordClickListener
import com.awais.raza.car.app.model.Records
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class DashboardFragment : Fragment(), OnRecordClickListener {

    private val TAG = "DashboardFragment"
    private lateinit var binding: FragmentDashboardBinding

    private lateinit var recordsAdapter: RecordsAdapter
    private val recordsList: ArrayList<Records> = ArrayList()


    private var totalCount =0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarDash.backBtn.visibility = View.GONE
        binding.toolbarDash.title.text = "All Records"

//        val recordsDatabase: RecordsDatabase =
//            RecordsDatabase.getDatabase(requireContext().applicationContext)

//        CoroutineScope(Dispatchers.IO).launch {
//            val list: List<Records> = recordsDatabase.recordsDao().readAllData()
        recordsList.clear()


        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("Records")


        databaseReference
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    totalCount=dataSnapshot.children.count()
                    binding.txtTotal.text="No of vehicle : $totalCount"
                    for (datas in dataSnapshot.children) {

                        val key = datas.key

                        Log.d(TAG, "onDataChange: $key")

                        val rStatus = datas.child("rstatus").value.toString()

                        val sdf = SimpleDateFormat("dd-MM-yyyy")
                        val strDate = sdf.parse(datas.child("rendDate").value.toString())
                        if (Date().after(strDate)&&rStatus != "Completed"&&rStatus != "Renew") {
                            Log.d(TAG, "onViewCreated: due")
                            recordsList.add(Records(
                                datas.child("rregNO").value.toString(),
                                datas.child("rname").value.toString(),
                                datas.child("rmillage").value.toString(),
                                "Due",
                                datas.child("rcategory").value.toString(),
                                datas.child("rstartDate").value.toString(),
                                datas.child("rendDate").value.toString(),
                                datas.child("rnote").value.toString()
                            ))
                        } else if (Date().before(strDate)) {
                            Log.d(TAG, "onViewCreated: under")
                            recordsList.add(Records(
                                datas.child("rregNO").value.toString(),
                                datas.child("rname").value.toString(),
                                datas.child("rmillage").value.toString(),
                                datas.child("rstatus").value.toString(),
                                datas.child("rcategory").value.toString(),
                                datas.child("rstartDate").value.toString(),
                                datas.child("rendDate").value.toString(),
                                datas.child("rnote").value.toString()
                            ))
                        }else
                        {
                            Log.d(TAG, "onViewCreated: other")
                            recordsList.add(Records(
                                datas.child("rregNO").value.toString(),
                                datas.child("rname").value.toString(),
                                datas.child("rmillage").value.toString(),
                                datas.child("rstatus").value.toString(),
                                datas.child("rcategory").value.toString(),
                                datas.child("rstartDate").value.toString(),
                                datas.child("rendDate").value.toString(),
                                datas.child("rnote").value.toString()
                            ))
                        }

                    }

                    updateRecycler()

                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })

//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                recordsList.clear()
//                for (postSnapshot in snapshot.children) {
//                    val records: Records ?= postSnapshot.getValue(Records::class.java)
//                    Log.d(TAG, "onDataChange: ${records?.rRegNO}")
//                    records?.let { recordsList.add(it) }
//
//                    // here you can access to name property like university.name
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                System.out.println("The read failed: " + databaseError.getMessage())
//            }
//        })

//        for (item in list) {
//
//                val sdf = SimpleDateFormat("dd-MM-yyyy")
//                val strDate = sdf.parse(item.rEndDate)
//
//                if (Date().after(strDate)) {
//                    Log.d(TAG, "onViewCreated: due")
//
//                    item.rStatus = "Due"
//                    recordsList.add(item)
//                } else if (Date().before(strDate)) {
//                    Log.d(TAG, "onViewCreated: under")
//                    recordsList.add(item)
//                }
//            }

//            withContext(Dispatchers.Main)
//            {
        updateRecycler()
//            }
//        }

        binding.addBtn.setOnClickListener {
            navigateRecord()
        }
        binding.completedRecords.setOnClickListener {
            navigateCompleted()
        }
        binding.dueRecords.setOnClickListener {
            navigateDue()
        }


        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                val myQuittingDialogBox: AlertDialog =
                    AlertDialog.Builder(requireActivity()) // set message, title, and icon
                        .setTitle("Exit")
                        .setMessage("Do you want to Exit")
                        .setPositiveButton(
                            "Exit",
                            DialogInterface.OnClickListener { dialog, whichButton -> //your deleting code
                                dialog.dismiss()
                                requireActivity().finish()
                            })
                        .setNegativeButton("cancel",
                            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
                        .create()
                myQuittingDialogBox.show()
            }
        }
        requireActivity().getOnBackPressedDispatcher()
            .addCallback(requireActivity(), onBackPressedCallback)
    }

    //1Day
//Due
    private fun updateRecycler() {
        recordsAdapter = RecordsAdapter(requireContext(), recordsList, this)
        binding.recyclerViewAll.adapter = recordsAdapter

    }


    private fun navigateRecord() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(
                requireActivity(),
                com.awais.raza.car.app.R.id.fragment_container
            ).navigate(
                com.awais.raza.car.app.R.id.action_dashboardFragment_to_recordFragment
            )
        }
    }

    private fun navigateCompleted() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(
                requireActivity(),
                com.awais.raza.car.app.R.id.fragment_container
            ).navigate(
                com.awais.raza.car.app.R.id.action_dashboardFragment_to_completedFragment
            )
        }
    }

    private fun navigateDue() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(
                requireActivity(),
                com.awais.raza.car.app.R.id.fragment_container
            ).navigate(
                com.awais.raza.car.app.R.id.action_dashboardFragment_to_dueFragment
            )
        }
    }

    private fun navigateEdit(bundle: Bundle) {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(
                requireActivity(),
                com.awais.raza.car.app.R.id.fragment_container
            ).navigate(
                com.awais.raza.car.app.R.id.action_dashboardFragment_to_editFragment, bundle
            )
        }
    }


    override fun onRecordClick(records: Records) {
        Log.d(TAG, "onRecordClick: ${records.rName}")
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


                        databaseReference.child(records.rRegNO).removeValue()
                        recordsList.remove(records)

                        binding.txtTotal.text="No of vehicle : ${recordsList.size}"

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