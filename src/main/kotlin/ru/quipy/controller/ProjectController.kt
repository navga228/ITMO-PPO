package ru.quipy.controller

import org.springframework.web.bind.annotation.*
import ru.quipy.api.ProjectAggregate
import ru.quipy.api.ProjectCreatedEvent
import ru.quipy.api.TaskCreatedEvent
import ru.quipy.core.EventSourcingService
import ru.quipy.dto.CreateProjectRequest
import ru.quipy.dto.CreateTaskRequest
import ru.quipy.logic.ProjectAggregateState
import ru.quipy.logic.addTask
import ru.quipy.logic.create
import java.util.*

@RestController
@RequestMapping("/projects")
class ProjectController(
    val projectEsService: EventSourcingService<UUID, ProjectAggregate, ProjectAggregateState>
) {

    @PostMapping
    fun createProject(@RequestBody createProjectRequest: CreateProjectRequest) : ProjectCreatedEvent {

        return projectEsService.create {
            it.create(UUID.randomUUID(), createProjectRequest.projectTitle, createProjectRequest.creator)
        }
    }

    @GetMapping("/{projectId}")
    fun getAccount(@PathVariable projectId: UUID) : ProjectAggregateState? {
        return projectEsService.getState(projectId)
    }

    @PostMapping("/{projectId}/tasks")
    fun createTask(@PathVariable projectId: UUID, @RequestBody createTaskRequest: CreateTaskRequest) : TaskCreatedEvent {
        return projectEsService.update(projectId) {
            it.addTask(createTaskRequest.taskName)
        }
    }
}