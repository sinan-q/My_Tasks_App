package com.sinxn.mytasks.data.repository

import com.sinxn.mytasks.data.local.dao.ItemRelationDao
import com.sinxn.mytasks.data.local.entities.ItemRelation
import com.sinxn.mytasks.domain.models.RelationItemType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class ItemRelationRepositoryImplTest {

    @Mock
    private lateinit var dao: ItemRelationDao

    private lateinit var repository: ItemRelationRepositoryImpl

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ItemRelationRepositoryImpl(dao)
    }

    @Test
    fun `addRelation calls dao insert`() = runBlocking {
        val relation = com.sinxn.mytasks.domain.models.ItemRelation(
            parentId = 1L,
            parentType = RelationItemType.TASK,
            childId = 2L,
            childType = RelationItemType.NOTE
        )
        val entity = ItemRelation(
            parentId = 1L,
            parentType = RelationItemType.TASK,
            childId = 2L,
            childType = RelationItemType.NOTE
        )

        repository.addRelation(relation)

        verify(dao).insert(entity)
    }

    @Test
    fun `removeRelation calls dao delete`() = runBlocking {
        val relation = com.sinxn.mytasks.domain.models.ItemRelation(
            parentId = 1L,
            parentType = RelationItemType.TASK,
            childId = 2L,
            childType = RelationItemType.NOTE
        )
        val entity = ItemRelation(
            parentId = 1L,
            parentType = RelationItemType.TASK,
            childId = 2L,
            childType = RelationItemType.NOTE
        )

        repository.removeRelation(relation)

        verify(dao).delete(entity)
    }

    @Test
    fun `getParent returns mapped relation`() = runBlocking {
        val childId = 2L
        val childType = RelationItemType.NOTE
        val entity = ItemRelation(
            parentId = 1L,
            parentType = RelationItemType.TASK,
            childId = childId,
            childType = childType
        )
        `when`(dao.getParent(childId, childType)).thenReturn(flowOf(entity))

        val result = repository.getParent(childId, childType).first()

        assertEquals(1L, result?.parentId)
        assertEquals(RelationItemType.TASK, result?.parentType)
    }

    @Test
    fun `getChildren returns mapped relations`() = runBlocking {
        val parentId = 1L
        val parentType = RelationItemType.TASK
        val entity1 = ItemRelation(
            parentId = parentId,
            parentType = parentType,
            childId = 2L,
            childType = RelationItemType.NOTE
        )
        val entity2 = ItemRelation(
            parentId = parentId,
            parentType = parentType,
            childId = 3L,
            childType = RelationItemType.EVENT
        )
        `when`(dao.getChildren(parentId, parentType)).thenReturn(flowOf(listOf(entity1, entity2)))

        val result = repository.getChildren(parentId, parentType).first()

        assertEquals(2, result.size)
        assertEquals(2L, result[0].childId)
        assertEquals(RelationItemType.NOTE, result[0].childType)
        assertEquals(3L, result[1].childId)
        assertEquals(RelationItemType.EVENT, result[1].childType)
    }
}
