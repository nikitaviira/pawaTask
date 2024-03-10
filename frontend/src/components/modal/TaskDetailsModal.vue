<template>
  <ModalWrapper
    :show="show"
    :title="taskDetails.title"
    :loading="loading"
    @closed="closeModal"
  >
    <div class="description">
      {{ taskDetails.description }}
    </div>
    <div class="details">
      <p><span>Created on:</span>{{ taskDetails.createdAt }}</p>
      <p><span>Due date:</span>{{ taskDetails.dueDate }}</p>
      <p><span>Priority:</span>{{ priorityConversion[taskDetails.priority] }}</p>
      <p><span>Created by:</span>{{ taskDetails.createdBy }}</p>
      <p><span>Last edited by:</span>{{ taskDetails.lastEditedBy }}</p>
    </div>
    <div class="comment-section">
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
            <span class="username">{{ comment.createdBy }}</span><span class="date">on {{ comment.createdAt }}</span>
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
    </div>
  </ModalWrapper>
</template>

<script setup lang="ts">
  import ModalWrapper from '@/components/modal/ModalWrapper.vue';
  import { maxLength, required } from '@vuelidate/validators';
  import { ref, watch } from 'vue';
  import taskController, {
    priorityConversion,
    type SaveCommentDto,
    type TaskDetailsDto,
    TaskPriority
  } from '@/api/controllers/taskController';
  import useVuelidate from '@vuelidate/core';
  import InputWrapper from '@/components/generic/InputWrapper.vue';
  import SubmitButton from '@/components/buttons/SubmitButton.vue';

  const emit = defineEmits<{
    (e: 'closed'): void
  }>();

  const props = defineProps<{
    show: boolean,
    taskId: number | null
  }>();

  const loading = ref(true);
  const taskDetails = ref<TaskDetailsDto>({
    id: -1,
    title: '',
    description: '',
    createdBy: '',
    createdAt: '',
    lastEditedBy: '',
    dueDate: '',
    priority: TaskPriority.LOW,
    comments: []
  });

  watch(() => props.taskId, (taskId) => {
    if (props.show && taskId) {
      loadTask(taskId);
    }
  });

  const rules = {
    comment: {
      required,
      maxLength: maxLength(150)
    }
  };

  const commentForm = ref<SaveCommentDto>({
    comment: ''
  });

  const $v = useVuelidate(rules, commentForm);

  async function loadTask(taskId: number) {
    loading.value = true;
    const { data } = await taskController.taskDetails(taskId);
    taskDetails.value = data;
    loading.value = false;
  }

  async function submitForm() {
    await $v.value.$validate().then(async(result) => {
      if (result && props.taskId) {
        await taskController.saveComment({ ...commentForm.value, taskId: props.taskId });
        await loadTask(props.taskId);
        resetForm();
      }
    });
  }

  function closeModal() {
    resetForm();
    emit('closed');
  }

  function resetForm() {
    commentForm.value.comment = '';
    $v.value.$reset();
  }
</script>

<style scoped lang="scss">
  * {
    font-size: 14px;
  }

  .description {
    word-break: break-word;
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

  .comment-section {
    display: flex;
    flex-direction: column;

    @media screen and (max-width: 550px) {
      flex-direction: column-reverse;

      .comment-form-container {
        margin-top: 20px;
        margin-bottom: 20px !important;
      }

      .comments {
        margin-top: 0 !important;
        margin-bottom: 0 !important;
      }
    }

    .comments {
      margin-top: 20px;
      margin-bottom: 20px;

      .comments-wrapper {
        display: flex;
        flex-direction: column;
        gap: 15px;

        .comment {
          .username {
            font-weight: bold;
          }
          .date {
            margin-left: 5px;
            font-style: italic;
            color: grey;
          }
        }

        @media screen and (min-width: 550px) {
          max-height: 400px;
          overflow-y: scroll;
          scrollbar-width: thin;
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
            width: 100%;
            input {
              background: white;
            }
          }

          ::v-deep(.submit-btn) {
            min-width: 85px;
            font-size: 12px;
          }
        }
      }
    }
  }
</style>
