<template>
  <ModalWrapper
    :show="show"
    title="New task"
    @closed="emit('closed')"
  >
    <div style="margin-top: 20px">
      <InputWrapper
        v-model.trim="createTaskForm.title"
        :validator="$v.title"
        label="Title"
        placeholder="Title"
        type="input"
      />

      <InputWrapper
        v-model.trim="createTaskForm.description"
        :validator="$v.description"
        label="Description"
        placeholder="Enter some description..."
        type="textarea"
      />

      <InputWrapper
        v-model.trim="createTaskForm.dueDate"
        :validator="$v.dueDate"
        label="Set due date"
        placeholder="date"
        type="date"
      />

      <InputWrapper
        v-model.trim="createTaskForm.priority"
        :validator="$v.priority"
        label="Priority"
        placeholder="Select task priority..."
        type="select"
        :select-options="priorityOptions"
      />

      <InputWrapper
        v-model.trim="createTaskForm.comments"
        :validator="$v.description"
        label="Comments"
        placeholder="Enter some comments (optional)..."
        type="textarea"
      />

      <SubmitButton
        style="float: right"
        text="Add a new task"
        @submit="submitForm"
      />
    </div>
  </ModalWrapper>
</template>

<script setup lang="ts">
  import InputWrapper from '@/components/auth/InputWrapper.vue';
  import ModalWrapper from '@/components/modal/ModalWrapper.vue';
  import { helpers, required } from '@vuelidate/validators';
  import { ref } from 'vue';
  import { type CreateTaskRequest, TaskPriority } from '@/api/controllers/taskController';
  import useVuelidate from '@vuelidate/core';
  import SubmitButton from '@/components/buttons/SubmitButton.vue';

  const emit = defineEmits<{ (e: 'closed'): void }>();
  defineProps<{ show: boolean }>();

  const dateValidation = helpers.withMessage('Date has to be in future', (value: string) => {
    const currentDate = new Date().setHours(0, 0, 0, 0);
    const inputDate = new Date(value).setHours(0, 0, 0, 0);
    return inputDate >= currentDate;
  });

  const rules = {
    title: { required },
    description: { required },
    dueDate: { required, dateValidation },
    priority: { required },
    comments: { }
  };

  const createTaskForm = ref<CreateTaskRequest>({
    title: '',
    description: '',
    dueDate: '',
    priority: undefined,
    comments: ''
  });

  const priorityOptions: Record<string, TaskPriority> = {
    Low: TaskPriority.LOW,
    Medium: TaskPriority.MEDIUM,
    High: TaskPriority.HIGH,
    Critical: TaskPriority.CRITICAL
  };

  const $v = useVuelidate(rules, createTaskForm);

  async function submitForm() {
    await $v.value.$validate().then(async(result) => {
      if (result) {
        console.log('submited');
      }
    });
  }
</script>
