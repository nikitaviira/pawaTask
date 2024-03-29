<template>
  <ModalWrapper
    :show="show"
    :title="taskId ? 'Edit task' : 'Add task'"
    :loading="loading"
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

    <SubmitButton
      style="float: right"
      :text="taskId ? 'Submit changes' : 'Add a new task'"
      @click="submitForm"
    />
  </ModalWrapper>
</template>

<script setup lang="ts">
  import InputWrapper from '@/components/generic/InputWrapper.vue';
  import ModalWrapper from '@/components/modal/ModalWrapper.vue';
  import { maxLength, required } from '@vuelidate/validators';
  import { ref, watch } from 'vue';
  import taskController, { type TaskDto, TaskPriority } from '@/api/controllers/taskController';
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

  watch(() => props.show, (show) => {
    if (show && props.taskId) {
      loadTask(props.taskId);
    }
  });

  const loading = ref(false);
  const taskForm = ref<TaskDto>({
    id: null,
    title: '',
    description: '',
    dueDate: '',
    priority: null
  });

  const priorityOptions: Record<string, TaskPriority> = {
    Low: TaskPriority.LOW,
    Medium: TaskPriority.MEDIUM,
    High: TaskPriority.HIGH,
    Critical: TaskPriority.CRITICAL
  };

  const $v = useVuelidate({
    title: {
      required,
      maxLength: maxLength(150)
    },
    description: {
      required,
      maxLength: maxLength(1000)
    },
    dueDate: { required },
    priority: { required }
  }, taskForm);

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
    loading.value = true;
    const { data } = await taskController.task(taskId);
    taskForm.value = data;
    loading.value = false;
  }

  function closeModal() {
    taskForm.value = {
      id: null,
      title: '',
      description: '',
      dueDate: '',
      priority: null
    };

    $v.value.$reset();
    emit('closed');
  }
</script>
