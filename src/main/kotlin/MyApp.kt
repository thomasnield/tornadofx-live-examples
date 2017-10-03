import javafx.beans.property.ReadOnlyLongWrapper
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MyApp: WorkspaceApp(PatientsList::class)

object SavePatientEvent : FXEvent()
object DeleteSelectedPatient : FXEvent()

class PatientsList : View("Patients") {
    val ctrl: PatientsController by inject()
    val patients = FXCollections.observableArrayList<Patient>()

    override val root = borderpane {
        center = tableview(patients) {
            column("ID", Patient::patientId)
            column("FIRST NAME", Patient::firstNameProperty).makeEditable()
            column("LAST NAME", Patient::lastNameProperty).makeEditable()
            column("BIRTHDAY", Patient::birthdayProperty).makeEditable()
            column("AGE", Patient::ageBinding)
            column("WBCC", Patient::whiteBloodCellCountProperty).makeEditable()

            isEditable = true
            enableDirtyTracking()

            subscribe<SavePatientEvent> {
                editModel.commit()
            }

            subscribe<DeleteSelectedPatient> {
                patients.remove(selectedItem)
            }

            deletableWhen {
                selectionModel.selectedItemProperty().isNotNull
            }
        }
    }

    init {
        onRefresh()
    }

    override fun onSave() = fire(SavePatientEvent)

    override fun onRefresh() {
        patients.asyncItems {
            ctrl.loadPatients()
        }
    }

    override fun onDelete() {
        fire(DeleteSelectedPatient)
    }
}

class Patient(
        val patientId: Int,
        firstName: String,
        lastName: String,
        birthday: LocalDate,
        whiteBloodCellCount: Int
) {

    val firstNameProperty = SimpleStringProperty(firstName)
    var firstName by firstNameProperty

    val lastNameProperty = SimpleStringProperty(lastName)
    var lastName by lastNameProperty

    val birthdayProperty = SimpleObjectProperty(birthday)
    var birthday by birthdayProperty

    val whiteBloodCellCountProperty = SimpleObjectProperty(whiteBloodCellCount)
    var whiteBloodCellCount by whiteBloodCellCountProperty

    val ageBinding = birthdayProperty.select { ReadOnlyLongWrapper(ChronoUnit.YEARS.between(it, LocalDate.now())) }
    val age by ageBinding
}


class PatientsController : Controller() {
    fun loadPatients() = listOf(
            Patient(1, "John", "Simone", LocalDate.of(1989, 1, 7), 4500),
            Patient(2, "Sarah", "Marley", LocalDate.of(1970, 2, 5), 6700),
            Patient(3, "Jessica", "Arnold", LocalDate.of(1980, 3, 9), 3400),
            Patient(4, "Sam", "Beasley", LocalDate.of(1981, 4, 17), 8800),
            Patient(5, "Dan", "Forney", LocalDate.of(1985, 9, 13), 5400),
            Patient(6, "Lauren", "Michaels", LocalDate.of(1975, 8, 21), 5000),
            Patient(7, "Michael", "Erlich", LocalDate.of(1985, 12, 17), 4100),
            Patient(8, "Jason", "Miles", LocalDate.of(1991, 11, 1), 3900),
            Patient(9, "Rebekah", "Earley", LocalDate.of(1985, 2, 18), 4600),
            Patient(10, "James", "Larson", LocalDate.of(1974, 4, 10), 5100),
            Patient(11, "Dan", "Ulrech", LocalDate.of(1991, 7, 11), 6000),
            Patient(12, "Heather", "Eisner", LocalDate.of(1994, 3, 6), 6000),
            Patient(13, "Jasper", "Martin", LocalDate.of(1971, 7, 1), 6000)
    ).observable()
}