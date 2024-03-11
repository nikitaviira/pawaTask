<template>
  <AuthLayout>
    <AuthForm
      title="Choose new password"
      :error-text="errorText"
      :success-text="successText"
      :disable-submit-btn="disableSubmitBtn"
      @submit="submitForm"
    >
      <InputWrapper
        v-model.trim="passwordResetForm.newPassword"
        type="input-password"
        label="Password"
        placeholder="Enter new password"
        :validator="$v.newPassword"
      />

      <InputWrapper
        v-model.trim="passwordResetForm.repeatedPassword"
        type="input-password"
        label="Repeat password"
        placeholder="Enter new password again"
        :validator="$v.repeatedPassword"
      />
    </AuthForm>
  </AuthLayout>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router';
  import passwordResetController, { type NewPasswordDto } from '@/api/controllers/passwordResetController';
  import { ref, toRef } from 'vue';
  import useVuelidate from '@vuelidate/core';
  import { maxLength, required } from '@vuelidate/validators';
  import { passwordValid, repeatedPasswordValid } from '@/components/validation/validation';
  import AuthForm from '@/components/auth/FormLayout.vue';
  import InputWrapper from '@/components/generic/InputWrapper.vue';
  import AuthLayout from '@/components/auth/AuthLayout.vue';

  interface PasswordResetDto extends NewPasswordDto {
    repeatedPassword: string;
  }

  const props = defineProps<{ otp: string }>();
  const router = useRouter();
  const errorText = ref('');
  const successText = ref('');
  const disableSubmitBtn = ref(false);
  const passwordResetForm = ref<PasswordResetDto>({
    newPassword: '',
    repeatedPassword: ''
  });

  const $v = useVuelidate({
    newPassword: {
      required,
      passwordValid,
      maxLength: maxLength(30)
    },
    repeatedPassword: {
      required,
      repeatedPasswordValid: repeatedPasswordValid(toRef(() => passwordResetForm.value.newPassword)),
      maxLength: maxLength(30)
    }
  }, passwordResetForm);

  async function submitForm() {
    await $v.value.$validate().then((result) => {
      if (result) saveNewPassword();
    });
  }

  async function saveNewPassword() {
    disableSubmitBtn.value = true;
    try {
      await passwordResetController.saveNewPassword(props.otp, passwordResetForm.value);
      errorText.value = '';
      successText.value = 'New password has been saved';
      setTimeout(() => router.push({ name: 'auth' }), 2000);
    } catch (error: any) {
      errorText.value = error.response?.data?.message;
      disableSubmitBtn.value = false;
    }
  }
</script>
