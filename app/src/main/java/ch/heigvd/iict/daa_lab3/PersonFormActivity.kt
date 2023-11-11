package ch.heigvd.iict.daa_lab3

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import ch.heigvd.iict.daa_lab3.databinding.ActivityPersonFormBinding
import ch.heigvd.iict.daa_lab3.utils.Person
import ch.heigvd.iict.daa_lab3.utils.Student
import ch.heigvd.iict.daa_lab3.utils.Worker
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

/**
 * @author Cochet Yvan, Anthonponrajkumar Pirakasraj, Chollet Bastian
 * @brief Activité d'un formulaire récoltant diverses information sur un personne pouvant
 * être un étudiant ou un employé
 */
class PersonFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonFormBinding
    private var calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inflation de la vue
        binding = ActivityPersonFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainBaseOccupationRdg.setOnCheckedChangeListener { _, choiceId ->
            when (choiceId) {
                R.id.rdb_student -> setSpecificFormView(VISIBLE, GONE)
                R.id.rdb_worker -> setSpecificFormView(GONE, VISIBLE)
                else -> setSpecificFormView(GONE, GONE)
            }
        }

        binding.mainBaseBirthdateBtn.setOnClickListener {
            openDatePickerDialog()
        }

        binding.mainBaseBirthdateInput.setOnClickListener {
            openDatePickerDialog()
        }

        binding.okBtn.setOnClickListener {
            Log.d("Person created", createPerson().toString())
        }

        binding.cancelBtn.setOnClickListener {
            emptyForm()
        }

        binding.additionalEmailInput.setOnEditorActionListener { _, _, _ ->
            binding.okBtn.performClick()
            true
        }


        //Fill form with example Person
        fillForm(Person.exampleWorker)
    }


    private fun setSpecificFormView(studentView: Int, workerView: Int) {
        //Set de la vue étudiant
        binding.studentGroup.visibility = studentView

        //Set de la vue employé
        binding.workerGroup.visibility = workerView
    }

    private fun openDatePickerDialog() {

        val today = Calendar.getInstance()

        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -110)

        val constraintsBuilder = CalendarConstraints.Builder()
        constraintsBuilder.setStart(minDate.timeInMillis)
        constraintsBuilder.setEnd(today.timeInMillis)
        val constraints = constraintsBuilder.build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.main_base_birthdate_title)
            .setCalendarConstraints(constraints)
            .build()

        datePicker.isCancelable = false
        datePicker.addOnPositiveButtonClickListener {
             fillDate(it)
        }

        datePicker.show(supportFragmentManager, "DatePicker")
    }

    private fun fillDate(time : Long){
        calendar.apply { timeInMillis = time }.time
        // formater la date pour l'afficher dans le champ de texte en utilisant le format local
        val formattedDate = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
            .format(calendar.time)

        // Mettez à jour votre champ de texte avec la date formatée
        binding.mainBaseBirthdateInput.setText(formattedDate)
    }

    private fun fillForm(person: Person?) {
        if (person == null) {
            return
        }

        //Fill common values
        binding.mainBaseNameInput.setText(person.name)
        binding.mainBaseFirstnameInput.setText(person.firstName)
        fillDate(person.birthDay.timeInMillis)
        binding.mainBaseNationalitySpinner.setSelection(
            resources.getStringArray(R.array.nationalities).indexOf(person.nationality)
        )
        binding.additionalEmailInput.setText(person.email)
        binding.additionalRemarksInput.setText(person.remark)

        when (person) {
            is Worker -> {
                binding.rdbWorker.isChecked = true
                binding.mainSpecificCompagnyInput.setText(person.company)
                binding.mainSpecificSectorSpinner.setSelection(
                    resources.getStringArray(R.array.sectors).indexOf(person.sector)
                )
                binding.mainSpecificExperienceInput.setText(person.experienceYear.toString())
            }

            is Student -> {
                binding.rdbStudent.isChecked = true
                binding.mainSpecificSchoolInput.setText(person.university)
                binding.mainSpecificGraduationyearInput.setText(person.graduationYear.toString())
            }
        }
    }



    private fun emptyForm() {
        binding.mainBaseNameInput.setText("")
        binding.mainBaseFirstnameInput.setText("")
        binding.mainBaseBirthdateInput.setText("")
        binding.mainBaseNationalitySpinner.setSelection(0)
        binding.mainSpecificCompagnyInput.setText("")
        binding.mainSpecificSectorSpinner.setSelection(0)
        binding.mainSpecificExperienceInput.setText("")
        binding.additionalEmailInput.setText("")
        binding.additionalRemarksInput.setText("")
        binding.mainSpecificSchoolInput.setText("")
        binding.mainSpecificGraduationyearInput.setText("")
        binding.mainBaseOccupationRdg.clearCheck()
    }

    private fun createPerson(): Person? {
        val name = binding.mainBaseNameInput.text?.toString()
        val firstName = binding.mainBaseFirstnameInput.text?.toString()
        val nationality = binding.mainBaseNationalitySpinner.selectedItem?.toString()
        val email = binding.additionalEmailInput.text?.toString()
        val remarks = binding.additionalRemarksInput.text?.toString()

        if (!name.isNullOrBlank() && !firstName.isNullOrBlank() && !nationality.isNullOrBlank() && !email.isNullOrBlank() && !remarks.isNullOrBlank()) {
            if (binding.rdbWorker.isChecked) {
                val company = binding.mainSpecificCompagnyInput.text?.toString()
                val sector = binding.mainSpecificSectorSpinner.selectedItem?.toString()
                val experienceYear = binding.mainSpecificExperienceInput.text?.toString()?.toIntOrNull()

                if (!company.isNullOrBlank() && !sector.isNullOrBlank() && experienceYear != null) {
                    return Worker(name, firstName, calendar, nationality, company, sector, experienceYear, email, remarks)
                }
            } else if (binding.rdbStudent.isChecked) {
                val school = binding.mainSpecificSchoolInput.text?.toString()
                val graduationYear = binding.mainSpecificGraduationyearInput.text?.toString()?.toIntOrNull()

                if (!school.isNullOrBlank() && graduationYear != null) {
                    return Student(name, firstName, calendar, nationality, school, graduationYear, email, remarks)
                }
            }
        }

        return null
    }
}
