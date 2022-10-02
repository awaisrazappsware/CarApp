package com.awais.raza.car.app.ui.edit

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.awais.raza.car.app.database.RecordsDatabase
import com.awais.raza.car.app.databinding.FragmentEditBinding
import com.awais.raza.car.app.model.Records
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class EditFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentEditBinding
    var status = arrayOf("New", "Renew", "Completed")

    private var sYear = 0
    private var sMonth = 0
    private var sDay = 0

    private var eYear = 0
    private var eMonth = 0
    private var eDay = 0

    private var endDate: String? = null
    private var startDate: String? = null
    private var pos = 0
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




        binding.spnStatus.onItemSelectedListener = this
        val aa = ArrayAdapter<Any?>(requireContext(), android.R.layout.simple_spinner_item, status)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Setting the ArrayAdapter data on the Spinner
        //Setting the ArrayAdapter data on the Spinner
        binding.spnStatus.adapter = aa


        binding.edtRegistrationNumber.setText(arguments?.getString("rRegNO"))
        binding.edtName.setText(arguments?.getString("rName"))
        binding.edtMillage.setText(arguments?.getString("rMillage"))
        startDate= arguments?.getString("rStartDate")
        endDate= arguments?.getString("rEndDate")
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

            val recordsDatabase: RecordsDatabase =
                RecordsDatabase.getDatabase(requireContext().applicationContext)

            CoroutineScope(Dispatchers.IO).launch {


                val record =

                    startDate?.let { it1 ->
                        endDate?.let { it2 ->
                            Records(
                                binding.edtRegistrationNumber.text.toString(),
                                binding.edtName.text.toString(),
                                binding.edtMillage.text.toString(),
                                status[pos],
                                it1,
                                it2
                            )
                        }
                    }

                record?.let { it1 ->
                    recordsDatabase.recordsDao().addRecord(it1)
                    navigateDashboard()
                } ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            requireContext(),
                            "PLease enter Complete Details",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
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

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        pos = p2
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }
}