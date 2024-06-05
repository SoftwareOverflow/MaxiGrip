package com.softwareoverflow.maxigriphangboardtrainer.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.softwareoverflow.maxigriphangboardtrainer.data.entity.GripTypeEntity

@Dao
interface GripTypeDao :
    BaseDao<GripTypeEntity> {

    @Query("SELECT * FROM GripType order by [name] asc")
    fun getAllGripTypes(): LiveData<List<GripTypeEntity>>

    @Query("SELECT * FROM GripType WHERE [gripTypeId] = :gripTypeId")
    fun getGripTypeById(gripTypeId: Long) : LiveData<GripTypeEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: GripTypeEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: GripTypeEntity)

    @Transaction
    override suspend fun createOrUpdate(obj: GripTypeEntity): Long {
        var insertedId = insert(obj)
        if (insertedId == -1L) {
            update(obj)
            insertedId = obj.gripTypeId
        }

        return insertedId
    }
}