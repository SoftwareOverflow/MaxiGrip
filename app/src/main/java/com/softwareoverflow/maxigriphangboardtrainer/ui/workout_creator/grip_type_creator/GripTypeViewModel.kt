package com.softwareoverflow.maxigriphangboardtrainer.ui.workout_creator.grip_type_creator

import androidx.lifecycle.ViewModel
import com.softwareoverflow.maxigriphangboardtrainer.repository.IWorkoutRepository
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.FingerType
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.GripTypeDTO
import com.softwareoverflow.maxigriphangboardtrainer.repository.dto.HandType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

class GripTypeViewModel(
    private val repository: IWorkoutRepository, gripTypeOriginal: GripTypeDTO
) : ViewModel() {

    private val _gripType = MutableStateFlow(gripTypeOriginal)
    val gripType: StateFlow<GripTypeDTO> get() = _gripType

    fun updateHandGrip(hand: HandType, finger: FingerType, enabled: Boolean) {
        val handToEdit = when(hand) {
            HandType.LEFT -> _gripType.value.leftHand.copy()
            HandType.RIGHT -> _gripType.value.rightHand.copy()
        }

        when (finger) {
            FingerType.THUMB -> handToEdit.thumb = enabled
            FingerType.INDEX -> handToEdit.index = enabled
            FingerType.MIDDLE -> handToEdit.middle = enabled
            FingerType.RING -> handToEdit.ring = enabled
            FingerType.LITTLE -> handToEdit.little = enabled
        }

        when (hand) {
            HandType.LEFT -> _gripType.value = _gripType.value.copy(leftHand = handToEdit)
            HandType.RIGHT -> _gripType.value = _gripType.value.copy(rightHand = handToEdit)
        }
    }

    fun updateName(name: String) {
        _gripType.value = _gripType.value.copy(name = name)
    }

    fun saveGripType(): Long {
        return runBlocking {
            return@runBlocking repository.createOrUpdateGripType(_gripType.value)
        }
    }
}