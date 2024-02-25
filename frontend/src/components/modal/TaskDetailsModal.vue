<template>
  <ModalWrapper
    :show="show"
    :title="taskDetails.title"
    @closed="closeModal"
  >
    <div class="description">
      {{ taskDetails.description }}
    </div>
    <div class="details">
      <p><span>Due date:</span>{{ taskDetails.dueDate }}</p>
      <p><span>Priority:</span>{{ priorityConversion[taskDetails.priority] }}</p>
      <p><span>Created by:</span>{{ taskDetails.createdBy }}</p>
      <p><span>Last edited by:</span>{{ taskDetails.lastEditedBy }}</p>
    </div>
    <div class="comments">
      <p v-if="taskDetails.comments.length === 0">
        There are no comments yet
      </p>
      <div
        v-else
        class="comments-wrapper"
      >
        <div
          v-for="(comment, i) in taskDetails.comments"
          :key="i"
          class="comment"
        >
          <span>{{ comment.createdBy }}</span><span>{{ comment.createdAt }}</span>
          <p>{{ comment.comment }}</p>
        </div>
      </div>
    </div>
    <div class="comment-form-container">
      <div class="comment-form-wrapper">
        <div class="comment-form">
          <InputWrapper
            v-model.trim="commentForm.comment"
            :validator="$v.comment"
            placeholder="Write a comment..."
            type="input"
          />
          <SubmitButton
            text="Add comment"
            @click="submitForm"
          />
        </div>
      </div>
    </div>
  </ModalWrapper>
</template>

<script setup lang="ts">
  import ModalWrapper from '@/components/modal/ModalWrapper.vue';
  import { required } from '@vuelidate/validators';
  import { ref, watch } from 'vue';
  import taskController, {
    priorityConversion,
    type SaveCommentDto,
    type TaskDetailsDto,
    TaskPriority
  } from '@/api/controllers/taskController';
  import useVuelidate from '@vuelidate/core';
  import InputWrapper from '@/components/auth/InputWrapper.vue';
  import SubmitButton from '@/components/buttons/SubmitButton.vue';

  const emit = defineEmits<{
    (e: 'closed'): void
  }>();

  const props = defineProps<{
    show: boolean,
    taskId: number | null
  }>();

  watch(() => props.taskId, (taskId) => {
    if (props.show && taskId) {
      // loadTask(taskId);
    }
  });

  const taskDetails = ref<TaskDetailsDto>({
    id: -1,
    title: 'Some title',
    description: 'Ssadasdas dasjdnasjkdnas daskdnaskjdasj ddaskhdashdui asiduhasiudhasi udhasiudhasiuhdiuashd asiudhiasuhdiuashd asdasdasd asdasda sd asdasdas.',
    createdBy: 'b@mail.ru',
    lastEditedBy: 'a@mail.ru',
    dueDate: '22.02.1998',
    priority: TaskPriority.LOW,
    comments: []
  });

  const rules = {
    comment: { required }
  };

  const commentForm = ref<SaveCommentDto>({
    comment: ''
  });

  const $v = useVuelidate(rules, commentForm);

  async function loadTask(taskId: number) {
    const { data } = await taskController.taskDetails(taskId);
    taskDetails.value = data;
  }

  async function submitForm() {
    await $v.value.$validate().then(async(result) => {
      if (result && props.taskId) {
        await taskController.saveComment({ ...commentForm.value, taskId: props.taskId });
        await loadTask(props.taskId);
      }
    });
  }

  function closeModal() {
    commentForm.value.comment = '';
    $v.value.$reset();
    emit('closed');
  }
</script>

<style scoped lang="scss">
  * {
    font-size: 14px;
  }

  .details {
    margin-top: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #ccc;

    span {
      margin-right: 5px;
      font-weight: bold;
    }
  }

  .comments {
    margin-top: 20px;
    margin-bottom: 20px;

    .comments-wrapper {
      display: flex;
      flex-direction: column;
      gap: 15px;

      @media screen and (min-width: 550px) {
        max-height: 400px;
        overflow-y: scroll;
        scrollbar-width: none;
      }
    }
  }

  .comment-form-container {
    height: 80px;
    background: #e3e3e3;
    margin-left: -20px;
    margin-right: -20px;
    margin-bottom: -20px;

    .comment-form-wrapper {
      width: 100%;
      height: 100%;
      padding: 15px;

      .comment-form {
        display: flex;
        height: 40px;
        justify-content: space-between;
        gap: 15px;

        ::v-deep(.input-wrapper) {
          width: 70%;
          input {
            background: white;
          }
        }
      }
    }
  }
</style>
