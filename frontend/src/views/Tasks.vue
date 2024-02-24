<template>
  <div class="container">
    <div style="display: flex; justify-content: space-between;">
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
      <span>You do not have any tasks.</span> <span class="add-task-link">Add a new task</span>
    </div>
    <div v-else>
      <div
        v-for="task in tasks"
        :key="task.id"
        class="task-container"
      >
        <input
          class="checkbox"
          type="checkbox"
        >
        <div class="title">
          {{ task.title }}
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
            color="white"
            icon="message"
          />
          <ButtonWithIcon
            color="white"
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
  />
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import CreateTaskModal from '@/components/modal/CreateTaskModal.vue';
  import SubmitButton from '@/components/buttons/SubmitButton.vue';
  import type { TaskDto } from '@/api/controllers/taskController';
  import ButtonWithIcon from '@/components/buttons/ButtonWithIcon.vue';

  const showSaveTaskModal = ref(false);
  const selectedTaskId = ref<number | null>(null);
  const tasks = ref<TaskDto[]>([
    {
      id: 10,
      title: 'Task title lorem ipsum task title dolor',
      dueDate: '01.01.2019'
    },
    {
      id: 11,
      title: 'Water the plants.',
      dueDate: '02.02.2019'
    }
  ]);

  function openSaveTaskModal(taskId: number | null) {
    selectedTaskId.value = taskId;
    showSaveTaskModal.value = true;
  }

  function closeSaveTaskModal() {
    selectedTaskId.value = null;
    showSaveTaskModal.value = false;
  }
</script>

<style lang="scss" scoped>
  @import "@/assets/variables";

  .task-container {
    display: grid;
    grid-template-columns: min-content repeat(5, 1fr);
    grid-template-areas: "checkbox title title title date actions";
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

    .checkbox {
      grid-area: checkbox;
    }

    @media screen and (max-width: 600px) {
      grid-row-gap: 5px;
      grid-template-areas:
        "title title title title title checkbox"
        "date . . . . actions";

      input[type='checkbox'] {
        place-self: end;
      }
    }
  }

  .brand {
    font-weight: bold;
    font-size: 25px;
    margin: 0;
    color: $main-color;
  }

  .container {
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    width: 640px;
    height: 800px;
    padding: 20px;
    background: white;
    box-shadow: $shadow;

    @media screen and (max-width: 700px) {
      height: 100%;
      box-shadow: none;
      width: 100%;
      top: 0;
    }
  }

  input[type='checkbox'] {
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

  .add-task-link {
    color: $main-color;
    text-decoration: underline;
    font-weight: bold;
    cursor: pointer;
  }

  hr {
    display: block;
    height: 1px;
    border: 0;
    border-top: 1px solid #ccc;
    margin: 1em 0;
    padding: 0;
  }

  span {
    font-size: 14px;
  }
</style>
