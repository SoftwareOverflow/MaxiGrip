package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.workout_set_creator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.softwareoverflow.maxigriphangboardtrainer.repository.IWorkoutRepository
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.WorkoutSetDTO
import com.softwareoverflow.maxigriphangboardtrainer.ui.utils.SortOrder
import kotlinx.coroutines.launch

class WorkoutSetCreatorViewModel(
    workoutSetToEdit: WorkoutSetDTO,
    private val repo: IWorkoutRepository
) :
    ViewModel() {

    val _workoutSet = MutableLiveData(workoutSetToEdit)
    val workoutSet: LiveData<WorkoutSetDTO> get() = _workoutSet

    val sortOrder = MutableLiveData(SortOrder.DESC)
    private val _searchFilter: MutableLiveData<String> = MutableLiveData("")

    private val _allGripTypes = repo.getAllGripTypes()
    private val _allGripTypesOrdered = MediatorLiveData<List<GripTypeDTO>>()
    val allGripTypes: LiveData<List<GripTypeDTO>>
        get() = _allGripTypesOrdered

    private val _selectedGripTypeId = MutableLiveData<Long?>(workoutSet.value?.gripTypeDTO?.id)
    val selectedGripTypeId: LiveData<Long?> get() = _selectedGripTypeId

    private val _unableToDeleteGripType = MutableLiveData("")
    val unableToDeleteGripType: LiveData<String>
        get() = _unableToDeleteGripType

    init {
        _allGripTypesOrdered.addSource(_allGripTypes) { GripTypes ->
            GripTypes?.let {
                _allGripTypesOrdered.value = getGripTypesToDisplay(GripTypes)
            }
        }

        _allGripTypesOrdered.addSource(sortOrder) {
            _allGripTypesOrdered.value =
                getGripTypesToDisplay(_allGripTypes.value ?: arrayListOf())
        }

        _allGripTypesOrdered.addSource(_searchFilter) {
            _allGripTypesOrdered.value =
                getGripTypesToDisplay(_allGripTypes.value ?: arrayListOf())
        }
    }

    fun unableToDeleteGripTypeWarningShown() {
        _unableToDeleteGripType.value = ""
    }

    private fun getGripTypesToDisplay(_GripTypes: List<GripTypeDTO>): List<GripTypeDTO> {
        var GripTypes = _GripTypes

        // Filter
        val filter = _searchFilter.value
        if (!filter.isNullOrBlank())
            GripTypes = GripTypes.filter { it.name!!.contains(filter, ignoreCase = true) }

        // Sort
        var sortedList = GripTypes.sortedBy { it.name }
        if (sortOrder.value == SortOrder.ASC)
            sortedList = sortedList.reversed()

        return sortedList
    }

    fun changeSortOrder() {
        sortOrder.value =
            if (sortOrder.value == SortOrder.ASC)
                SortOrder.DESC
            else SortOrder.ASC
    }

    fun setFilterText(filter: String) {
        _searchFilter.value = filter
    }

    fun setChosenGripTypeId(id: Long?) {

        val gripType = _allGripTypes.value!!.firstOrNull { it.id == id }
        workoutSet.value!!.gripTypeDTO = gripType ?: GripTypeDTO()

        _selectedGripTypeId.value = id
    }

    fun deleteGripTypeById(id: Long, currentWorkoutSets: List<WorkoutSetDTO>) {
        setChosenGripTypeId(null)

        viewModelScope.launch {
            val gripType = allGripTypes.value!!.first { it.id == id }

            val currentGripTypes = currentWorkoutSets.map { it.gripTypeDTO }
            if (currentGripTypes.contains(gripType)) {
                _unableToDeleteGripType.value =
                    "Unable to delete Exercise Type. It's used in this workout!"
                return@launch
            }

            try {
                repo.deleteGripType(gripType)
            } catch (ex: IllegalStateException) {
                _unableToDeleteGripType.value = ex.message
            }
        }
    }
}