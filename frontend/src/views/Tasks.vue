<template>
  <div class="container">
    <div class="header">
      <p class="brand">
        pawaTask
      </p>
      <SubmitButton
        text="Add a new task"
        @click="openSaveTaskModal(null)"
      />
    </div>
    <hr>
    <div v-if="tasks.length === 0">
      <span>You do not have any tasks.</span>
      <span
        class="add-task-link"
        @click="openSaveTaskModal(null)"
      >
        Add a new task
      </span>
    </div>
    <div
      v-else
      class="tasks-wrapper"
    >
      <div class="tasks-action-bar">
        <ButtonWithIcon
          v-if="selectedForDeleteTaskIds.length > 0"
          icon-color="white"
          icon="trashcan"
          btn-color="red"
          @click="deleteSelection"
        />
        <select v-model="sortOrder">
          <option
            selected
            :value="TaskSortOrder.DEFAULT"
          >
            Sort by
          </option>
          <option :value="TaskSortOrder.PRIORITY_ASC">
            Priority (from lowest)
          </option>
          <option :value="TaskSortOrder.PRIORITY_DESC">
            Priority (from highest)
          </option>
          <option :value="TaskSortOrder.DUE_DATE_ASC">
            Due date (from closest)
          </option>
          <option :value="TaskSortOrder.DUE_DATE_DESC">
            Due date (from furthest)
          </option>
        </select>
      </div>
      <div
        ref="taskContainerRef"
        class="tasks-scroll-wrapper"
      >
        <div
          v-for="task in tasks"
          :key="task.id!"
          class="task-container"
        >
          <input
            v-model="selectedForDeleteTaskIds"
            class="checkbox"
            type="checkbox"
            :value="task.id"
          >
          <div class="title">
            {{ task.title }}
          </div>
          <div class="priority">
            {{ priorityConversion[task.priority!] }}
          </div>
          <div class="date">
            <img
              src="@/assets/icons/calendar.svg"
              alt="calendar-icon"
            >
            {{ task.dueDate }}
          </div>
          <div class="actions">
            <ButtonWithIcon
              icon-color="white"
              icon="message"
              @click="openTaskDetailsModal(task.id!)"
            />
            <ButtonWithIcon
              icon-color="white"
              icon="edit"
              @click="openSaveTaskModal(task.id)"
            />
          </div>
        </div>
      </div>
    </div>
    <CreateTaskModal
      :show="showSaveTaskModal"
      :task-id="selectedTaskId"
      @closed="closeSaveTaskModal"
      @refresh-tasks="loadTasks"
    />

    <TaskDetailsModal
      :show="showTaskDetailsModal"
      :task-id="selectedTaskId"
      @closed="closeTaskDetailsModal"
    />
  </div>
</template>

<script setup lang="ts">
  import { onBeforeMount, ref, watch } from 'vue';
  import CreateTaskModal from '@/components/modal/CreateTaskModal.vue';
  import SubmitButton from '@/components/buttons/SubmitButton.vue';
  import taskController, { priorityConversion, type TaskDto, TaskSortOrder } from '@/api/controllers/taskController';
  import ButtonWithIcon from '@/components/buttons/ButtonWithIcon.vue';
  import TaskDetailsModal from '@/components/modal/TaskDetailsModal.vue';

  const taskContainerRef = ref<HTMLElement | null>(null);
  const showSaveTaskModal = ref(false);
  const showTaskDetailsModal = ref(false);
  const selectedTaskId = ref<number | null>(null);
  const selectedForDeleteTaskIds = ref<number[]>([]);
  const sortOrder = ref<TaskSortOrder>(TaskSortOrder.DEFAULT);
  const tasks = ref<TaskDto[]>([]);

  onBeforeMount(() => {
    loadTasks();
  });

  watch(sortOrder, () => {
    loadTasks();
  });

  function openSaveTaskModal(taskId: number | null) {
    selectedTaskId.value = taskId;
    showSaveTaskModal.value = true;
  }

  function closeSaveTaskModal() {
    selectedTaskId.value = null;
    showSaveTaskModal.value = false;
  }

  function openTaskDetailsModal(taskId: number) {
    selectedTaskId.value = taskId;
    showTaskDetailsModal.value = true;
  }

  function closeTaskDetailsModal() {
    selectedTaskId.value = null;
    showTaskDetailsModal.value = false;
  }

  async function loadTasks() {
    const { data } = await taskController.tasks(sortOrder.value);
    tasks.value = data;
    if (taskContainerRef.value) {
      taskContainerRef.value.scrollTop = 0;
    }
  }

  async function deleteSelection() {
    await taskController.deleteTasks(selectedForDeleteTaskIds.value);
    tasks.value = tasks.value.filter((t) => !selectedForDeleteTaskIds.value.includes(t.id!));
    selectedForDeleteTaskIds.value = [];
  }
</script>

<style lang="scss" scoped>
@import "@/assets/variables";

.container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  width: 750px;
  height: 800px;
  padding: 20px;
  background: white;
  box-shadow: $shadow;

  @media screen and (max-width: 850px) {
    height: 100%;
    box-shadow: none;
    width: 100%;
    top: 0;
  }

  .header {
    display: flex;
    justify-content: space-between;

    .brand {
      font-weight: bold;
      font-size: 25px;
      margin: 0;
      color: $main-color;
    }
  }

  hr {
    display: block;
    height: 1px;
    border: 0;
    border-top: 1px solid $main-color;
    margin-top: 10px;
    margin-bottom: 10px;
    padding: 0;
  }

  span {
    font-size: 14px;
    &.add-task-link {
      color: $main-color;
      text-decoration: underline;
      font-weight: bold;
      cursor: pointer;
    }
  }

  .tasks-wrapper {
    height: calc(100% - 105px);

    .tasks-action-bar {
      padding: 0 10px 10px;
      display: flex;
      gap: 15px;
      height: 50px;
      align-items: self-end;

      select {
        background: $input-color;
        padding: 10px 15px;
        border-radius: 10px;
        font-size: 14px;
        margin-left: auto;
        user-select: none;
        cursor: pointer;
      }

      @media screen and (max-width: 650px) {
        flex-direction: row-reverse;
        select {
          margin-right: auto;
          margin-left: 0;
        }
      }
    }

    .tasks-scroll-wrapper {
      overflow-y: scroll;
      scrollbar-width: none;
      height: 100%;

      .task-container {
        display: grid;
        grid-template-columns: min-content repeat(6, 1fr);
        grid-template-areas: "checkbox title title title priority date actions";
        grid-template-rows: auto;
        align-items: center;
        column-gap: 10px;
        font-size: 14px;
        padding: 10px;
        border-bottom: 1px solid #ccc;

        .actions {
          grid-area: actions;
          display: flex;
          gap: 10px;
          justify-content: end;
        }

        .date {
          display: flex;
          justify-content: center;
          grid-area: date;

          img {
            margin-right: 5px;
          }
        }

        .title {
          grid-area: title
        }

        .priority {
          grid-area: priority;
        }

        .checkbox {
          grid-area: checkbox;
          width: 15px;
          height: 15px;
          border: 1px solid black;
          border-radius: 3px;
          cursor: pointer;
          position: relative;

          &:checked {
            background: $main-color;
          }

          &:checked:before {
            color: white;
            position: absolute;
            font-size: 20px;
            font-weight: bold;
            bottom: -6px;
            left: -1px;
            content: "✓";
          }
        }

        @media screen and (max-width: 650px) {
          grid-row-gap: 5px;
          grid-template-areas:
        "title title title title title title checkbox"
        "date . priority . . . actions";

          input[type='checkbox'] {
            place-self: end;
            align-self: self-start;
          }
        }
      }
    }
  }
}
</style>
