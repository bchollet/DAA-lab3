package ch.heigvd.iict.daa_lab3

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import ch.heigvd.iict.daa_lab3.databinding.ActivityPersonFormBinding

/**
 * @author Cochet Yvan, Anthonponrajkumar Pirakasraj, Chollet Bastian
 * @brief Activité d'un formulaire récoltant diverses information sur un personne pouvant
 * être un étudiant ou un employé
 */
class PersonFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonFormBinding

    /**
     * @brief wrapper permettant l'utilisation d'une visibilité en un type
     */
    enum class Visibility(
        val asInt: Int
    ) {
        VISIBLE(View.VISIBLE),
        GONE(View.GONE),
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Inflation de la vue
        binding = ActivityPersonFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainBaseOccupationRdg.setOnCheckedChangeListener { _, choiceId ->
            when (choiceId) {
                R.id.rdb_student -> {
                    setSpecificFormView(Visibility.VISIBLE, Visibility.GONE)
                }

                R.id.rdb_worker -> {
                    setSpecificFormView(Visibility.GONE, Visibility.VISIBLE)
                }
            }
        }

        binding.mainBaseBirthdateInput.setOnTouchListener {_, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {true}
                else -> {false}
            }
        }
    }

    private fun setVisibility(view: View, visibility: Visibility) {
        view.visibility = visibility.asInt
    }

    private fun setSpecificFormView(studentView: Visibility, workerView: Visibility) {
        //Set de la vue étudiant
        setVisibility(binding.mainSpecificSchoolTitle, studentView)
        setVisibility(binding.mainSpecificSchoolInput, studentView)
        setVisibility(binding.mainSpecificGraduationyearTitle, studentView)
        setVisibility(binding.mainSpecificGraduationyearInput, studentView)

        //Set de la vue employé
        setVisibility(binding.mainSpecificCompagnyTitle, workerView)
        setVisibility(binding.mainSpecificCompagnyInput, workerView)
        setVisibility(binding.mainSpecificSectorTitle, workerView)
        setVisibility(binding.mainSpecificSectorSpinner, workerView)
        setVisibility(binding.mainSpecificExperienceTitle, workerView)
        setVisibility(binding.mainSpecificExperienceInput, workerView)
    }
}
