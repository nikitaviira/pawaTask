<template>
  <div class="form">
    <p
      v-if="title"
      class="title"
    >
      {{ title }}
    </p>
    <div
      v-if="errorText"
      class="alert error"
    >
      <AlertIcon />
      <p>{{ errorText }}</p>
    </div>
    <div
      v-if="successText"
      class="alert success"
    >
      <SuccessIcon />
      <p>{{ successText }}</p>
    </div>
    <slot />
    <button
      :disabled="disableSubmitBtn"
      :class="{'disabled': disableSubmitBtn}"
      class="form-button"
      @click="emit('submit')"
    >
      Submit
    </button>
  </div>
</template>

<script setup lang="ts">
  import AlertIcon from '@/components/icons/alert-icon.vue';
  import SuccessIcon from '@/components/icons/success-icon.vue';

  defineProps<{
    errorText?: string,
    successText?: string,
    title?: string,
    disableSubmitBtn?: boolean
  }>();
  const emit = defineEmits<{ (e: 'submit'): void }>();
</script>

<style scoped lang="scss">
  @import "@/assets/variables";

  .form {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;

    .title {
      font-weight: bold;
      font-size: 25px;
      margin-bottom: 30px;
    }

    .alert {
      width: 100%;
      display: flex;
      justify-content: center;
      align-items: center;
      gap: 5px;
      padding: 15px;
      font-weight: bold;
      font-size: 14px;
      color: white;
      border-radius: 10px;
      margin-bottom: 15px;

      &.error {
        background: $validation-error-color;
      }
      &.success {
        background: $success-color;
      }

      svg {
        fill: white;
      }
    }

    .form-button {
      margin-top: 15px;
      padding: 15px 60px;
      background: $main-color;
      color: white;
      font-weight: bold;
      text-transform: uppercase;
      border-radius: 15px;
      cursor: pointer;

      &.disabled {
        cursor: not-allowed;
        filter: brightness(80%);
      }

      &:not(.disabled):hover {
        filter: brightness(95%);
      }
    }

    @media screen and (max-width: 600px) {
      height: auto;
    }
  }
</style>
