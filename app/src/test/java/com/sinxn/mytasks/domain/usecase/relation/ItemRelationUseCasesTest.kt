package com.sinxn.mytasks.domain.usecase.relation

import com.sinxn.mytasks.domain.models.ItemRelation
import com.sinxn.mytasks.domain.models.RelationItemType
import com.sinxn.mytasks.domain.repository.ItemRelationRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ItemRelationUseCasesTest {

    @Mock
    private lateinit var repository: ItemRelationRepository

    private lateinit var useCases: ItemRelationUseCases

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        useCases = ItemRelationUseCases(
            addRelation = AddRelation(repository),
            removeRelation = RemoveRelation(repository),
            getParent = GetParent(repository),
            getChildren = GetChildren(repository),
            removeRelationsForItem = RemoveRelationsForItem(repository)
        )
    }

    @Test
    fun `AddRelation calls repository addRelation`() = runBlocking {
        val relation = ItemRelation(1, RelationItemType.TASK, 2, RelationItemType.NOTE)
        useCases.addRelation(relation)
        verify(repository).addRelation(relation)
    }

    @Test
    fun `RemoveRelation calls repository removeRelation`() = runBlocking {
        val relation = ItemRelation(1, RelationItemType.TASK, 2, RelationItemType.NOTE)
        useCases.removeRelation(relation)
        verify(repository).removeRelation(relation)
    }

    @Test
    fun `GetParent calls repository getParent`() = runBlocking {
        val childId = 2L
        val childType = RelationItemType.NOTE
        `when`(repository.getParent(childId, childType)).thenReturn(flowOf(null))
        useCases.getParent(childId, childType)
        verify(repository).getParent(childId, childType)
    }

    @Test
    fun `GetChildren calls repository getChildren`() = runBlocking {
        val parentId = 1L
        val parentType = RelationItemType.TASK
        `when`(repository.getChildren(parentId, parentType)).thenReturn(flowOf(emptyList()))
        useCases.getChildren(parentId, parentType)
        verify(repository).getChildren(parentId, parentType)
    }
    
    @Test
    fun `RemoveRelationsForItem calls repository removeRelationByParent and removeRelationByChild`() = runBlocking {
        val itemId = 1L
        val itemType = RelationItemType.TASK
        useCases.removeRelationsForItem(itemId, itemType)
        verify(repository).removeRelationByParent(itemId, itemType)
        verify(repository).removeRelationByChild(itemId, itemType)
    }
}
