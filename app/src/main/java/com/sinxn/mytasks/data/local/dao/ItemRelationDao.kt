package com.sinxn.mytasks.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sinxn.mytasks.data.local.entities.ItemRelation
import com.sinxn.mytasks.domain.models.RelationItemType
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemRelationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(relation: ItemRelation)

    @Delete
    suspend fun deleteRelation(relation: ItemRelation)

    @Query("DELETE FROM item_relations WHERE childId = :childId AND childType = :childType")
    suspend fun deleteRelationByChild(childId: Long, childType: RelationItemType)

    @Query("DELETE FROM item_relations WHERE parentId = :parentId AND parentType = :parentType")
    suspend fun deleteRelationByParent(parentId: Long, parentType: RelationItemType)

    @Query("SELECT * FROM item_relations WHERE childId = :childId AND childType = :childType LIMIT 1")
    fun getParent(childId: Long, childType: RelationItemType): Flow<ItemRelation?>

    @Query("SELECT * FROM item_relations WHERE parentId = :parentId AND parentType = :parentType")
    fun getChildren(parentId: Long, parentType: RelationItemType): Flow<List<ItemRelation>>
}
