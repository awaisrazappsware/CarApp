package com.awais.raza.car.app.ui.record

import android.R
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.awais.raza.car.app.databinding.FragmentRecordBinding
import com.awais.raza.car.app.model.Records
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*


class RecordFragment : Fragment(){
    var status = arrayOf("New", "Completed")
    var category = arrayOf("Sedan", "Crossover","Hatchback","Suv","Others")

    private var sYear = 0
    private var sMonth = 0
    private var sDay = 0

    private var eYear = 0
    private var eMonth = 0
    private var eDay = 0

    private var endDate: String? = null
    private var startDate: String? = null
    private var pos = 0
    private var pos1 = 0


    private lateinit var binding: FragmentRecordBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecordBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)




        binding.spnStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    pos = position
            }

        }

        binding.spnCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
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


        binding.toolbarRecord.title.text = "Add Records"

        binding.toolbarRecord.backBtn.setOnClickListener {
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

        binding.edtStartDate.setOnClickListener {
            // Get Current Date
            val c: Calendar = Calendar.getInstance()
            sYear = c.get(Calendar.YEAR)
            sMonth = c.get(Calendar.MONTH)
            sDay = c.get(Calendar.DAY_OF_MONTH)


            val datePickerDialog = DatePickerDialog(
                requireContext(),
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

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
                OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

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


            val record =

                startDate?.let { it1 ->
                    endDate?.let { it2 ->
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
                    }
                }

            record?.let { it1 ->
                sendDataToFirebase(it1)
//                    recordsDatabase.recordsDao().addRecord(it1)
//                    navigateDashboard()
            } ?: run {
//                    withContext(Dispatchers.Main) {
                Toast.makeText(
                    requireContext(),
                    "PLease enter Complete Details",
                    Toast.LENGTH_SHORT
                ).show()
//                    }
            }
//            }
        }

    }

    private fun sendDataToFirebase(records: Records) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference = firebaseDatabase.getReference("Records")
        val key: String? = databaseReference.push().getKey()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // inside the method of on Data change we are setting
                // our object class to our database reference.
                // data base reference will sends data to firebase.


                key?.let { databaseReference.child(it).setValue(records) }



                // after adding this data we are showing toast message.
                try {

                    Toast.makeText(requireContext(), "data added", Toast.LENGTH_SHORT).show()

//                        navigateDashboard2()

                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
//                navigateDashboard()
            }

            override fun onCancelled(error: DatabaseError) {
                // if the data is not added or it is cancelled then
                // we are displaying a failure toast message.
                try {

                    Toast.makeText(requireContext(), "Fail to add data $error", Toast.LENGTH_SHORT)
                        .show()
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

            }
        })

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