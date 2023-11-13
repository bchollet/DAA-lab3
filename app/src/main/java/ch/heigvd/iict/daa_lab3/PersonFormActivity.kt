package ch.heigvd.iict.daa_lab3

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
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

        //Listener sur RadioGroup
        binding.mainBaseOccupationRdg.setOnCheckedChangeListener { _, choiceId ->
            when (choiceId) {
                R.id.rdb_student -> setSpecificFormView(VISIBLE, GONE)
                R.id.rdb_worker -> setSpecificFormView(GONE, VISIBLE)
                else -> setSpecificFormView(GONE, GONE)
            }
        }

        //Listener sur click bouton gâteau
        binding.mainBaseBirthdateBtn.setOnClickListener {
            openDatePickerDialog()
        }

        //Listener sur click input Birthdate
        binding.mainBaseBirthdateInput.setOnClickListener {
            openDatePickerDialog()
        }

        //Listener sur click OK
        binding.okBtn.setOnClickListener {
            Log.d("Person created", createPerson().toString())
        }

        //Listener sur click CANCEL
        binding.cancelBtn.setOnClickListener {
            emptyForm()
        }

        // Permet de valider le form au clavier sur l'input email
        binding.additionalEmailInput.setOnEditorActionListener { _, _, _ ->
            Log.d("Person created", createPerson().toString())
            true
        }

        //Pré-rempli le form avec les valeurs d'une personne
        //fillForm(Person.exampleWorker)
    }


    /**
     * @brief Set la visibilité de la section Student / Worker
     */
    private fun setSpecificFormView(studentView: Int, workerView: Int) {
        //Set de la vue étudiant
        binding.studentGroup.visibility = studentView

        //Set de la vue employé
        binding.workerGroup.visibility = workerView
    }

    /**
     * @brief Ouvre le datepicker dialog de material. Set également une range de date
     * possible
     */
    private fun openDatePickerDialog() {
        val today = Calendar.getInstance()
        val minDate = Calendar.getInstance()
        minDate.add(Calendar.YEAR, -110)
        val constraints = CalendarConstraints.Builder()
            .setStart(minDate.timeInMillis)
            .setEnd(today.timeInMillis)
            .build()
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

    /**
     * @brief rempli le formulaire
     */
    private fun fillDate(time: Long) {
        calendar.apply { timeInMillis = time }.time
        // formate la date pour l'afficher dans le champ de texte en utilisant le format du sytème
        val formattedDate = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
            .format(calendar.time)

        // Met à jour le champ de texte avec la date formatée
        binding.mainBaseBirthdateInput.setText(formattedDate)
    }

    /**
     * @brief Pré-rempli les champs du formulaire selon les propriété de la personne
     * passée en paramètre
     */
    private fun fillForm(person: Person?) {
        if (person == null) {
            return
        }

        //Remplissage de la partie commune
        binding.mainBaseNameInput.setText(person.name)
        binding.mainBaseFirstnameInput.setText(person.firstName)
        fillDate(person.birthDay.timeInMillis)
        binding.mainBaseNationalitySpinner.setSelection(
            resources.getStringArray(R.array.nationalities).indexOf(person.nationality)
        )
        binding.additionalEmailInput.setText(person.email)
        binding.additionalRemarksInput.setText(person.remark)

        //Remplissage de la partie spécifique selon le type de personne
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

    /**
     * @brief Vide le formulaire en settant une valeur par défaut à chaque type d'input
     */
    private fun emptyForm() {
        for (view: View in binding.root.children) {
            when (view) {
                is EditText -> view.setText("")
                is Spinner -> view.setSelection(0)
                is RadioGroup -> view.clearCheck()
                else -> continue
            }
        }
    }

    /**
     * @brief vérifie que les champs du formulaire soient tous remplis. S'adapte selon l'état du
     * formulaire (Sutdent ou Worker)
     */
    private fun isFormValid(): Boolean {
        if (binding.mainBaseNationalitySpinner.selectedItemPosition <= 0 && listOf(
                binding.mainBaseNameInput.text,
                binding.mainBaseFirstnameInput.text,
                binding.additionalEmailInput.text,
                binding.additionalRemarksInput.text
            ).any { it.isNullOrBlank() }
        ) return false

        when (binding.mainBaseOccupationRdg.checkedRadioButtonId) {
            binding.rdbWorker.id -> {
                if (binding.mainSpecificCompagnyInput.text.isNullOrBlank() ||
                    binding.mainSpecificSectorSpinner.selectedItemPosition <= 0 ||
                    binding.mainSpecificExperienceInput.text.isNullOrBlank()
                ) return false
            }

            binding.rdbStudent.id -> {
                if (binding.mainSpecificSchoolInput.text.isNullOrBlank() ||
                    binding.mainSpecificGraduationyearInput.text.isNullOrBlank()
                ) return false
            }

            else -> return false
        }

        return true
    }

    /**
     * @brief Prends les valeurs des champs en communs puis les passent à la création d'un student
     * ou d'un worker selon le radiobutton sélectionné
     */
    private fun createPerson(): Person? {
        if (!isFormValid()) return null

        when (binding.mainBaseOccupationRdg.checkedRadioButtonId) {
            binding.rdbWorker.id -> return createWorker(
                binding.mainBaseNameInput.text.toString(),
                binding.mainBaseFirstnameInput.text.toString(),
                binding.mainBaseNationalitySpinner.selectedItem.toString(),
                binding.additionalEmailInput.text.toString(),
                binding.additionalRemarksInput.text.toString()
            )

            binding.rdbStudent.id -> return createStudent(
                binding.mainBaseNameInput.text.toString(),
                binding.mainBaseFirstnameInput.text.toString(),
                binding.mainBaseNationalitySpinner.selectedItem.toString(),
                binding.additionalEmailInput.text.toString(),
                binding.additionalRemarksInput.text.toString()
            )

            else -> return null
        }
    }

    private fun createWorker(
        name: String,
        firstName: String,
        nationality: String,
        email: String,
        remark: String
    ): Worker {
        return Worker(
            name,
            firstName,
            calendar,
            nationality,
            binding.mainSpecificCompagnyInput.text.toString(),
            binding.mainSpecificSectorSpinner.selectedItem.toString(),
            binding.mainSpecificExperienceInput.text.toString().toInt(),
            email,
            remark
        )
    }

    private fun createStudent(
        name: String,
        firstName: String,
        nationality: String,
        email: String,
        remark: String
    ): Student {
        return Student(
            name,
            firstName,
            calendar,
            nationality,
            binding.mainSpecificSchoolInput.text.toString(),
            binding.mainSpecificGraduationyearInput.text.toString().toInt(),
            email,
            remark
        )
    }
}
