<template>
  <FormLayout
    :error-text="errorText"
    :success-text="successText"
    :disable-submit-btn="disableSubmitBtn"
    title="Password reset"
    @submit="submitForm"
  >
    <InputWrapper
      v-model.trim="passwordResetForm.email"
      type="input"
      label="Your account's email"
      placeholder="Enter your email"
      :validator="$v.email"
    />
  </FormLayout>
</template>

<script setup lang="ts">
  import FormLayout from '@/components/auth/FormLayout.vue';
  import InputWrapper from '@/components/generic/InputWrapper.vue';
  import { ref } from 'vue';
  import useVuelidate from '@vuelidate/core';
  import { required } from '@vuelidate/validators';
  import passwordResetController, { type PasswordResetDto } from '@/api/controllers/passwordResetController';

  const emit = defineEmits<{ (e: 'open-tab', tab: number): void }>();

  const errorText = ref('');
  const successText = ref('');
  const disableSubmitBtn = ref(false);
  const passwordResetForm = ref<PasswordResetDto>({ email: '' });
  const $v = useVuelidate({ email: { required } }, passwordResetForm);

  async function submitForm() {
    await $v.value.$validate().then((result) => {
      if (result) initPasswordReset();
    });
  }

  async function initPasswordReset() {
    disableSubmitBtn.value = true;
    try {
      await passwordResetController.initPasswordReset(passwordResetForm.value);
      errorText.value = '';
      successText.value = 'Instructions have been sent to your email';
      setTimeout(() => emit('open-tab', 0), 2000);
    } catch (error: any) {
      errorText.value = error.response?.message;
      disableSubmitBtn.value = false;
    }
  }
</script>
