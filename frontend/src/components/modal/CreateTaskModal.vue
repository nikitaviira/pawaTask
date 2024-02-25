<template>
  <ModalWrapper
    :show="show"
    title="New task"
    @closed="closeModal"
  >
    <InputWrapper
      v-model.trim="taskForm.title"
      :validator="$v.title"
      label="Title"
      placeholder="Title"
      type="input"
    />

    <InputWrapper
      v-model.trim="taskForm.description"
      :validator="$v.description"
      label="Description"
      placeholder="Enter some description..."
      type="textarea"
    />

    <InputWrapper
      v-model.trim="taskForm.dueDate"
      :validator="$v.dueDate"
      label="Set due date"
      placeholder="date"
      type="date"
    />

    <InputWrapper
      v-model.trim="taskForm.priority"
      :validator="$v.priority"
      label="Priority"
      placeholder="Select task priority..."
      type="select"
      :select-options="priorityOptions"
    />

    <InputWrapper
      v-model.trim="taskForm.comments"
      :validator="$v.comments"
      label="Comments"
      placeholder="Enter some comments (optional)..."
      type="textarea"
    />

    <SubmitButton
      style="float: right"
      text="Add a new task"
      @click="submitForm"
    />
  </ModalWrapper>
</template>

<script setup lang="ts">
  import InputWrapper from '@/components/auth/InputWrapper.vue';
  import ModalWrapper from '@/components/modal/ModalWrapper.vue';
  import { required } from '@vuelidate/validators';
  import { ref, watch } from 'vue';
  import taskController, { type SaveTaskDto, TaskPriority } from '@/api/controllers/taskController';
  import useVuelidate from '@vuelidate/core';
  import SubmitButton from '@/components/buttons/SubmitButton.vue';

  const emit = defineEmits<{
    (e: 'closed'): void,
    (e: 'refresh-tasks'): void
  }>();

  const props = defineProps<{
    show: boolean,
    taskId: number | null
  }>();

  watch(() => props.taskId, (taskId) => {
    if (props.show && taskId) {
      loadTask(taskId);
    }
  });

  const rules = {
    title: { required },
    description: { required },
    dueDate: { required },
    priority: { required },
    comments: { }
  };

  const taskForm = ref<SaveTaskDto>({
    id: null,
    title: '',
    description: '',
    dueDate: '',
    priority: null,
    comments: ''
  });

  const priorityOptions: Record<string, TaskPriority> = {
    Low: TaskPriority.LOW,
    Medium: TaskPriority.MEDIUM,
    High: TaskPriority.HIGH,
    Critical: TaskPriority.CRITICAL
  };

  const $v = useVuelidate(rules, taskForm);

  async function submitForm() {
    await $v.value.$validate().then(async(result) => {
      if (result) {
        await taskController.saveTask(taskForm.value);
        emit('refresh-tasks');
        closeModal();
      }
    });
  }

  async function loadTask(taskId: number) {
    const { data } = await taskController.task(taskId);
    taskForm.value = data;
  }

  function closeModal() {
    taskForm.value = {
      id: null,
      title: '',
      description: '',
      dueDate: '',
      priority: null,
      comments: ''
    };

    $v.value.$reset();
    emit('closed');
  }
</script>
