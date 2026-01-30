package com.scto.codelikebastimove.core.actions.impl.executor

import com.scto.codelikebastimove.core.actions.api.action.*
import com.scto.codelikebastimove.core.actions.api.event.ActionEventEmitter
import com.scto.codelikebastimove.core.actions.api.registry.ActionRegistry
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface ActionExecutor {
  val runningActions: StateFlow<Set<String>>

  suspend fun execute(actionId: String, context: ActionContext = ActionContext.EMPTY): ActionResult

  suspend fun executeAction(
    action: Action,
    context: ActionContext = ActionContext.EMPTY,
  ): ActionResult

  fun cancel(actionId: String): Boolean

  fun cancelAll()

  fun isRunning(actionId: String): Boolean
}

class DefaultActionExecutor(
  private val registry: ActionRegistry,
  private val eventEmitter: ActionEventEmitter,
) : ActionExecutor {

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

  private val _runningActions = MutableStateFlow<Set<String>>(emptySet())
  override val runningActions: StateFlow<Set<String>> = _runningActions.asStateFlow()

  private val runningJobs = ConcurrentHashMap<String, Job>()

  override suspend fun execute(actionId: String, context: ActionContext): ActionResult {
    val action =
      registry.getAction(actionId) ?: return ActionResult.Failure("Action not found: $actionId")

    return executeAction(action, context)
  }

  override suspend fun executeAction(action: Action, context: ActionContext): ActionResult {
    val actionId = action.id

    if (!action.canExecute(context)) {
      return ActionResult.Failure("Action cannot be executed in current context")
    }

    if (isRunning(actionId)) {
      return ActionResult.Failure("Action is already running")
    }

    val startTime = System.currentTimeMillis()

    _runningActions.value = _runningActions.value + actionId
    eventEmitter.emitExecuting(actionId, context)

    return try {
      val job =
        scope.launch {
          // Action execution will happen in the coroutine
        }
      runningJobs[actionId] = job

      val result = withContext(Dispatchers.Default) { action.execute(context) }

      val duration = System.currentTimeMillis() - startTime
      eventEmitter.emitExecuted(actionId, result, duration)

      result
    } catch (e: CancellationException) {
      eventEmitter.emitCancelled(actionId)
      ActionResult.Cancelled
    } catch (e: Exception) {
      val errorMessage = e.message ?: "Unknown error"
      eventEmitter.emitFailed(actionId, errorMessage, e)
      ActionResult.Failure(errorMessage, e)
    } finally {
      _runningActions.value = _runningActions.value - actionId
      runningJobs.remove(actionId)
    }
  }

  override fun cancel(actionId: String): Boolean {
    val job = runningJobs[actionId] ?: return false
    job.cancel()
    return true
  }

  override fun cancelAll() {
    runningJobs.values.forEach { it.cancel() }
    runningJobs.clear()
    _runningActions.value = emptySet()
  }

  override fun isRunning(actionId: String): Boolean {
    return actionId in _runningActions.value
  }
}

class ActionInvoker(private val executor: ActionExecutor, private val registry: ActionRegistry) {
  suspend fun invoke(actionId: String, args: Map<String, Any> = emptyMap()): ActionResult {
    val context = ActionContext(customData = args)
    return executor.execute(actionId, context)
  }

  suspend fun invokeWithContext(
    actionId: String,
    filePath: String? = null,
    selectedText: String? = null,
    cursorLine: Int = 0,
    cursorColumn: Int = 0,
    args: Map<String, Any> = emptyMap(),
  ): ActionResult {
    val context =
      ActionContext(
        filePath = filePath,
        fileName = filePath?.substringAfterLast('/'),
        fileExtension = filePath?.substringAfterLast('.'),
        selectedText = selectedText,
        cursorLine = cursorLine,
        cursorColumn = cursorColumn,
        customData = args,
      )
    return executor.execute(actionId, context)
  }

  fun getAvailableActions(context: ActionContext): List<Action> {
    return registry.getActionsForContext(context)
  }
}
