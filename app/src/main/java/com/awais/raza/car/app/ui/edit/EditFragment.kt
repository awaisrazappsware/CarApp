package com.awais.raza.car.app.ui.edit

import android.R
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.awais.raza.car.app.databinding.FragmentEditBinding
import com.awais.raza.car.app.model.Records
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class EditFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding
    var status = arrayOf("New", "Renew", "Due", "Completed")

    private var sYear = 0
    private var sMonth = 0
    private var sDay = 0

    private var eYear = 0
    private var eMonth = 0
    private var eDay = 0

    private var endDate: String? = null
    private var startDate: String? = null
    private var pos = 0

    var category = arrayOf("Sedan", "Crossover", "Hatchback", "Suv", "Others")

    private var pos1 = 0

    private val TAG = "EditFragment"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                navigateDashboard()
            }
        }
        requireActivity().getOnBackPressedDispatcher()
            .addCallback(requireActivity(), onBackPressedCallback)


        binding.spnStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pos = position
            }

        }

        binding.spnCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pos1 = position
            }

        }
        val aa = ArrayAdapter<Any?>(requireContext(), R.layout.simple_spinner_item, status)
        val cc = ArrayAdapter<Any?>(requireContext(), R.layout.simple_spinner_item, category)
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        cc.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        //Setting the ArrayAdapter data on the Spinner
        binding.spnStatus.adapter = aa
        binding.spnCategories.adapter = cc



        Log.d(TAG, "onViewCreated: ${arguments?.getString("rStatus")}")
        Log.d(TAG, "onViewCreated: ${arguments?.getString("rCategory")}")


        val spinnerPosition: Int = aa.getPosition(arguments?.getString("rStatus"))
        binding.spnStatus.setSelection(spinnerPosition)

        val spinnePosition: Int = cc.getPosition(arguments?.getString("rCategory"))
        binding.spnCategories.setSelection(spinnePosition)

        binding.edtNote.setText(arguments?.getString("rNote"))
        binding.edtRegistrationNumber.setText(arguments?.getString("rRegNO"))
        binding.edtName.setText(arguments?.getString("rName"))
        binding.edtMillage.setText(arguments?.getString("rMillage"))
        startDate = arguments?.getString("rStartDate")
        endDate = arguments?.getString("rEndDate")
        binding.edtStartDate.text = arguments?.getString("rStartDate")
        binding.edtEndDate.text = arguments?.getString("rEndDate")

        binding.toolbarEdit.title.text = "Edit Records"

        binding.toolbarEdit.backBtn.setOnClickListener {
            navigateDashboard()
        }


        binding.edtStartDate.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            sYear = c.get(Calendar.YEAR)
            sMonth = c.get(Calendar.MONTH)
            sDay = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    startDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

                    val content = SpannableString(startDate)
                    startDate?.length?.let { it1 -> content.setSpan(UnderlineSpan(), 0, it1, 0) }
                    binding.edtStartDate.text = content

                }, sYear, sMonth, sDay
            )
            datePickerDialog.show()
        }

        binding.edtEndDate.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            eYear = c.get(Calendar.YEAR)
            eMonth = c.get(Calendar.MONTH)
            eDay = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(
                requireContext(),
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                    endDate = dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year

                    val content = SpannableString(endDate)
                    endDate?.length?.let { it1 -> content.setSpan(UnderlineSpan(), 0, it1, 0) }
                    binding.edtEndDate.text = content
                }, eYear, eMonth, eDay
            )
            datePickerDialog.show()
        }

        binding.btnSave.setOnClickListener {

//            val recordsDatabase: RecordsDatabase =
//                RecordsDatabase.getDatabase(requireContext().applicationContext)

//            CoroutineScope(Dispatchers.IO).launch {


            startDate?.let { it1 ->
                endDate?.let { it2 ->

                    val firebaseDatabase = FirebaseDatabase.getInstance()
                    val databaseReference = firebaseDatabase.getReference("Records")
                    val reportReference = firebaseDatabase.getReference("Reports")
                    val key: String? = databaseReference.push().getKey()


                    databaseReference.child(binding.edtRegistrationNumber.text.toString()).setValue(
                        Records(
                            binding.edtRegistrationNumber.text.toString(),
                            binding.edtName.text.toString(),
                            binding.edtMillage.text.toString(),
                            status[pos],
                            category[pos1],
                            it1,
                            it2,
                            binding.edtNote.text.toString()
                        )

                    ).addOnCompleteListener {

                    }
                    key?.let { reportReference.child(it).setValue( Records(
                        binding.edtRegistrationNumber.text.toString(),
                        binding.edtName.text.toString(),
                        binding.edtMillage.text.toString(),
                        status[pos],
                        category[pos1],
                        it1,
                        it2,
                        binding.edtNote.text.toString()
                    )).addOnCompleteListener {
                        navigateDashboard()
                    } }





                } ?: run {
//                    withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "PLease enter Complete Details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } ?: run {
//                    withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "PLease enter Complete Details",
                    Toast.LENGTH_SHORT
                ).show()
            }
//                record?.let { it1 ->
//                    recordsDatabase.recordsDao().addRecord(it1)


            /* databaseReference.orderByChild("rregNO").equalTo(it1.rRegNO).addListenerForSingleValueEvent(
                 object : ValueEventListener {
                     override fun onDataChange(dataSnapshot: DataSnapshot) {

                         Log.d(TAG, "onDataChange: ${dataSnapshot.child("rstatus").value.toString()}")

//                                dataSnapshot.ref.key?.let { it2 -> databaseReference.child(it2).setValue(it1) }
                         databaseReference.ref.setValue(it1)
                         navigateDashboard()
                     }

                     override fun onCancelled(databaseError: DatabaseError) {}
                 }
             )*/


            /*databaseReference.orderByChild("rregNO").equalTo(it1.rRegNO)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (data in dataSnapshot.children) {

                            data.ref.setValue(it1)
                        }
                        navigateDashboard()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })*/
//                } ?: run {
////                    withContext(Dispatchers.Main) {
//                        Toast.makeText(
//                            requireContext(),
//                            "PLease enter Complete Details",
//                            Toast.LENGTH_SHORT
//                        ).show()
////                    }
//                }
//                }
        }

    }

    private fun navigateDashboard() {
        lifecycleScope.launchWhenStarted {
            Navigation.findNavController(
                requireActivity(),
                com.awais.raza.car.app.R.id.fragment_container
            ).navigateUp()
        }
    }


}