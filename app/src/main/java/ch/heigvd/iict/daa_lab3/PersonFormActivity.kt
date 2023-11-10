package ch.heigvd.iict.daa_lab3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import ch.heigvd.iict.daa_lab3.databinding.ActivityPersonFormBinding
import ch.heigvd.iict.daa_lab3.utils.Person
import ch.heigvd.iict.daa_lab3.utils.Student
import ch.heigvd.iict.daa_lab3.utils.Worker
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.DateFormat
import java.text.SimpleDateFormat
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

        //Fill form with example Person
//        fillForm(Person.exampleWorker)

        binding.mainBaseOccupationRdg.setOnCheckedChangeListener { _, choiceId ->
            when (choiceId) {
                R.id.rdb_student -> setSpecificFormView(VISIBLE, GONE)
                R.id.rdb_worker -> setSpecificFormView(GONE, VISIBLE)
            }
        }

        binding.mainBaseBirthdateBtn.setOnClickListener {
            openDatePickerDialog()
        }

        binding.okBtn.setOnClickListener {
            Log.d("Person created", createPerson().toString())
        }

        binding.cancelBtn.setOnClickListener {
            emptyForm();
        }
    }

    private fun setSpecificFormView(studentView: Int, workerView: Int) {
        //Set de la vue étudiant
        binding.studentGroup.visibility = studentView

        //Set de la vue employé
        binding.workerGroup.visibility = workerView
    }

    private fun openDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(R.string.main_base_birthdate_title)
            .build()

        datePicker.isCancelable = false
        datePicker.addOnPositiveButtonClickListener {
            calendar.apply { timeInMillis = it }.time
            val formattedDate = DateFormat.getDateInstance(DateFormat.LONG, Locale.FRENCH)
                .format(calendar.time)

            // Mettez à jour votre champ de texte avec la date formatée
            binding.mainBaseBirthdateInput.setText(formattedDate)
        }

        datePicker.show(supportFragmentManager, "DatePicker")
    }

    private fun fillForm(person: Person?) {
        if (person == null) {
            return
        }

        //Fill common values
        binding.mainBaseNameInput.setText(person.name)
        binding.mainBaseFirstnameInput.setText(person.firstName)
        calendar = person.birthDay
        //TODO : binding.mainBaseBirthdateInput.setText(person.birthDay)
        binding.mainBaseNationalitySpinner.setSelection(
            resources.getStringArray(R.array.nationalities).indexOf(person.nationality)
        )
        binding.additionalEmailInput.setText(person.email)
        binding.additionalRemarksInput.setText(person.remark)

        when (person) {
            is Worker -> {
                binding.mainSpecificCompagnyInput.setText(person.company)
                binding.mainSpecificSectorSpinner.setSelection(
                    resources.getStringArray(R.array.sectors).indexOf(person.sector)
                )
                binding.mainSpecificExperienceInput.setText(person.experienceYear)
            }

            is Student -> {
                binding.mainSpecificSchoolInput.setText(person.university)
                binding.mainSpecificGraduationyearInput.setText(person.graduationYear)
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
    }

    private fun createPerson(): Person? {
        if (binding.rdbWorker.isChecked) {
            return Worker(
                binding.mainBaseNameInput.toString(),
                binding.mainBaseFirstnameInput.toString(),
                calendar,
                binding.mainBaseNationalitySpinner.selectedItem.toString(),
                binding.mainSpecificCompagnyInput.toString(),
                binding.mainSpecificSectorSpinner.selectedItem.toString(),
                binding.mainSpecificExperienceInput.text.toString().toInt(),
                binding.additionalEmailInput.toString(),
                binding.additionalRemarksInput.toString()
            )
        } else if (binding.rdbStudent.isChecked) {
            return Student(
                binding.mainBaseNameInput.toString(),
                binding.mainBaseFirstnameInput.toString(),
                calendar,
                binding.mainBaseNationalitySpinner.selectedItem.toString(),
                binding.mainSpecificSchoolInput.toString(),
                binding.mainSpecificGraduationyearInput.text.toString().toInt(),
                binding.additionalEmailInput.toString(),
                binding.additionalRemarksInput.toString()
            )
        }
        return null
    }
}
